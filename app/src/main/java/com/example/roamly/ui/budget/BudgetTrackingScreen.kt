package com.example.roamly.ui.budget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roamly.model.BudgetHealth
import com.example.roamly.model.Expense
import com.example.roamly.model.TripBudgetSummary
import com.example.roamly.theme.BudgetHealthyGreen
import com.example.roamly.theme.BudgetOverRed
import com.example.roamly.theme.BudgetWarningAmber
import com.example.roamly.ui.components.BudgetHealthBadge
import com.example.roamly.ui.components.BudgetProgressBar
import com.example.roamly.ui.components.TripCategoryBadge
import com.example.roamly.ui.components.formatCurrency
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTrackingScreen(
  summary: TripBudgetSummary,
  expenses: List<Expense>,
  onBackClicked: () -> Unit,
  onAddExpenseClicked: () -> Unit,
  onDeleteExpenseClicked: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val trip = summary.trip

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("budget_tracking_screen"),
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = trip.title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${trip.destination} • Budget Tracker",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBackClicked,
            modifier = Modifier.testTag("back_to_dashboard_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back to Trips Dashboard"
            )
          }
        },
        actions = {
          TripCategoryBadge(
            category = trip.category,
            modifier = Modifier.padding(end = 12.dp)
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = onAddExpenseClicked,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.testTag("add_expense_fab")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Expense")
          Spacer(modifier = Modifier.width(6.dp))
          Text("Log Expense", fontWeight = FontWeight.Bold)
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Summary of Remaining Funds Hero Card
      item {
        RemainingFundsHeroCard(
          summary = summary,
          modifier = Modifier.fillMaxWidth().testTag("remaining_funds_hero_card")
        )
      }

      // 2. Spending Breakdown by Category
      if (summary.categoryBreakdown.isNotEmpty()) {
        item {
          CategoryBreakdownCard(
            categoryBreakdown = summary.categoryBreakdown,
            totalBudget = trip.totalBudget,
            modifier = Modifier.fillMaxWidth().testTag("category_breakdown_card")
          )
        }
      }

      // 3. Expenses Section Header
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.ReceiptLong,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Expense History (${expenses.size})",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          Text(
            text = "Total: ${formatCurrency(summary.totalSpent)}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // 4. Expenses List or Empty State
      if (expenses.isEmpty()) {
        item {
          EmptyExpensesCard(
            onAddExpenseClicked = onAddExpenseClicked,
            modifier = Modifier.fillMaxWidth()
          )
        }
      } else {
        items(
          items = expenses,
          key = { it.id }
        ) { expense ->
          ExpenseItemCard(
            expense = expense,
            onDeleteClicked = { onDeleteExpenseClicked(expense.id) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("expense_item_${expense.id}")
          )
        }
      }

      // Bottom Spacer for FAB
      item {
        Spacer(modifier = Modifier.height(72.dp))
      }
    }
  }
}

@Composable
fun RemainingFundsHeroCard(
  summary: TripBudgetSummary,
  modifier: Modifier = Modifier
) {
  val remaining = summary.remainingFunds
  val isOver = remaining < 0
  val remainingColor = when (summary.health) {
    BudgetHealth.HEALTHY -> BudgetHealthyGreen
    BudgetHealth.WARNING -> BudgetWarningAmber
    BudgetHealth.OVER_BUDGET -> BudgetOverRed
  }

  Card(
    modifier = modifier,
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      // Header with Health Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Savings,
            contentDescription = null,
            tint = remainingColor,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isOver) "Budget Deficit" else "Remaining Funds",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        BudgetHealthBadge(health = summary.health)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Big Remaining Funds Text
      Text(
        text = if (isOver) "-${formatCurrency(abs(remaining))}" else formatCurrency(remaining),
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.ExtraBold,
        color = remainingColor,
        modifier = Modifier.testTag("remaining_funds_amount")
      )

      Text(
        text = if (isOver) {
          "You are ${formatCurrency(abs(remaining))} over the allocated budget of ${formatCurrency(summary.trip.totalBudget)}."
        } else {
          "Available to spend across the remainder of your trip."
        },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
      )

      // Budget Progress Bar
      BudgetProgressBar(
        spent = summary.totalSpent,
        total = summary.trip.totalBudget,
        health = summary.health
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Metrics Breakdown Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "Total Budget",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = formatCurrency(summary.trip.totalBudget),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Total Spent",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = formatCurrency(summary.totalSpent),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = remainingColor
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "Budget Used",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          val percent = (summary.percentSpent * 100).toInt()
          Text(
            text = "$percent%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun CategoryBreakdownCard(
  categoryBreakdown: Map<com.example.roamly.model.ExpenseCategory, Double>,
  totalBudget: Double,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp)
      ) {
        Icon(
          imageVector = Icons.Default.PieChart,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Spending by Category",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      val sorted = categoryBreakdown.entries.sortedByDescending { it.value }
      val totalSpent = categoryBreakdown.values.sum().coerceAtLeast(1.0)

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        sorted.forEach { (cat, amount) ->
          val shareOfSpent = (amount / totalSpent).toFloat().coerceIn(0f, 1f)
          val percentText = (shareOfSpent * 100).toInt()

          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .background(cat.color.copy(alpha = 0.15f), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = cat.icon,
                    contentDescription = null,
                    tint = cat.color,
                    modifier = Modifier.size(15.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = cat.label,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Medium
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = formatCurrency(amount),
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "($percentText%)",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Spacer(modifier = Modifier.height(4.dp))
            androidx.compose.material3.LinearProgressIndicator(
              progress = { shareOfSpent },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = cat.color,
              trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
          }
        }
      }
    }
  }
}

@Composable
fun ExpenseItemCard(
  expense: Expense,
  onDeleteClicked: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Category Icon
      Box(
        modifier = Modifier
          .size(42.dp)
          .background(expense.category.color.copy(alpha = 0.15f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = expense.category.icon,
          contentDescription = expense.category.label,
          tint = expense.category.color,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Expense info
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = expense.title,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(top = 2.dp)
        ) {
          Text(
            text = expense.category.label,
            style = MaterialTheme.typography.bodySmall,
            color = expense.category.color,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = " • ${expense.date}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        if (expense.notes.isNotBlank()) {
          Text(
            text = expense.notes,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp)
          )
        }
      }

      // Amount & Delete
      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = formatCurrency(expense.amount),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(
          onClick = onDeleteClicked,
          modifier = Modifier.size(32.dp).testTag("delete_expense_${expense.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete expense",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
fun EmptyExpensesCard(
  onAddExpenseClicked: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(28.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(56.dp)
          .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.MonetizationOn,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(28.dp)
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "No expenses logged yet",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Record flights, stays, food, or activities to monitor your remaining funds.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
    }
  }
}
