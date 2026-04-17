package com.gshoaib998.progressly.utils

import android.icu.util.Calendar
import android.icu.util.ULocale
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    private val HIJRI_MONTH_NAMES = listOf(
        "Muharram", "Safar", "Rabi al-Awwal", "Rabi al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    fun timePickerToMillis(hour: Int, minutes: Int): Long {
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minutes))
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun millisToFormattedTime(millis: Long): String {
        val localDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(millis),
            ZoneId.systemDefault()
        )
        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            localDateTime.hour,
            localDateTime.minute
        )
    }

    fun millisToFormattedDuration(millis: Long, isSecondsReq: Boolean = false): String {
        val localDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(millis),
            ZoneId.of("UTC")
        )
        return String.format(
            locale = Locale.getDefault(),
            format = if (isSecondsReq) "%02d:%02d:%02d" else "%02d:%02d",
            args = arrayOf(localDateTime.hour, localDateTime.minute, localDateTime.second)
        )
    }

    fun formatedDate(epochMillis: Long, isOnlyDateRequired: Boolean = false): String {
        val instant = Instant.ofEpochMilli(epochMillis)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter =
            if (isOnlyDateRequired) DateTimeFormatter.ofPattern("dd MMM yyyy") else DateTimeFormatter.ofPattern(
                "dd MMM yyyy, hh:mm a"
            )
        return localDateTime.format(formatter)
    }

    fun calculateIslamicDate(epochMillis: Long): String {
        val islamicLocale = ULocale("ar@calendar=islamic-umalqura")
        val instance = Calendar.getInstance(islamicLocale)
        instance.timeInMillis = epochMillis
        val day = instance.get(Calendar.DAY_OF_MONTH)
        val month = instance.get(Calendar.MONTH)
        val year = instance.get(Calendar.YEAR)
        val monthName = HIJRI_MONTH_NAMES.getOrElse(month) { "unknown month" }
        val formatedDate = "$day $monthName $year AH"
        return formatedDate
    }

    fun toMidnightEpoch(millis: Long): Long {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}