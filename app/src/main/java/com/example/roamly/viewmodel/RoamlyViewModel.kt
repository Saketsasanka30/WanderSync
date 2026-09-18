package com.example.roamly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roamly.data.RoamlyRepository
import com.example.roamly.model.BudgetHealth
import com.example.roamly.model.Expense
import com.example.roamly.model.ExpenseCategory
import com.example.roamly.model.Trip
import com.example.roamly.model.TripBudgetSummary
import com.example.roamly.model.TripCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeParseException

data class TripFormState(
  val title: String = "",
  val destination: String = "",
  val startDate: String = "",
  val endDate: String = "",
  val category: TripCategory = TripCategory.LEISURE,
  val totalBudget: String = "",
  val notes: String = "",
  val titleError: String? = null,
  val destinationError: String? = null,
  val startDateError: String? = null,
  val endDateError: String? = null,
  val budgetError: String? = null,
  val formSubmittedAttempted: Boolean = false
) {
  val isValid: Boolean
    get() = titleError == null &&
      destinationError == null &&
      startDateError == null &&
      endDateError == null &&
      budgetError == null &&
      title.isNotBlank() &&
      destination.isNotBlank() &&
      startDate.isNotBlank() &&
      endDate.isNotBlank() &&
      totalBudget.isNotBlank()
}

data class ExpenseFormState(
  val title: String = "",
  val amount: String = "",
  val category: ExpenseCategory = ExpenseCategory.FOOD,
  val date: String = "",
  val notes: String = "",
  val titleError: String? = null,
  val amountError: String? = null,
  val dateError: String? = null,
  val submitAttempted: Boolean = false
) {
  val isValid: Boolean
    get() = titleError == null &&
      amountError == null &&
      dateError == null &&
      title.isNotBlank() &&
      amount.isNotBlank() &&
      date.isNotBlank()
}

data class DashboardUiState(
  val trips: List<Trip> = emptyList(),
  val tripSummaries: List<TripBudgetSummary> = emptyList(),
  val filteredSummaries: List<TripBudgetSummary> = emptyList(),
  val selectedTripSummary: TripBudgetSummary? = null,
  val selectedTripExpenses: List<Expense> = emptyList(),
  val totalTripsCount: Int = 0,
  val totalBudgetOverall: Double = 0.0,
  val totalSpentOverall: Double = 0.0,
  val totalRemainingOverall: Double = 0.0,
  val isDarkMode: Boolean = false,
  val searchQuery: String = "",
  val selectedCategoryFilter: TripCategory? = null,
  val selectedTripId: String? = null,
  val isCreateTripDialogOpen: Boolean = false,
  val isAddExpenseDialogOpen: Boolean = false,
  val tripForm: TripFormState = TripFormState(),
  val expenseForm: ExpenseFormState = ExpenseFormState(),
  val snackbarMessage: String? = null
)

class RoamlyViewModel(
  private val repository: RoamlyRepository = RoamlyRepository.instance
) : ViewModel() {

  private val _searchQuery = MutableStateFlow("")
  private val _selectedCategoryFilter = MutableStateFlow<TripCategory?>(null)
  private val _selectedTripId = MutableStateFlow<String?>(null)
  private val _isCreateTripDialogOpen = MutableStateFlow(false)
  private val _isAddExpenseDialogOpen = MutableStateFlow(false)
  private val _tripForm = MutableStateFlow(TripFormState())
  private val _expenseForm = MutableStateFlow(ExpenseFormState())
  private val _snackbarMessage = MutableStateFlow<String?>(null)

  val uiState: StateFlow<DashboardUiState> =
    combine(
      repository.trips,
      repository.expenses,
      repository.isDarkMode,
      _searchQuery,
      _selectedCategoryFilter,
      _selectedTripId,
      _isCreateTripDialogOpen,
      _isAddExpenseDialogOpen,
      _tripForm,
      _expenseForm,
      _snackbarMessage
    ) { args: Array<Any?> ->
      @Suppress("UNCHECKED_CAST")
      val trips = args[0] as List<Trip>
      @Suppress("UNCHECKED_CAST")
      val expenses = args[1] as List<Expense>
      val isDarkMode = args[2] as Boolean
      val query = args[3] as String
      val categoryFilter = args[4] as TripCategory?
      val selectedTripId = args[5] as String?
      val isCreateDialogOpen = args[6] as Boolean
      val isAddExpenseOpen = args[7] as Boolean
      val tripForm = args[8] as TripFormState
      val expenseForm = args[9] as ExpenseFormState
      val snackbarMsg = args[10] as String?

      // Compute summaries for each trip
      val summaries = trips.map { trip ->
        val tripExpenses = expenses.filter { it.tripId == trip.id }
        val spent = tripExpenses.sumOf { it.amount }
        val remaining = trip.totalBudget - spent
        val ratio = if (trip.totalBudget > 0) (spent / trip.totalBudget).toFloat() else 0f
        val health = when {
          spent > trip.totalBudget -> BudgetHealth.OVER_BUDGET
          ratio >= 0.75f -> BudgetHealth.WARNING
          else -> BudgetHealth.HEALTHY
        }
        val breakdown = tripExpenses
          .groupBy { it.category }
          .mapValues { entry -> entry.value.sumOf { it.amount } }

        TripBudgetSummary(
          trip = trip,
          totalSpent = spent,
          remainingFunds = remaining,
          percentSpent = ratio,
          health = health,
          expenseCount = tripExpenses.size,
          categoryBreakdown = breakdown
        )
      }

      // Filter summaries
      val filtered = summaries.filter { summary ->
        val matchesQuery = query.isBlank() ||
          summary.trip.title.contains(query, ignoreCase = true) ||
          summary.trip.destination.contains(query, ignoreCase = true)
        val matchesCategory = categoryFilter == null || summary.trip.category == categoryFilter
        matchesQuery && matchesCategory
      }

      val selectedSummary = summaries.find { it.trip.id == selectedTripId }
      val selectedExpenses = expenses.filter { it.tripId == selectedTripId }

      val totalBudget = summaries.sumOf { it.trip.totalBudget }
      val totalSpent = summaries.sumOf { it.totalSpent }
      val totalRemaining = totalBudget - totalSpent

      DashboardUiState(
        trips = trips,
        tripSummaries = summaries,
        filteredSummaries = filtered,
        selectedTripSummary = selectedSummary,
        selectedTripExpenses = selectedExpenses,
        totalTripsCount = trips.size,
        totalBudgetOverall = totalBudget,
        totalSpentOverall = totalSpent,
        totalRemainingOverall = totalRemaining,
        isDarkMode = isDarkMode,
        searchQuery = query,
        selectedCategoryFilter = categoryFilter,
        selectedTripId = selectedTripId,
        isCreateTripDialogOpen = isCreateDialogOpen,
        isAddExpenseDialogOpen = isAddExpenseOpen,
        tripForm = tripForm,
        expenseForm = expenseForm,
        snackbarMessage = snackbarMsg
      )
    }.stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      DashboardUiState()
    )

  // Global Theme Toggle
  fun toggleTheme() {
    repository.toggleDarkMode()
  }

  fun setDarkMode(dark: Boolean) {
    repository.setDarkMode(dark)
  }

  // Search & Filter
  fun onSearchQueryChanged(query: String) {
    _searchQuery.value = query
  }

  fun onCategoryFilterSelected(category: TripCategory?) {
    _selectedCategoryFilter.value = if (_selectedCategoryFilter.value == category) null else category
  }

  // Navigation / Selection
  fun selectTrip(tripId: String?) {
    _selectedTripId.value = tripId
  }

  fun clearSnackbarMessage() {
    _snackbarMessage.value = null
  }

  // Trip Creation & Input Validation
  fun openCreateTripDialog() {
    // Initialize with suggested dates (e.g. today + 1 month)
    val today = try {
      LocalDate.now()
    } catch (e: Throwable) {
      null
    }
    val defaultStart = today?.plusWeeks(2)?.toString() ?: "2026-10-15"
    val defaultEnd = today?.plusWeeks(3)?.toString() ?: "2026-10-25"

    _tripForm.value = TripFormState(
      startDate = defaultStart,
      endDate = defaultEnd
    )
    _isCreateTripDialogOpen.value = true
  }

  fun dismissCreateTripDialog() {
    _isCreateTripDialogOpen.value = false
    _tripForm.value = TripFormState()
  }

  fun onTripTitleChanged(title: String) {
    _tripForm.update { state ->
      val error = if (state.formSubmittedAttempted) validateTitle(title) else null
      state.copy(title = title, titleError = error)
    }
  }

  fun onTripDestinationChanged(destination: String) {
    _tripForm.update { state ->
      val error = if (state.formSubmittedAttempted) validateDestination(destination) else null
      state.copy(destination = destination, destinationError = error)
    }
  }

  fun onTripStartDateChanged(startDate: String) {
    _tripForm.update { state ->
      val (startErr, endErr) = if (state.formSubmittedAttempted) {
        Pair(validateStartDate(startDate), validateEndDate(startDate, state.endDate))
      } else {
        Pair(null, state.endDateError)
      }
      state.copy(startDate = startDate, startDateError = startErr, endDateError = endErr)
    }
  }

  fun onTripEndDateChanged(endDate: String) {
    _tripForm.update { state ->
      val error = if (state.formSubmittedAttempted) validateEndDate(state.startDate, endDate) else null
      state.copy(endDate = endDate, endDateError = error)
    }
  }

  fun onTripCategoryChanged(category: TripCategory) {
    _tripForm.update { it.copy(category = category) }
  }

  fun onTripBudgetChanged(budget: String) {
    _tripForm.update { state ->
      val error = if (state.formSubmittedAttempted) validateBudget(budget) else null
      state.copy(totalBudget = budget, budgetError = error)
    }
  }

  fun onTripNotesChanged(notes: String) {
    _tripForm.update { it.copy(notes = notes) }
  }

  /**
   * Basic input validation for the trip creation form.
   * Ensures all required fields are properly filled out before saving.
   */
  fun submitCreateTrip(): Boolean {
    val current = _tripForm.value
    val titleErr = validateTitle(current.title)
    val destErr = validateDestination(current.destination)
    val startErr = validateStartDate(current.startDate)
    val endErr = validateEndDate(current.startDate, current.endDate)
    val budgetErr = validateBudget(current.totalBudget)

    val hasErrors = titleErr != null || destErr != null || startErr != null || endErr != null || budgetErr != null

    if (hasErrors) {
      _tripForm.update {
        it.copy(
          titleError = titleErr,
          destinationError = destErr,
          startDateError = startErr,
          endDateError = endErr,
          budgetError = budgetErr,
          formSubmittedAttempted = true
        )
      }
      return false
    }

    // All fields valid: construct Trip and save
    val budgetValue = current.totalBudget.toDoubleOrNull() ?: 0.0
    val newTrip = Trip(
      title = current.title.trim(),
      destination = current.destination.trim(),
      startDate = current.startDate.trim(),
      endDate = current.endDate.trim(),
      category = current.category,
      totalBudget = budgetValue,
      notes = current.notes.trim()
    )

    repository.addTrip(newTrip)
    _isCreateTripDialogOpen.value = false
    _tripForm.value = TripFormState()
    _snackbarMessage.value = "Trip \"${newTrip.title}\" created successfully!"
    return true
  }

  fun deleteTrip(tripId: String) {
    val trip = repository.trips.value.find { it.id == tripId }
    repository.deleteTrip(tripId)
    if (_selectedTripId.value == tripId) {
      _selectedTripId.value = null
    }
    _snackbarMessage.value = "Trip \"${trip?.title ?: "Trip"}\" deleted."
  }

  // Expense Input Validation & Budget Tracking
  fun openAddExpenseDialog(tripId: String) {
    val today = try {
      LocalDate.now().toString()
    } catch (e: Throwable) {
      "2026-09-17"
    }

    _expenseForm.value = ExpenseFormState(date = today)
    _selectedTripId.value = tripId
    _isAddExpenseDialogOpen.value = true
  }

  fun dismissAddExpenseDialog() {
    _isAddExpenseDialogOpen.value = false
    _expenseForm.value = ExpenseFormState()
  }

  fun onExpenseTitleChanged(title: String) {
    _expenseForm.update { state ->
      val error = if (state.submitAttempted) validateExpenseTitle(title) else null
      state.copy(title = title, titleError = error)
    }
  }

  fun onExpenseAmountChanged(amount: String) {
    _expenseForm.update { state ->
      val error = if (state.submitAttempted) validateExpenseAmount(amount) else null
      state.copy(amount = amount, amountError = error)
    }
  }

  fun onExpenseCategoryChanged(category: ExpenseCategory) {
    _expenseForm.update { it.copy(category = category) }
  }

  fun onExpenseDateChanged(date: String) {
    _expenseForm.update { state ->
      val error = if (state.submitAttempted) validateExpenseDate(date) else null
      state.copy(date = date, dateError = error)
    }
  }

  fun onExpenseNotesChanged(notes: String) {
    _expenseForm.update { it.copy(notes = notes) }
  }

  /**
   * Validates expense fields and saves expense to update remaining funds
   */
  fun submitAddExpense(): Boolean {
    val current = _expenseForm.value
    val tripId = _selectedTripId.value ?: return false

    val titleErr = validateExpenseTitle(current.title)
    val amountErr = validateExpenseAmount(current.amount)
    val dateErr = validateExpenseDate(current.date)

    if (titleErr != null || amountErr != null || dateErr != null) {
      _expenseForm.update {
        it.copy(
          titleError = titleErr,
          amountError = amountErr,
          dateError = dateErr,
          submitAttempted = true
        )
      }
      return false
    }

    val amountValue = current.amount.toDoubleOrNull() ?: 0.0
    val newExpense = Expense(
      tripId = tripId,
      title = current.title.trim(),
      amount = amountValue,
      category = current.category,
      date = current.date.trim(),
      notes = current.notes.trim()
    )

    repository.addExpense(newExpense)
    _isAddExpenseDialogOpen.value = false
    _expenseForm.value = ExpenseFormState()
    _snackbarMessage.value = "Added expense: $${"%.2f".format(amountValue)} for ${newExpense.title}"
    return true
  }

  fun deleteExpense(expenseId: String) {
    repository.deleteExpense(expenseId)
    _snackbarMessage.value = "Expense removed."
  }

  // --- Validation Helpers ---
  private fun validateTitle(title: String): String? {
    val trimmed = title.trim()
    return when {
      trimmed.isEmpty() -> "Trip title is required."
      trimmed.length < 3 -> "Trip title must be at least 3 characters."
      else -> null
    }
  }

  private fun validateDestination(destination: String): String? {
    val trimmed = destination.trim()
    return when {
      trimmed.isEmpty() -> "Destination is required."
      trimmed.length < 2 -> "Please specify a valid destination."
      else -> null
    }
  }

  private fun validateStartDate(date: String): String? {
    val trimmed = date.trim()
    if (trimmed.isEmpty()) return "Start date is required."
    return tryParseDate(trimmed)
  }

  private fun validateEndDate(startDate: String, endDate: String): String? {
    val trimmed = endDate.trim()
    if (trimmed.isEmpty()) return "End date is required."
    val formatErr = tryParseDate(trimmed)
    if (formatErr != null) return formatErr

    // Compare dates if both parseable
    try {
      val start = LocalDate.parse(startDate.trim())
      val end = LocalDate.parse(trimmed)
      if (end.isBefore(start)) {
        return "End date cannot be before start date."
      }
    } catch (_: Exception) {
      // If not strict ISO, fallback to basic non-empty check
    }
    return null
  }

  private fun tryParseDate(date: String): String? {
    return try {
      LocalDate.parse(date)
      null
    } catch (e: DateTimeParseException) {
      // If user typed MM/DD/YYYY or similar, ensure it has sensible length
      if (date.length >= 8 && (date.contains("-") || date.contains("/"))) {
        null
      } else {
        "Please use YYYY-MM-DD format (e.g. 2026-10-14)."
      }
    }
  }

  private fun validateBudget(budgetStr: String): String? {
    val trimmed = budgetStr.trim().replace("$", "").replace(",", "")
    if (trimmed.isEmpty()) return "Total budget is required."
    val value = trimmed.toDoubleOrNull()
    return when {
      value == null -> "Please enter a valid numeric budget."
      value <= 0.0 -> "Budget must be greater than $0."
      value > 10_000_000 -> "Budget cannot exceed $10,000,000."
      else -> null
    }
  }

  private fun validateExpenseTitle(title: String): String? {
    val trimmed = title.trim()
    return when {
      trimmed.isEmpty() -> "Expense description is required."
      trimmed.length < 2 -> "Description must be at least 2 characters."
      else -> null
    }
  }

  private fun validateExpenseAmount(amountStr: String): String? {
    val trimmed = amountStr.trim().replace("$", "").replace(",", "")
    if (trimmed.isEmpty()) return "Amount is required."
    val value = trimmed.toDoubleOrNull()
    return when {
      value == null -> "Please enter a valid number."
      value <= 0.0 -> "Amount must be greater than $0."
      value > 1_000_000 -> "Amount cannot exceed $1,000,000."
      else -> null
    }
  }

  private fun validateExpenseDate(date: String): String? {
    val trimmed = date.trim()
    return if (trimmed.isEmpty()) "Date is required." else null
  }
}
