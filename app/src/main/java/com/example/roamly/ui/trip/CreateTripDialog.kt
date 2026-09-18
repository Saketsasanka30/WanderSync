package com.example.roamly.ui.trip

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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.roamly.model.TripCategory
import com.example.roamly.viewmodel.TripFormState
import java.time.LocalDate

@Composable
fun CreateTripDialog(
  formState: TripFormState,
  onTitleChanged: (String) -> Unit,
  onDestinationChanged: (String) -> Unit,
  onStartDateChanged: (String) -> Unit,
  onEndDateChanged: (String) -> Unit,
  onCategoryChanged: (TripCategory) -> Unit,
  onBudgetChanged: (String) -> Unit,
  onNotesChanged: (String) -> Unit,
  onSaveClicked: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = modifier.testTag("create_trip_dialog"),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.FlightTakeoff,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Plan New Trip",
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
        // Validation Error Banner if user attempted submission with invalid inputs
        if (formState.formSubmittedAttempted && !formState.isValid) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 12.dp)
              .testTag("validation_error_banner"),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            shape = RoundedCornerShape(10.dp)
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Validation Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Please complete all required fields correctly before saving.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        // Trip Title
        OutlinedTextField(
          value = formState.title,
          onValueChange = onTitleChanged,
          label = { Text("Trip Title *") },
          placeholder = { Text("e.g. Kyoto Autumn Explorer") },
          leadingIcon = {
            Icon(Icons.Default.Title, contentDescription = null)
          },
          isError = formState.titleError != null,
          supportingText = {
            if (formState.titleError != null) {
              Text(formState.titleError, color = MaterialTheme.colorScheme.error)
            } else {
              Text("Required (at least 3 characters)")
            }
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("trip_title_input")
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Destination
        OutlinedTextField(
          value = formState.destination,
          onValueChange = onDestinationChanged,
          label = { Text("Destination *") },
          placeholder = { Text("e.g. Kyoto, Japan") },
          leadingIcon = {
            Icon(Icons.Default.LocationOn, contentDescription = null)
          },
          isError = formState.destinationError != null,
          supportingText = {
            if (formState.destinationError != null) {
              Text(formState.destinationError, color = MaterialTheme.colorScheme.error)
            } else {
              Text("Required (city, country, or region)")
            }
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("trip_destination_input")
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Dates Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Start Date
          OutlinedTextField(
            value = formState.startDate,
            onValueChange = onStartDateChanged,
            label = { Text("Start Date *") },
            placeholder = { Text("YYYY-MM-DD") },
            leadingIcon = {
              Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            isError = formState.startDateError != null,
            supportingText = {
              if (formState.startDateError != null) {
                Text(formState.startDateError, color = MaterialTheme.colorScheme.error)
              } else {
                Text("YYYY-MM-DD")
              }
            },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("trip_start_date_input")
          )

          // End Date
          OutlinedTextField(
            value = formState.endDate,
            onValueChange = onEndDateChanged,
            label = { Text("End Date *") },
            placeholder = { Text("YYYY-MM-DD") },
            leadingIcon = {
              Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            isError = formState.endDateError != null,
            supportingText = {
              if (formState.endDateError != null) {
                Text(formState.endDateError, color = MaterialTheme.colorScheme.error)
              } else {
                Text("YYYY-MM-DD")
              }
            },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("trip_end_date_input")
          )
        }

        // Quick date duration presets
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          SuggestionChip(
            onClick = {
              try {
                val start = if (formState.startDate.isNotBlank()) LocalDate.parse(formState.startDate) else LocalDate.now()
                onStartDateChanged(start.toString())
                onEndDateChanged(start.plusDays(7).toString())
              } catch (_: Exception) {
                onStartDateChanged("2026-10-15")
                onEndDateChanged("2026-10-22")
              }
            },
            label = { Text("+7 Days") }
          )
          SuggestionChip(
            onClick = {
              try {
                val start = if (formState.startDate.isNotBlank()) LocalDate.parse(formState.startDate) else LocalDate.now()
                onStartDateChanged(start.toString())
                onEndDateChanged(start.plusDays(14).toString())
              } catch (_: Exception) {
                onStartDateChanged("2026-10-15")
                onEndDateChanged("2026-10-29")
              }
            },
            label = { Text("+14 Days") }
          )
          SuggestionChip(
            onClick = {
              try {
                val start = if (formState.startDate.isNotBlank()) LocalDate.parse(formState.startDate) else LocalDate.now()
                onStartDateChanged(start.toString())
                onEndDateChanged(start.plusMonths(1).toString())
              } catch (_: Exception) {
                onStartDateChanged("2026-10-15")
                onEndDateChanged("2026-11-15")
              }
            },
            label = { Text("+1 Month") }
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Total Budget Field
        OutlinedTextField(
          value = formState.totalBudget,
          onValueChange = onBudgetChanged,
          label = { Text("Total Budget *") },
          placeholder = { Text("e.g. 3500.00") },
          prefix = { Text("$ ") },
          leadingIcon = {
            Icon(Icons.Default.AttachMoney, contentDescription = null)
          },
          isError = formState.budgetError != null,
          supportingText = {
            if (formState.budgetError != null) {
              Text(formState.budgetError, color = MaterialTheme.colorScheme.error)
            } else {
              Text("Required (positive number greater than $0)")
            }
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("trip_budget_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Trip Category Selection
        Text(
          text = "Trip Category",
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
          TripCategory.values().forEach { cat ->
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
                selectedContainerColor = cat.accentColor.copy(alpha = 0.2f),
                selectedLabelColor = cat.accentColor
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Optional Notes
        OutlinedTextField(
          value = formState.notes,
          onValueChange = onNotesChanged,
          label = { Text("Trip Notes (Optional)") },
          placeholder = { Text("Key sights, packing notes, flight numbers...") },
          leadingIcon = {
            Icon(Icons.Default.Notes, contentDescription = null)
          },
          maxLines = 3,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("trip_notes_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onSaveClicked,
        modifier = Modifier.testTag("save_trip_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Save Trip", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("cancel_trip_button")
      ) {
        Text("Cancel")
      }
    }
  )
}
