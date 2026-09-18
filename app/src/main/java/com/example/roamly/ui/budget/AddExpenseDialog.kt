package com.example.roamly.ui.budget

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.roamly.model.ExpenseCategory
import com.example.roamly.viewmodel.ExpenseFormState

@Composable
fun AddExpenseDialog(
  formState: ExpenseFormState,
  onTitleChanged: (String) -> Unit,
  onAmountChanged: (String) -> Unit,
  onCategoryChanged: (ExpenseCategory) -> Unit,
  onDateChanged: (String) -> Unit,
  onNotesChanged: (String) -> Unit,
  onSaveClicked: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = modifier.testTag("add_expense_dialog"),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Receipt,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Log New Expense",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(scrollState)
      ) {
        // Validation Error Banner
        if (formState.submitAttempted && !formState.isValid) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 10.dp)
              .testTag("expense_validation_error_banner"),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            shape = RoundedCornerShape(10.dp)
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Please enter a valid expense description and positive amount.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
              )
            }
          }
        }

        // Expense Description
        OutlinedTextField(
          value = formState.title,
          onValueChange = onTitleChanged,
          label = { Text("Expense Description *") },
          placeholder = { Text("e.g. Shinkansen Bullet Train Ticket") },
          leadingIcon = {
            Icon(Icons.Default.Receipt, contentDescription = null)
          },
          isError = formState.titleError != null,
          supportingText = {
            if (formState.titleError != null) {
              Text(formState.titleError, color = MaterialTheme.colorScheme.error)
            } else {
              Text("Required (e.g. Dinner, Train, Museum pass)")
            }
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("expense_title_input")
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Expense Amount
        OutlinedTextField(
          value = formState.amount,
          onValueChange = onAmountChanged,
          label = { Text("Amount ($) *") },
          placeholder = { Text("0.00") },
          prefix = { Text("$ ") },
          leadingIcon = {
            Icon(Icons.Default.AttachMoney, contentDescription = null)
          },
          isError = formState.amountError != null,
          supportingText = {
            if (formState.amountError != null) {
              Text(formState.amountError, color = MaterialTheme.colorScheme.error)
            } else {
              Text("Required (positive dollar value)")
            }
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("expense_amount_input")
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Date
        OutlinedTextField(
          value = formState.date,
          onValueChange = onDateChanged,
          label = { Text("Date *") },
          placeholder = { Text("YYYY-MM-DD") },
          leadingIcon = {
            Icon(Icons.Default.CalendarMonth, contentDescription = null)
          },
          isError = formState.dateError != null,
          supportingText = {
            if (formState.dateError != null) {
              Text(formState.dateError, color = MaterialTheme.colorScheme.error)
            } else {
              Text("YYYY-MM-DD")
            }
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("expense_date_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Expense Category Selector
        Text(
          text = "Expense Category",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          ExpenseCategory.values().forEach { cat ->
            val isSelected = formState.category == cat
            FilterChip(
              selected = isSelected,
              onClick = { onCategoryChanged(cat) },
              label = { Text(cat.label) },
              leadingIcon = {
                if (isSelected) {
                  Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                } else {
                  Icon(cat.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                }
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = cat.color.copy(alpha = 0.2f),
                selectedLabelColor = cat.color
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Notes (optional)
        OutlinedTextField(
          value = formState.notes,
          onValueChange = onNotesChanged,
          label = { Text("Notes (Optional)") },
          placeholder = { Text("Payment method, split details, etc.") },
          leadingIcon = {
            Icon(Icons.Default.Description, contentDescription = null)
          },
          maxLines = 2,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("expense_notes_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onSaveClicked,
        modifier = Modifier.testTag("save_expense_button")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Add Expense", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("cancel_expense_button")
      ) {
        Text("Cancel")
      }
    }
  )
}
