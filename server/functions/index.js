const { onRequest, onCall, HttpsError } = require("firebase-functions/v2/https");
const { onDocumentDeleted, onDocumentUpdated, onDocumentCreated } = require("firebase-functions/v2/firestore");
const { getFirestore } = require("firebase-admin/firestore");
const { getStorage } = require("firebase-admin/storage")
const { getAuth } = require("firebase-admin/auth")
const { initializeApp } = require("firebase-admin/app");
const logger = require("firebase-functions/logger");

initializeApp();
const db = getFirestore();
const auth = getAuth();
const storage = getStorage();

exports.updateRatingOnFeedbackDeletion = onDocumentDeleted("feedbacks/{feedbackId}", async (event) => {
    const snap = event.data;
    if (!snap) {
        logger.warn("No data in deleted snapshot.");
        return;
    }

    const deletedFeedback = snap.data();
    const postId = deletedFeedback.postId;
    const ratingValue = deletedFeedback.rating;

    if (!postId || ratingValue === undefined) {
        logger.warn("Deleted feedback is missing postId or rating.");
        return;
    }

    const postRef = db.collection("posts").doc(postId);

    try {
        await db.runTransaction(async (transaction) => {
            const postSnap = await transaction.get(postRef);

            if (!postSnap.exists) {
                throw new Error(`Post with ID ${postId} does not exist.`);
            }

            const postData = postSnap.data();
            const rating = postData.rating || {};
            const currentCount = rating.numberOfFeedbacks || 0;
            const currentAvg = rating.rating || 0;

            if (currentCount <= 0) {
                logger.warn(`Post ${postId} has no feedbacks to update.`);
                return;
            }

            const newCount = currentCount - 1;
            const newAvg = newCount > 0
                ? ((currentAvg * currentCount) - ratingValue) / newCount
                : 0;

            transaction.update(postRef, {
                "rating.numberOfFeedbacks": newCount,
                "rating.rating": newAvg
            });
        });

        logger.info(`Updated rating for post ${postId} after feedback deletion.`);
    } catch (error) {
        logger.error("Error updating post rating:", error);
    }
});

exports.updateRatingOnFeedbackCreation = onDocumentCreated("feedbacks/{feedbackId}", async (event) => {
    const snap = event.data;
    if (!snap) {
        logger.warn("No data in created snapshot.");
        return;
    }

    const createdFeedback = snap.data();
    const postId = createdFeedback.postId;
    const ratingValue = createdFeedback.rating;

    if (!postId || ratingValue === undefined) {
        logger.warn("Created feedback is missing postId or rating.");
        return;
    }

    const postRef = db.collection("posts").doc(postId);

    try {
        await db.runTransaction(async (transaction) => {
            const postSnap = await transaction.get(postRef);

            if (!postSnap.exists) {
                throw new Error(`Post with ID ${postId} does not exist.`);
            }

            const postData = postSnap.data();
            const rating = postData.rating || {};
            const currentCount = rating.numberOfFeedbacks || 0;
            const currentAvg = rating.rating || 0;

            const newCount = currentCount + 1;
            const newAvg = ((currentAvg * currentCount) + ratingValue) / newCount;

            transaction.update(postRef, {
                "rating.numberOfFeedbacks": newCount,
                "rating.rating": newAvg
            });
        });

        logger.info(`Updated rating for post ${postId} after feedback creation.`);
    } catch (error) {
        logger.error("Error updating post rating:", error);
    }
});

exports.updateUserNameReferences = onDocumentUpdated("users/{userId}", async (event) => {
    const beforeName = event.data.before.data().name;
    const afterName = event.data.after.data().name;
    const isProvider = event.data.before.data().isProvider;
    const userId = event.params.userId;

    if (beforeName === afterName) return null;

    const batch = db.batch();

    // Update in bookings collection
    const userIdField = isProvider ? "providerId" : "userId"
    const bookingsQuery = await db.collection("bookings")
        .where(userIdField, "==", userId)
        .get();
    bookingsQuery.forEach(doc => {
        const docRef = db.collection("bookings").doc(doc.id);
        if (isProvider) {
            batch.update(docRef, { provider: afterName });
        } else {
            batch.update(docRef, { username: afterName });
        }
    });

    if (!isProvider) {
        // Update customer in feedbacks collection
        const feedbacksQuery = await db.collection("feedbacks")
            .where("userId", "==", userId)
            .get();
        feedbacksQuery.forEach(doc => {
            const docRef = db.collection("feedbacks").doc(doc.id);
            batch.update(docRef, { username: afterName });
        });
    } else {
        // Update provider in posts collection
        const postsQuery = await db.collection("posts")
            .where("providerId", "==", userId)
            .get();

        for (const postDoc of postsSnapshot.docs) {
            const postDocRef = db.collection("posts").doc(postDoc.id);
            batch.update(postDocRef, { providerName: afterName });

            // Update provider in feedbacks collection
            const feedbacksQuery = await db.collection("feedbacks")
                .where("postId", "==", postDoc.id)
                .get();
            feedbacksQuery.forEach(doc => {
                const docRef = db.collection("feedbacks").doc(doc.id);
                batch.update(docRef, { provider: afterName });
            });
        }
    }

    logger.info(`Commiting batch for changing name of user with id ${userId}`);
    await batch.commit();
    logger.info(`Batch committed successfully for changing name of user with id ${userId}`);

    return null;
});

exports.onPostDeleted = onDocumentDeleted("posts/{postId}", async (event) => {
    const snap = event.data;
    const postId = event.params.postId;
    const postData = snap.data();
    const userId = postData.providerId;

    // Delete folder
    const folderPath = `${userId}/${postId}`;
    const [files] = await storage.bucket().getFiles({ prefix: folderPath });

    await Promise.all(files.map(file => {
        logger.info(`Deleting file: ${file.name}`);
        return file.delete();
    }));
    logger.info(`Files are deleted in folder ${folderPath}`);

    // Delete feedbacks
    const feedbacksSnapshot = await db.collection("feedbacks")
      .where("postId", "==", postId)
      .get();
    
    const batch = db.batch();
    feedbacksSnapshot.forEach(doc => {
        batch.delete(doc.ref);
    });

    logger.info(`Commiting batch for deleting feedbacks for post with id ${postId}`);
    await batch.commit();
    logger.info(`Batch committed successfully for deleting feedbacks for post with id ${postId}`);
});

exports.onUserDeleted = onDocumentDeleted("users/{userId}", async (event) => {
    const userId = event.params.userId;
    
    // Delete posts for user
    const postsSnapshot = await db.collection("posts")
      .where("providerId", "==", userId)
      .get();

    for (const postDoc of postsSnapshot.docs) {
      const postId = postDoc.id;
      await db.collection("posts").doc(postId).delete();
      logger.info(`Deleted post: ${postId}`);
    }

    // Delete feedbacks for user
    const userFeedbacks = await db.collection("feedbacks")
      .where("userId", "==", userId)
      .get();

    const feedbackBatch = db.batch();
    userFeedbacks.forEach(doc => {
      feedbackBatch.delete(doc.ref);
    });
    await feedbackBatch.commit();
    logger.info(`Deleted ${userFeedbacks.size} feedbacks left by user: ${userId}`);

    // Delete user folder
    const folderPath = `${userId}/`;
    const [files] = await storage.bucket().getFiles({ prefix: folderPath });

    await Promise.all(files.map(file => {
        logger.info(`Deleting file: ${file.name}`);
        return file.delete();
    }));
    logger.info(`Files are deleted in folder ${folderPath}`);

    // Delete user from Auth
    try {
        await auth.deleteUser(userId);
        logger.info(`User is deleted from Auth with userId: ${userId}`);
    } catch (error) {
        logger.error(`Error deleting user from Auth with id ${userId}:`, error);
    }
});

exports.createBookingWithLock = onCall(async (request, response) => {
    const data = request.data;
    const startDate = new Date(data.dateRange.startDate);
    const endDate = new Date(data.dateRange.endDate);

    const postRef = db.collection("posts").doc(data.postId);
    const bookingsRef = db.collection("bookings");

    await db.runTransaction(async (tx) => {
        const postSnap = await tx.get(postRef);
        if (!postSnap.exists) {
            logger.error(`Post not found with id: ${data.postId}`);
            throw new HttpsError("not-found", "Post not found");
        }

        const postData = postSnap.data();
        const notAvailableDates = postData.notAvailableDates || [];

        // Check overlap with unavailable ranges
        const overlapsWithUnavailable = notAvailableDates.some(range => {
            const start = new Date(range.startDate);
            const end = new Date(range.endDate);
            return startDate <= end && endDate >= start;
        });

        if (overlapsWithUnavailable) {
            logger.error(`Booking dates ${startDate}-${endDate} fall into unavailable range`);
            throw new HttpsError("already-exists", "Booking dates fall into unavailable range");
        }

        const bookings = await tx.get(
            bookingsRef.where("postId", "==", data.postId).where("status", "in", ["NOT_CONFIRMED", "CONFIRMED"])
        );
        
        // Check overlap with booked ranges
        const overlap = bookings.docs.some(doc => {
            const bookingData = doc.data();
            const existingStartDate = new Date(bookingData.dateRange.startDate);
            const existingEndDate = new Date(bookingData.dateRange.endDate);
            return startDate <= existingEndDate && endDate >= existingStartDate;
        });

        if (overlap) {
            logger.error(`Booking dates ${startDate}-${endDate} fall into booked range`);
            throw new HttpsError("already-exists", "Booking dates fall into booked range");
        }

        const ref = bookingsRef.doc();
        tx.set(ref, data);
        logger.info(`Booking is created with id: ${ref.id}`);
    });
});

exports.updateBookingDateRange = onCall(async (request, response) => {
    const data = request.data;
    const startDate = new Date(data.dateRange.startDate);
    const endDate = new Date(data.dateRange.endDate);

    const bookingsRef = db.collection("bookings");
    const bookingDocRef = bookingsRef.doc(data.id);

    await db.runTransaction(async (tx) => {
        const bookingSnap = await tx.get(bookingDocRef);
        if (!bookingSnap.exists) {
            logger.error(`Booking not found with id: ${data.id}`);
            throw new HttpsError("not-found", "Booking not found");
        }

        const bookingData = bookingSnap.data();

        const { postId } = bookingData;
        const postRef = db.collection("posts").doc(postId);
        const postSnap = await tx.get(postRef);
        if (!postSnap.exists) {
            logger.error(`Post not found with id: ${postId}`);
            throw new HttpsError("not-found", "Post not found");
        }

        const postData = postSnap.data();
        const notAvailableDates = postData.notAvailableDates || [];

        // Check overlap with unavailable ranges
        const overlapsWithUnavailable = notAvailableDates.some(range => {
            const start = new Date(range.startDate);
            const end = new Date(range.endDate);
            return startDate <= end && endDate >= start;
        });

        if (overlapsWithUnavailable) {
            logger.error(`Booking dates ${startDate}-${endDate} fall into unavailable range`);
            throw new HttpsError("already-exists", "Booking dates fall into unavailable range");
        }

        const bookings = await tx.get(
            bookingsRef.where("postId", "==", postId).where("status", "in", ["NOT_CONFIRMED", "CONFIRMED"])
        );
        
        // Check overlap with booked ranges
        const overlap = bookings.docs.some(doc => {
            if (doc.id === data.id) return false;
            const bookingData = doc.data();
            const existingStartDate = new Date(bookingData.dateRange.startDate);
            const existingEndDate = new Date(bookingData.dateRange.endDate);
            return startDate <= existingEndDate && endDate >= existingStartDate;
        });

        if (overlap) {
            logger.error(`Booking dates ${startDate}-${endDate} fall into booked range`);
            throw new HttpsError("already-exists", "Booking dates fall into booked range");
        }

        tx.update(bookingDocRef, {
            dateRange: data.dateRange,
            status: "NOT_CONFIRMED"
        });
        logger.info(`Booking dates are updated with id: ${bookingDocRef.id}`);
    });
});

exports.updateBookingStatus = onCall(async (request, response) => {
    const data = request.data;
    const { id, status } = data;

    const bookingsRef = db.collection("bookings");
    const bookingDocRef = bookingsRef.doc(id);

    await db.runTransaction(async (tx) => {
        const bookingSnap = await tx.get(bookingDocRef);
        if (!bookingSnap.exists) {
            logger.error(`Booking not found with id: ${id}`);
            throw new HttpsError("not-found", "Booking not found");
        }

        const bookingData = bookingSnap.data();

        const { postId } = bookingData;
        const postRef = db.collection("posts").doc(postId);
        const postSnap = await tx.get(postRef);
        if (!postSnap.exists) {
            logger.error(`Post not found with id: ${postId}`);
            throw new HttpsError("not-found", "Post not found");
        }

        if (bookingData.status in ["CANCELED", "SERVICE_PROVIDED"]) {
            logger.error(`Cannot update canceled/completed booking with id: ${id}`);
            throw new HttpsError("failed-precondition", "Cannot update canceled/completed booking");
        }
        
        tx.update(bookingDocRef, {
            status: status,
        });
        logger.info(`Booking status is updated with id: ${bookingDocRef.id}`);
    });
});
