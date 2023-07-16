package com.example.util.simpletimetracker.core.interactor

import com.example.util.simpletimetracker.domain.mapper.RangeMapper
import com.example.util.simpletimetracker.core.mapper.TimeMapper
import com.example.util.simpletimetracker.domain.interactor.PrefsInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordInteractor
import com.example.util.simpletimetracker.domain.model.Range
import com.example.util.simpletimetracker.domain.model.RangeLength
import com.example.util.simpletimetracker.domain.model.RunningRecord
import java.lang.Long.max
import javax.inject.Inject

class GetCurrentRecordsDurationInteractor @Inject constructor(
    private val recordInteractor: RecordInteractor,
    private val rangeMapper: RangeMapper,
    private val prefsInteractor: PrefsInteractor,
    private val timeMapper: TimeMapper,
) {

    suspend fun getDailyCurrent(runningRecord: RunningRecord): Result {
        return getRangeCurrent(runningRecord, getRange(RangeLength.Day))
    }

    suspend fun getWeeklyCurrent(runningRecord: RunningRecord): Result {
        return getRangeCurrent(runningRecord, getRange(RangeLength.Week))
    }

    suspend fun getMonthlyCurrent(runningRecord: RunningRecord): Result {
        return getRangeCurrent(runningRecord, getRange(RangeLength.Month))
    }

    private suspend fun getRangeCurrent(
        runningRecord: RunningRecord,
        range: Range
    ): Result {
        val current = System.currentTimeMillis()
        val currentRunning = current - runningRecord.timeStarted
        val currentRunningClamped = current - max(runningRecord.timeStarted, range.timeStarted)

        val records = recordInteractor.getFromRange(range)
            .filter { it.typeId == runningRecord.id }
            .map { rangeMapper.clampToRange(it, range) }
        val duration = records
            .let(rangeMapper::mapToDuration)
        val count = records.size.toLong()

        return Result(
            range = range,
            duration = duration + currentRunningClamped,
            count = count + 1, // 1 is for current running record.
            durationDiffersFromCurrent = duration != 0L || currentRunning != currentRunningClamped
        )
    }

    private suspend fun getRange(rangeLength: RangeLength): Range {
        val firstDayOfWeek = prefsInteractor.getFirstDayOfWeek()
        val startOfDayShift = prefsInteractor.getStartOfDayShift()

        return timeMapper.getRangeStartAndEnd(
            rangeLength = rangeLength,
            shift = 0,
            firstDayOfWeek = firstDayOfWeek,
            startOfDayShift = startOfDayShift,
        )
    }

    data class Result(
        val range: Range,
        val duration: Long,
        val count: Long,
        val durationDiffersFromCurrent: Boolean,
    )
}