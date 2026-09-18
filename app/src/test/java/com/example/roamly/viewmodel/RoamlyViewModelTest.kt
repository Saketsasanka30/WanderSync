package com.example.roamly.viewmodel

import com.example.roamly.data.RoamlyRepository
import com.example.roamly.model.ExpenseCategory
import com.example.roamly.model.TripCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoamlyViewModelTest {

  private val testDispatcher = UnconfinedTestDispatcher()
  private lateinit var repository: RoamlyRepository
  private lateinit var viewModel: RoamlyViewModel

  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    repository = RoamlyRepository()
    viewModel = RoamlyViewModel(repository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun tripValidation_emptyFieldsFail() = runTest {
    viewModel.openCreateTripDialog()
    viewModel.onTripTitleChanged("")
    viewModel.onTripDestinationChanged("")
    viewModel.onTripBudgetChanged("")

    val result = viewModel.submitCreateTrip()
    assertFalse("Submit should fail for empty fields", result)

    val form = viewModel.uiState.value.tripForm
    assertNotNull("Title error expected", form.titleError)
    assertNotNull("Destination error expected", form.destinationError)
    assertNotNull("Budget error expected", form.budgetError)
  }

  @Test
  fun tripValidation_validInputSucceeds() = runTest {
    viewModel.openCreateTripDialog()
    viewModel.onTripTitleChanged("Swiss Alps Hiking")
    viewModel.onTripDestinationChanged("Interlaken, Switzerland")
    viewModel.onTripStartDateChanged("2026-10-01")
    viewModel.onTripEndDateChanged("2026-10-10")
    viewModel.onTripCategoryChanged(TripCategory.ADVENTURE)
    viewModel.onTripBudgetChanged("2800.00")

    val success = viewModel.submitCreateTrip()
    assertTrue("Submit should succeed for valid inputs", success)

    val state = viewModel.uiState.value
    assertTrue(state.trips.any { it.title == "Swiss Alps Hiking" })
  }

  @Test
  fun budgetTracking_calculatesRemainingFundsAccurately() = runTest {
    val firstTrip = repository.trips.value.first()
    viewModel.selectTrip(firstTrip.id)

    val initialSummary = viewModel.uiState.value.selectedTripSummary
    assertNotNull(initialSummary)
    val initialRemaining = initialSummary!!.remainingFunds
    val initialSpent = initialSummary.totalSpent

    // Log an expense
    viewModel.openAddExpenseDialog(firstTrip.id)
    viewModel.onExpenseTitleChanged("Museum Entry")
    viewModel.onExpenseAmountChanged("50.00")
    viewModel.onExpenseCategoryChanged(ExpenseCategory.ACTIVITIES)
    viewModel.onExpenseDateChanged("2026-10-15")

    val success = viewModel.submitAddExpense()
    assertTrue(success)

    val updatedSummary = viewModel.uiState.value.selectedTripSummary
    assertNotNull(updatedSummary)
    assertEquals(initialSpent + 50.0, updatedSummary!!.totalSpent, 0.01)
    assertEquals(initialRemaining - 50.0, updatedSummary.remainingFunds, 0.01)
  }

  @Test
  fun themeToggle_switchesMode() = runTest {
    val initialDark = viewModel.uiState.value.isDarkMode
    viewModel.toggleTheme()
    assertEquals(!initialDark, viewModel.uiState.value.isDarkMode)
    viewModel.toggleTheme()
    assertEquals(initialDark, viewModel.uiState.value.isDarkMode)
  }
}
