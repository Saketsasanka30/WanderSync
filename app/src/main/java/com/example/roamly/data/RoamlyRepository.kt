package com.example.roamly.data

import com.example.roamly.model.Expense
import com.example.roamly.model.ExpenseCategory
import com.example.roamly.model.Trip
import com.example.roamly.model.TripCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RoamlyRepository {

  private val initialTrips = listOf(
    Trip(
      id = "trip-tokyo-2026",
      title = "Tokyo & Kyoto Explorer",
      destination = "Japan",
      startDate = "2026-10-14",
      endDate = "2026-10-24",
      category = TripCategory.CULTURAL,
      totalBudget = 3800.0,
      notes = "Autumn leaves festival, bullet train passes, ryokan booking, and tea ceremonies."
    ),
    Trip(
      id = "trip-amalfi-2026",
      title = "Amalfi Coast & Capri",
      destination = "Italy",
      startDate = "2026-11-05",
      endDate = "2026-11-12",
      category = TripCategory.LEISURE,
      totalBudget = 4200.0,
      notes = "Cliffside villas, boat tour to Capri, coastal hiking, and lemon groves."
    ),
    Trip(
      id = "trip-iceland-2026",
      title = "Iceland Ring Road Expedition",
      destination = "Iceland",
      startDate = "2026-12-01",
      endDate = "2026-12-10",
      category = TripCategory.ADVENTURE,
      totalBudget = 3200.0,
      notes = "4x4 campervan rental, glacier hikes, northern lights hunting, and hot springs."
    )
  )

  private val initialExpenses = listOf(
    Expense(
      id = "exp-1",
      tripId = "trip-tokyo-2026",
      title = "Roundtrip Flights (Haneda)",
      amount = 1150.0,
      category = ExpenseCategory.FLIGHTS,
      date = "2026-09-01",
      notes = "Direct flights booked early"
    ),
    Expense(
      id = "exp-2",
      tripId = "trip-tokyo-2026",
      title = "Kyoto Ryokan (3 nights)",
      amount = 680.0,
      category = ExpenseCategory.LODGING,
      date = "2026-09-10",
      notes = "Includes traditional Kaiseki dinners"
    ),
    Expense(
      id = "exp-3",
      tripId = "trip-tokyo-2026",
      title = "7-Day JR Rail Pass",
      amount = 340.0,
      category = ExpenseCategory.TRANSIT,
      date = "2026-09-15",
      notes = "Shinkansen travel between Tokyo and Kyoto"
    ),
    Expense(
      id = "exp-4",
      tripId = "trip-tokyo-2026",
      title = "TeamLab Planets Tickets",
      amount = 75.0,
      category = ExpenseCategory.ACTIVITIES,
      date = "2026-09-16",
      notes = "Morning slot reservation"
    ),
    Expense(
      id = "exp-5",
      tripId = "trip-amalfi-2026",
      title = "Naples Flights",
      amount = 890.0,
      category = ExpenseCategory.FLIGHTS,
      date = "2026-08-20"
    ),
    Expense(
      id = "exp-6",
      tripId = "trip-amalfi-2026",
      title = "Positano Cliff Hotel Deposit",
      amount = 1450.0,
      category = ExpenseCategory.LODGING,
      date = "2026-08-25"
    ),
    Expense(
      id = "exp-7",
      tripId = "trip-iceland-2026",
      title = "4x4 Camper Van Downpayment",
      amount = 1200.0,
      category = ExpenseCategory.TRANSIT,
      date = "2026-09-05"
    )
  )

  private val _trips = MutableStateFlow<List<Trip>>(initialTrips)
  val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

  private val _expenses = MutableStateFlow<List<Expense>>(initialExpenses)
  val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

  private val _isDarkMode = MutableStateFlow(false)
  val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

  fun addTrip(trip: Trip) {
    _trips.update { listOf(trip) + it }
  }

  fun deleteTrip(tripId: String) {
    _trips.update { list -> list.filterNot { it.id == tripId } }
    _expenses.update { list -> list.filterNot { it.tripId == tripId } }
  }

  fun addExpense(expense: Expense) {
    _expenses.update { listOf(expense) + it }
  }

  fun deleteExpense(expenseId: String) {
    _expenses.update { list -> list.filterNot { it.id == expenseId } }
  }

  fun toggleDarkMode() {
    _isDarkMode.update { !it }
  }

  fun setDarkMode(dark: Boolean) {
    _isDarkMode.value = dark
  }

  companion object {
    val instance by lazy { RoamlyRepository() }
  }
}
