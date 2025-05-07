package com.ivantrykosh.app.zeitzuheiraten.presenter.main.customer.budget_picker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivantrykosh.app.zeitzuheiraten.R
import com.ivantrykosh.app.zeitzuheiraten.presenter.main.ItemWithDropdownMenu
import com.ivantrykosh.app.zeitzuheiraten.utils.CategoryAndWeight
import kotlin.text.filter
import kotlin.text.isDigit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetPickerScreen(
    budgetPickerViewModel: BudgetPickerViewModel,
    navigateToPosts: () -> Unit,
) {
    val categories = stringArrayResource(R.array.categories)
    val weights = stringArrayResource(R.array.importance)
    var categoryValue by rememberSaveable { mutableStateOf("") }
    var categoryValueError by rememberSaveable { mutableStateOf(false) }
    var weightValue by rememberSaveable { mutableStateOf("") }
    var weightValueError by rememberSaveable { mutableStateOf(false) }

    val cityValue by budgetPickerViewModel.city.collectAsStateWithLifecycle()
    val cityValueError = stringResource(R.string.you_need_to_choose_city)
    val cities = stringArrayResource(R.array.cities)

    val budgetValue by budgetPickerViewModel.budget.collectAsStateWithLifecycle()
    val budgetValueError = stringResource(R.string.you_need_to_choose_budget)

    var showAddCategoryDialog by rememberSaveable { mutableStateOf(false) }
    val chosenCategories by budgetPickerViewModel.chosenCategories.collectAsStateWithLifecycle()
    val chosenCategoriesError = stringResource(R.string.you_need_to_choose_at_least_one_category)

    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var textInAlertDialog by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.budget_picker)) },
                windowInsets = WindowInsets(top = 0.dp),
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it).padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ItemWithDropdownMenu(
                    currentValue = cityValue,
                    onValueChange = { budgetPickerViewModel.updateCity(it) },
                    label = R.string.city,
                    values = cities.toList(),
                    maxDropdownMenuHeight = 300.dp,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.budget),
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextField(
                        value = budgetValue,
                        onValueChange = {
                            val digitsOnly = it.filter { it.isDigit() }.take(8)
                            budgetPickerViewModel.updateBudget(digitsOnly)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                        suffix = {
                            Text("₴")
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (chosenCategories.isNotEmpty()) {
                    chosenCategories.forEach {
                        CategoryAndWeightView(
                            categoryAndWeight = it,
                            onDeleteClicked = { budgetPickerViewModel.removeCategory(it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                if (chosenCategories.size < categories.size) {
                    Button(
                        onClick = {
                            showAddCategoryDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        border = BorderStroke(2.dp, color = Color.Black)
                    ) {
                        Text(
                            text = stringResource(R.string.add_category).uppercase(),
                            fontSize = 16.sp
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.weight(1f).padding(bottom = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    onClick = {
                        if (cityValue.isEmpty()) {
                            showAlertDialog = true
                            textInAlertDialog = cityValueError
                        } else if (budgetValue.isEmpty()) {
                            showAlertDialog = true
                            textInAlertDialog = budgetValueError
                        } else if (chosenCategories.isEmpty()) {
                            showAlertDialog = true
                            textInAlertDialog = chosenCategoriesError
                        } else {
                            navigateToPosts()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    border = BorderStroke(2.dp, color = Color.Black)
                ) {
                    Text(
                        text = stringResource(R.string.find_posts).uppercase(),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    if (showAddCategoryDialog) {
        val onDismiss = {
            showAddCategoryDialog = false
            categoryValue = ""
            weightValue = ""
            categoryValueError = false
            weightValueError = false
        }
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.add_category),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        ItemWithDropdownMenu(
                            currentValue = categoryValue,
                            onValueChange = { categoryValue = it },
                            label = R.string.category,
                            values = categories
                                .filter { category ->
                                    chosenCategories.none { category == it.category }
                                },
                            isError = categoryValueError,
                            maxDropdownMenuHeight = 300.dp,
                        )
                        Text(
                            text = if (categoryValueError) stringResource(R.string.field_is_required) else "",
                            color = Color.Red
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        ItemWithDropdownMenu(
                            currentValue = weightValue,
                            onValueChange = { weightValue = it },
                            label = R.string.importance,
                            values = weights.toList(),
                            isError = weightValueError,
                            maxDropdownMenuHeight = 300.dp,
                        )
                        Text(
                            text = if (weightValueError) stringResource(R.string.field_is_required) else "",
                            color = Color.Red
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(text = stringResource(R.string.cancel))
                        }
                        TextButton(
                            onClick = {
                                if (categoryValue.isEmpty()) {
                                    categoryValueError = true
                                } else if (weightValue.isEmpty()) {
                                    weightValueError = true
                                } else {
                                    budgetPickerViewModel.addCategory(
                                        CategoryAndWeight(
                                            category = categoryValue,
                                            weight = weightValue.toInt(),
                                        )
                                    )
                                    onDismiss()
                                }
                            }
                        ) {
                            Text(text = stringResource(R.string.ok_title))
                        }
                    }
                }
            }
        }
    }

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = { Text(text = stringResource(id = R.string.ok_title), modifier = Modifier.clickable { showAlertDialog = false }) },
            title = { Text(text = stringResource(id = R.string.error)) },
            text = { Text(text = textInAlertDialog) }
        )
    }
}

@Composable
fun CategoryAndWeightView(
    categoryAndWeight: CategoryAndWeight,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = categoryAndWeight.category,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = categoryAndWeight.weight.toString(),
                fontStyle = FontStyle.Italic,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onDeleteClicked
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_delete_24),
                    contentDescription = stringResource(R.string.delete_category)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun BudgetPickerScreenPreview() {
    BudgetPickerScreen(
        budgetPickerViewModel = hiltViewModel(),
        navigateToPosts = {}
    )
}