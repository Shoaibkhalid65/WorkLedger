package com.gshoaib998.progressly.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gshoaib998.progressly.data.repository.DashboardRepository
import com.gshoaib998.progressly.model.DailyDurationTotal
import com.gshoaib998.progressly.model.DailySatisfactionAvg
import com.gshoaib998.progressly.model.TodaySummary
import com.gshoaib998.progressly.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeTodaySummary()
        observeWeeklyDurations()
        observeSatisfactionTrend()
    }

    private fun observeTodaySummary() {
        viewModelScope.launch {
            repository.observeTodaySummary().collect { summary ->
                _uiState.update { it.copy(todaySummary = summary, isLoading = false) }
            }
        }
    }

    private fun observeWeeklyDurations() {
        viewModelScope.launch {
            repository.observeDailyDurationTotals().collect { totals ->
                // Take last 7 days, fill missing days with 0
                val last7 = buildLast7Days(totals)
                _uiState.update { it.copy(weeklyDurations = last7) }
            }
        }
    }

    private fun observeSatisfactionTrend() {
        viewModelScope.launch {
            repository.observeDailySatisfactionAverages().collect { avgs ->
                _uiState.update { it.copy(satisfactionTrend = avgs) }
            }
        }
    }

    // Ensures all 7 days appear even if no data exists for that day
    private fun buildLast7Days(data: List<DailyDurationTotal>): List<DailyDurationTotal> {
        val dataMap = data.associateBy { it.dateEpoch }
        val today = DateTimeUtils.toMidnightEpoch(System.currentTimeMillis())
        val oneDayMs = 86_400_000L
        return (6 downTo 0).map { daysAgo ->
            val epoch = today - daysAgo * oneDayMs
            dataMap[epoch] ?: DailyDurationTotal(dateEpoch = epoch, totalMillis = 0L)
        }
    }

    fun dayLabel(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("EEE"))  // Mon, Tue…
    }
}

data class DashboardUiState(
    val isLoading: Boolean = true,
    val todaySummary: TodaySummary = TodaySummary(0f, 0, 0f),
    val weeklyDurations: List<DailyDurationTotal> = emptyList(),
    val satisfactionTrend: List<DailySatisfactionAvg> = emptyList()
)

