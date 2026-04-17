package com.gshoaib998.progressly.model

data class TaskDuration(
    val id: Long = 0L,
    val startTime: Long,
    val endTime: Long,
){
    val durationTime = endTime-startTime
}
