package com.example.roamly.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roamly.theme.RoamlyTheme
import com.example.roamly.ui.budget.AddExpenseDialog
import com.example.roamly.ui.budget.BudgetTrackingScreen
import com.example.roamly.ui.dashboard.TripDashboardScreen
import com.example.roamly.ui.trip.CreateTripDialog
import com.example.roamly.viewmodel.RoamlyViewModel

@Composable
fun MainApp(
  viewModel: RoamlyViewModel = viewModel(),
  modifier: Modifier = Modifier
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  RoamlyTheme(darkTheme = state.isDarkMode) {
    Surface(
      modifier = modifier.fillMaxSize(),
      color = MaterialTheme.colorScheme.background
    ) {
      AnimatedContent(
        targetState = state.selectedTripSummary,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition"
      ) { tripSummary ->
        if (tripSummary != null) {
          BackHandler {
            viewModel.selectTrip(null)
          }

          BudgetTrackingScreen(
            summary = tripSummary,
            expenses = state.selectedTripExpenses,
            onBackClicked = { viewModel.selectTrip(null) },
            onAddExpenseClicked = { viewModel.openAddExpenseDialog(tripSummary.trip.id) },
            onDeleteExpenseClicked = { expenseId -> viewModel.deleteExpense(expenseId) },
            modifier = Modifier.safeDrawingPadding()
          )
        } else {
          TripDashboardScreen(
            state = state,
            onThemeToggleClicked = { viewModel.toggleTheme() },
            onTripClicked = { tripId -> viewModel.selectTrip(tripId) },
            onAddTripClicked = { viewModel.openCreateTripDialog() },
            onQuickAddExpenseClicked = { tripId -> viewModel.openAddExpenseDialog(tripId) },
            onDeleteTripClicked = { tripId -> viewModel.deleteTrip(tripId) },
            onSearchQueryChanged = { query -> viewModel.onSearchQueryChanged(query) },
            onCategoryFilterSelected = { cat -> viewModel.onCategoryFilterSelected(cat) },
            onSnackbarDismissed = { viewModel.clearSnackbarMessage() },
            modifier = Modifier.safeDrawingPadding()
          )
        }
      }

      // Dialog: Trip Creation with Basic Input Validation
      if (state.isCreateTripDialogOpen) {
        CreateTripDialog(
          formState = state.tripForm,
          onTitleChanged = { viewModel.onTripTitleChanged(it) },
          onDestinationChanged = { viewModel.onTripDestinationChanged(it) },
          onStartDateChanged = { viewModel.onTripStartDateChanged(it) },
          onEndDateChanged = { viewModel.onTripEndDateChanged(it) },
          onCategoryChanged = { viewModel.onTripCategoryChanged(it) },
          onBudgetChanged = { viewModel.onTripBudgetChanged(it) },
          onNotesChanged = { viewModel.onTripNotesChanged(it) },
          onSaveClicked = { viewModel.submitCreateTrip() },
          onDismiss = { viewModel.dismissCreateTripDialog() }
        )
      }

      // Dialog: Add Expense with Input Validation
      if (state.isAddExpenseDialogOpen) {
        AddExpenseDialog(
          formState = state.expenseForm,
          onTitleChanged = { viewModel.onExpenseTitleChanged(it) },
          onAmountChanged = { viewModel.onExpenseAmountChanged(it) },
          onCategoryChanged = { viewModel.onExpenseCategoryChanged(it) },
          onDateChanged = { viewModel.onExpenseDateChanged(it) },
          onNotesChanged = { viewModel.onExpenseNotesChanged(it) },
          onSaveClicked = { viewModel.submitAddExpense() },
          onDismiss = { viewModel.dismissAddExpenseDialog() }
        )
      }
    }
  }
}
