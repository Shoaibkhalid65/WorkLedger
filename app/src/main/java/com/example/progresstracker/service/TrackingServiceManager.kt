package com.example.progresstracker.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingServiceManager @Inject constructor() {
    private val _elapsedMillis = MutableStateFlow(0L)
    val elapsedMillis=_elapsedMillis.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _startTime = MutableStateFlow(0L)
    val startTime = _startTime.asStateFlow()

    fun startTracking(startTime: Long){
        _startTime.value=startTime
        _isTracking.value=true
    }

    fun stopTracking(){
        _startTime.value=0L
        _isTracking.value=false
        _elapsedMillis.value=0L
    }

    fun updateElapsed(elapsed: Long){
        _elapsedMillis.value=elapsed
    }
}