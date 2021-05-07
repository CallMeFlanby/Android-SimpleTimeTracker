package com.example.util.simpletimetracker.feature_records_all.interactor

import com.example.util.simpletimetracker.core.adapter.ViewHolderType
import com.example.util.simpletimetracker.core.mapper.RangeMapper
import com.example.util.simpletimetracker.core.mapper.TimeMapper
import com.example.util.simpletimetracker.domain.interactor.PrefsInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordTypeInteractor
import com.example.util.simpletimetracker.feature_records_all.mapper.RecordsAllViewDataMapper
import com.example.util.simpletimetracker.feature_records_all.model.RecordsAllSortOrder
import com.example.util.simpletimetracker.feature_records_all.viewData.RecordsAllDateViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

class RecordsAllViewDataInteractor @Inject constructor(
    private val recordInteractor: RecordInteractor,
    private val recordTypeInteractor: RecordTypeInteractor,
    private val prefsInteractor: PrefsInteractor,
    private val recordsAllViewDataMapper: RecordsAllViewDataMapper,
    private val timeMapper: TimeMapper,
    private val rangeMapper: RangeMapper
) {

    suspend fun getViewData(
        typesSelected: List<Long>,
        sortOrder: RecordsAllSortOrder,
        rangeStart: Long,
        rangeEnd: Long
    ): List<ViewHolderType> {
        val isDarkTheme = prefsInteractor.getDarkMode()
        val useMilitaryTime = prefsInteractor.getUseMilitaryTimeFormat()
        val recordTypes = recordTypeInteractor.getAll()
            .map { it.id to it }
            .toMap()
        val records = recordInteractor.getByType(typesSelected).let {
            if (rangeStart != 0L && rangeEnd != 0L) {
                rangeMapper.getRecordsFromRange(it, rangeStart, rangeEnd)
                    // Skip records that started before this time range.
                    .filter { record -> record.timeStarted > rangeStart }
            } else {
                it
            }
        }

        return withContext(Dispatchers.Default) {
            records
                .mapNotNull { record ->
                    recordTypes[record.typeId]?.let { type -> record to type }
                }
                .map { (record, recordType) ->
                    Triple(
                        record.timeStarted,
                        record.timeEnded - record.timeStarted,
                        recordsAllViewDataMapper.map(
                            record = record,
                            recordType = recordType,
                            isDarkTheme = isDarkTheme,
                            useMilitaryTime = useMilitaryTime
                        )
                    )
                }
                .sortedByDescending { (timeStarted, duration, _) ->
                    when (sortOrder) {
                        RecordsAllSortOrder.TIME_STARTED -> timeStarted
                        RecordsAllSortOrder.DURATION -> duration
                    }
                }
                .map { (timeStarted, _, record) -> timeStarted to record }
                .let { viewData ->
                    if (sortOrder == RecordsAllSortOrder.TIME_STARTED) {
                        addDateViewData(viewData)
                    } else {
                        viewData.map { it.second }
                    }
                }
                .ifEmpty {
                    listOf(recordsAllViewDataMapper.mapToEmpty())
                }
        }
    }

    private fun addDateViewData(viewData: List<Pair<Long, ViewHolderType>>): List<ViewHolderType> {
        val calendar = Calendar.getInstance()
        val newViewData = mutableListOf<ViewHolderType>()
        var previousTimeStarted = 0L

        viewData.forEach { (timeStarted, recordViewData) ->
            synchronized(timeMapper) {
                if (!timeMapper.sameDay(timeStarted, previousTimeStarted, calendar)) {
                    timeMapper.formatDateYear(timeStarted)
                        .let(::RecordsAllDateViewData)
                        .let(newViewData::add)
                }
            }
            previousTimeStarted = timeStarted
            newViewData.add(recordViewData)
        }

        return newViewData
    }
}