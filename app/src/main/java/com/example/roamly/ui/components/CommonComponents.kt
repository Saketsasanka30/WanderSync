package com.example.roamly.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roamly.model.BudgetHealth
import com.example.roamly.model.TripCategory
import com.example.roamly.theme.BudgetHealthyContainer
import com.example.roamly.theme.BudgetHealthyGreen
import com.example.roamly.theme.BudgetOverContainer
import com.example.roamly.theme.BudgetOverRed
import com.example.roamly.theme.BudgetWarningAmber
import com.example.roamly.theme.BudgetWarningContainer
import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Double): String {
  val format = NumberFormat.getCurrencyInstance(Locale.US)
  format.maximumFractionDigits = 2
  format.minimumFractionDigits = if (amount % 1.0 == 0.0) 0 else 2
  return format.format(amount)
}

@Composable
fun BudgetHealthBadge(health: BudgetHealth, modifier: Modifier = Modifier) {
  val (bgColor, textColor, icon, label) = when (health) {
    BudgetHealth.HEALTHY -> Quadruple(
      BudgetHealthyContainer,
      BudgetHealthyGreen,
      Icons.Default.CheckCircle,
      "On Track"
    )
    BudgetHealth.WARNING -> Quadruple(
      BudgetWarningContainer,
      BudgetWarningAmber,
      Icons.Default.WarningAmber,
      "Near Limit"
    )
    BudgetHealth.OVER_BUDGET -> Quadruple(
      BudgetOverContainer,
      BudgetOverRed,
      Icons.Default.ErrorOutline,
      "Over Budget"
    )
  }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = bgColor
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = textColor,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = label,
        color = textColor,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun TripCategoryBadge(category: TripCategory, modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = category.accentColor.copy(alpha = 0.15f)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = category.icon,
        contentDescription = category.label,
        tint = category.accentColor,
        modifier = Modifier.size(13.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = category.label,
        color = category.accentColor,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

@Composable
fun BudgetProgressBar(
  spent: Double,
  total: Double,
  health: BudgetHealth,
  modifier: Modifier = Modifier
) {
  val ratio = if (total > 0) (spent / total).toFloat().coerceIn(0f, 1f) else 0f
  val animatedProgress by animateFloatAsState(targetValue = ratio, label = "budgetProgress")

  val progressColor by animateColorAsState(
    targetValue = when (health) {
      BudgetHealth.HEALTHY -> BudgetHealthyGreen
      BudgetHealth.WARNING -> BudgetWarningAmber
      BudgetHealth.OVER_BUDGET -> BudgetOverRed
    },
    label = "progressColor"
  )

  Column(modifier = modifier) {
    LinearProgressIndicator(
      progress = { animatedProgress },
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp)),
      color = progressColor,
      trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
  }
}

@Composable
fun StatCard(
  title: String,
  value: String,
  icon: ImageVector,
  iconTint: Color,
  modifier: Modifier = Modifier,
  subtitle: String? = null
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
          modifier = Modifier
            .size(32.dp)
            .background(iconTint.copy(alpha = 0.15f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      if (subtitle != null) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
