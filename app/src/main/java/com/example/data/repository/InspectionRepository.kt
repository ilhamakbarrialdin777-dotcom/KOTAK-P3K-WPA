package com.example.data.repository

import com.example.data.db.InspectionDao
import com.example.data.model.DefaultDataHelper
import com.example.data.model.InspectionEntity
import kotlinx.coroutines.flow.Flow

class InspectionRepository(private val dao: InspectionDao) {

    val allInspections: Flow<List<InspectionEntity>> = dao.getAllInspections()

    fun getInspectionById(id: Long): Flow<InspectionEntity?> = dao.getInspectionById(id)

    suspend fun saveInspection(entity: InspectionEntity): Long {
        return if (entity.id == 0L) {
            dao.insertInspection(entity)
        } else {
            dao.updateInspection(entity)
            entity.id
        }
    }

    suspend fun deleteInspection(entity: InspectionEntity) {
        dao.deleteInspection(entity)
    }

    suspend fun deleteInspectionById(id: Long) {
        dao.deleteInspectionById(id)
    }

    suspend fun ensureInitialData() {
        if (dao.getCount() == 0) {
            dao.insertAll(DefaultDataHelper.getSampleInspections())
        }
    }
}
