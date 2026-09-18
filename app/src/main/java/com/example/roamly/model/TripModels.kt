package com.example.roamly.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

enum class TripCategory(val label: String, val icon: ImageVector, val accentColor: Color) {
  LEISURE("Leisure", Icons.Default.Landscape, Color(0xFF00897B)),
  ADVENTURE("Adventure", Icons.Default.Hiking, Color(0xFFF57C00)),
  CULTURAL("Cultural", Icons.Default.Museum, Color(0xFF8E24AA)),
  ROAD_TRIP("Road Trip", Icons.Default.DirectionsBus, Color(0xFF1E88E5)),
  BUSINESS("Business", Icons.Default.Work, Color(0xFF546E7A))
}

enum class ExpenseCategory(val label: String, val icon: ImageVector, val color: Color) {
  FLIGHTS("Flights", Icons.Default.Flight, Color(0xFF0288D1)),
  LODGING("Lodging", Icons.Default.Hotel, Color(0xFF7B1FA2)),
  FOOD("Food & Dining", Icons.Default.Restaurant, Color(0xFFE64A19)),
  ACTIVITIES("Activities", Icons.Default.LocalActivity, Color(0xFF388E3C)),
  TRANSIT("Transit", Icons.Default.DirectionsBus, Color(0xFF0097A7)),
  SHOPPING("Shopping", Icons.Default.ShoppingBag, Color(0xFFC2185B)),
  OTHER("Other", Icons.Default.Category, Color(0xFF616161))
}

enum class BudgetHealth {
  HEALTHY, // Spent < 75%
  WARNING, // 75% - 100%
  OVER_BUDGET // > 100%
}

data class Trip(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val destination: String,
  val startDate: String,
  val endDate: String,
  val category: TripCategory = TripCategory.LEISURE,
  val totalBudget: Double,
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

data class Expense(
  val id: String = UUID.randomUUID().toString(),
  val tripId: String,
  val title: String,
  val amount: Double,
  val category: ExpenseCategory,
  val date: String,
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

data class TripBudgetSummary(
  val trip: Trip,
  val totalSpent: Double,
  val remainingFunds: Double,
  val percentSpent: Float,
  val health: BudgetHealth,
  val expenseCount: Int,
  val categoryBreakdown: Map<ExpenseCategory, Double>
)
