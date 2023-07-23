package com.example.util.simpletimetracker.data_local.repo

import com.example.util.simpletimetracker.data_local.database.RecordTagDao
import com.example.util.simpletimetracker.data_local.mapper.RecordTagDataLocalMapper
import com.example.util.simpletimetracker.domain.model.RecordTag
import com.example.util.simpletimetracker.domain.repo.RecordTagRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordTagRepoImpl @Inject constructor(
    private val dao: RecordTagDao,
    private val mapper: RecordTagDataLocalMapper
) : RecordTagRepo {

    override suspend fun isEmpty(): Boolean = withContext(Dispatchers.IO) {
        Timber.d("isEmpty")
        return@withContext dao.isEmpty() == 0L
    }

    override suspend fun getAll(): List<RecordTag> = withContext(Dispatchers.IO) {
        Timber.d("getAll")
        dao.getAll().map(mapper::map)
    }

    override suspend fun get(id: Long): RecordTag? = withContext(Dispatchers.IO) {
        Timber.d("get")
        dao.get(id)?.let(mapper::map)
    }

    override suspend fun getByType(typeId: Long): List<RecordTag> = withContext(Dispatchers.IO) {
        Timber.d("getByType")
        dao.getByType(typeId).map(mapper::map)
    }

    override suspend fun getUntyped(): List<RecordTag> = withContext(Dispatchers.IO) {
        Timber.d("getUntyped")
        dao.getUntyped().map(mapper::map)
    }

    override suspend fun getByTypeOrUntyped(typeId: Long): List<RecordTag> = withContext(Dispatchers.IO) {
        Timber.d("getByTypeOrUntyped")
        dao.getByTypeOrUntyped(typeId).map(mapper::map)
    }

    override suspend fun add(tag: RecordTag): Long = withContext(Dispatchers.IO) {
        Timber.d("add")
        dao.insert(tag.let(mapper::map))
    }

    override suspend fun archive(id: Long) = withContext(Dispatchers.IO) {
        Timber.d("archive")
        dao.archive(id)
    }

    override suspend fun restore(id: Long) = withContext(Dispatchers.IO) {
        Timber.d("restore")
        dao.restore(id)
    }

    override suspend fun remove(id: Long) = withContext(Dispatchers.IO) {
        Timber.d("remove")
        dao.delete(id)
    }

    override suspend fun removeByType(typeId: Long) = withContext(Dispatchers.IO) {
        Timber.d("removeByType")
        dao.deleteByType(typeId)
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        Timber.d("clear")
        dao.clear()
    }
}