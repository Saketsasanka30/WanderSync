package com.example.roamly.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.roamly.model.TripBudgetSummary
import com.example.roamly.model.TripCategory
import com.example.roamly.theme.BudgetHealthyGreen
import com.example.roamly.theme.BudgetOverRed
import com.example.roamly.theme.BudgetWarningAmber
import com.example.roamly.ui.components.BudgetHealthBadge
import com.example.roamly.ui.components.BudgetProgressBar
import com.example.roamly.ui.components.StatCard
import com.example.roamly.ui.components.TripCategoryBadge
import com.example.roamly.ui.components.formatCurrency
import com.example.roamly.viewmodel.DashboardUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDashboardScreen(
  state: DashboardUiState,
  onThemeToggleClicked: () -> Unit,
  onTripClicked: (String) -> Unit,
  onAddTripClicked: () -> Unit,
  onQuickAddExpenseClicked: (String) -> Unit,
  onDeleteTripClicked: (String) -> Unit,
  onSearchQueryChanged: (String) -> Unit,
  onCategoryFilterSelected: (TripCategory?) -> Unit,
  onSnackbarDismissed: () -> Unit,
  modifier: Modifier = Modifier
) {
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(state.snackbarMessage) {
    state.snackbarMessage?.let { message ->
      snackbarHostState.showSnackbar(message)
      onSnackbarDismissed()
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("trip_dashboard_screen"),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = "Roamly Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Roamly",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
              )
              Text(
                text = "Trip Dashboard & Budget Tracker",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        actions = {
          // Global Theme Toggle
          IconButton(
            onClick = onThemeToggleClicked,
            modifier = Modifier.testTag("theme_toggle_button")
          ) {
            Icon(
              imageVector = if (state.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = if (state.isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = onAddTripClicked,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.testTag("add_trip_fab")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Trip")
          Spacer(modifier = Modifier.width(6.dp))
          Text("Plan New Trip", fontWeight = FontWeight.Bold)
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("trips_list"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Overview Metrics Cards Row
      item {
        DashboardMetricsSection(
          totalTrips = state.totalTripsCount,
          totalBudget = state.totalBudgetOverall,
          totalRemaining = state.totalRemainingOverall,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // 2. Search & Filter Bar
      item {
        SearchAndFilterSection(
          searchQuery = state.searchQuery,
          onSearchQueryChanged = onSearchQueryChanged,
          selectedCategory = state.selectedCategoryFilter,
          onCategorySelected = onCategoryFilterSelected,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // 3. Section Title
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.FlightTakeoff,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Upcoming Trips (${state.filteredSummaries.size})",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          if (state.selectedCategoryFilter != null || state.searchQuery.isNotBlank()) {
            Text(
              text = "Filtered",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }

      // 4. Vertical List of Upcoming Trip Cards
      if (state.filteredSummaries.isEmpty()) {
        item {
          EmptyTripsStateCard(
            onAddTripClicked = onAddTripClicked,
            modifier = Modifier.fillMaxWidth()
          )
        }
      } else {
        items(
          items = state.filteredSummaries,
          key = { it.trip.id }
        ) { summary ->
          UpcomingTripCard(
            summary = summary,
            onCardClicked = { onTripClicked(summary.trip.id) },
            onQuickAddExpense = { onQuickAddExpenseClicked(summary.trip.id) },
            onDeleteTrip = { onDeleteTripClicked(summary.trip.id) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("trip_card_${summary.trip.id}")
          )
        }
      }

      // Bottom Spacer for FAB clearance
      item {
        Spacer(modifier = Modifier.height(72.dp))
      }
    }
  }
}

@Composable
fun DashboardMetricsSection(
  totalTrips: Int,
  totalBudget: Double,
  totalRemaining: Double,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    StatCard(
      title = "Upcoming",
      value = "$totalTrips Trips",
      icon = Icons.Default.Luggage,
      iconTint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.weight(1f)
    )

    StatCard(
      title = "Total Budget",
      value = formatCurrency(totalBudget),
      icon = Icons.Default.MonetizationOn,
      iconTint = MaterialTheme.colorScheme.secondary,
      modifier = Modifier.weight(1.2f)
    )

    StatCard(
      title = "Remaining",
      value = formatCurrency(totalRemaining),
      icon = Icons.Default.AccountBalanceWallet,
      iconTint = if (totalRemaining >= 0) BudgetHealthyGreen else BudgetOverRed,
      modifier = Modifier.weight(1.2f)
    )
  }
}

@Composable
fun SearchAndFilterSection(
  searchQuery: String,
  onSearchQueryChanged: (String) -> Unit,
  selectedCategory: TripCategory?,
  onCategorySelected: (TripCategory?) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    // Search TextField
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onSearchQueryChanged,
      placeholder = { Text("Search trips, destinations, or countries...") },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onSearchQueryChanged("") }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear search")
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("trip_search_input")
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Category Filter Chips Carousel
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      FilterChip(
        selected = selectedCategory == null,
        onClick = { onCategorySelected(null) },
        label = { Text("All Trips") },
        leadingIcon = {
          if (selectedCategory == null) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
          }
        }
      )

      TripCategory.values().forEach { cat ->
        val isSelected = selectedCategory == cat
        FilterChip(
          selected = isSelected,
          onClick = { onCategorySelected(cat) },
          label = { Text(cat.label) },
          leadingIcon = {
            if (isSelected) {
              Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
            } else {
              Icon(cat.icon, contentDescription = null, modifier = Modifier.size(14.dp))
            }
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = cat.accentColor.copy(alpha = 0.2f),
            selectedLabelColor = cat.accentColor
          )
        )
      }
    }
  }
}

/**
 * Vertical list card summarizing an upcoming trip:
 * Destination, dates, category, budget summary, remaining funds, and quick actions.
 */
@Composable
fun UpcomingTripCard(
  summary: TripBudgetSummary,
  onCardClicked: () -> Unit,
  onQuickAddExpense: () -> Unit,
  onDeleteTrip: () -> Unit,
  modifier: Modifier = Modifier
) {
  val trip = summary.trip
  val remainingColor = when (summary.health) {
    com.example.roamly.model.BudgetHealth.HEALTHY -> BudgetHealthyGreen
    com.example.roamly.model.BudgetHealth.WARNING -> BudgetWarningAmber
    com.example.roamly.model.BudgetHealth.OVER_BUDGET -> BudgetOverRed
  }

  Card(
    modifier = modifier.clickable { onCardClicked() },
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // Top row: Category Badge & Status / Delete
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TripCategoryBadge(category = trip.category)

        Row(verticalAlignment = Alignment.CenterVertically) {
          BudgetHealthBadge(health = summary.health)
          IconButton(
            onClick = onDeleteTrip,
            modifier = Modifier
              .size(28.dp)
              .padding(start = 6.dp)
              .testTag("delete_trip_${trip.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete trip",
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Title & Destination
      Text(
        text = trip.title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 2.dp)
      ) {
        Icon(
          imageVector = Icons.Default.LocationOn,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = trip.destination,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Dates
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
      ) {
        Icon(
          imageVector = Icons.Default.CalendarMonth,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "${trip.startDate} → ${trip.endDate}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Budget Progress & Summary Container
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          // Progress bar
          BudgetProgressBar(
            spent = summary.totalSpent,
            total = trip.totalBudget,
            health = summary.health
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Key numbers: Budget, Spent, Remaining
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Budget",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = formatCurrency(trip.totalBudget),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "Spent",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = formatCurrency(summary.totalSpent),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Remaining Funds",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = formatCurrency(summary.remainingFunds),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = remainingColor
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onCardClicked,
          modifier = Modifier
            .weight(1f)
            .testTag("view_budget_${trip.id}"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
          )
        ) {
          Icon(
            imageVector = Icons.Default.AccountBalanceWallet,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Budget & Expenses",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
          )
        }

        OutlinedButton(
          onClick = onQuickAddExpense,
          modifier = Modifier.testTag("quick_add_expense_${trip.id}"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Expense", style = MaterialTheme.typography.labelMedium)
        }
      }
    }
  }
}

@Composable
fun EmptyTripsStateCard(
  onAddTripClicked: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Luggage,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(32.dp)
        )
      }
      Spacer(modifier = Modifier.height(14.dp))
      Text(
        text = "No upcoming trips found",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Plan your next journey with custom budgets, expense tracking, and dates.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
      Spacer(modifier = Modifier.height(16.dp))
      Button(
        onClick = onAddTripClicked,
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Create First Trip")
      }
    }
  }
}
