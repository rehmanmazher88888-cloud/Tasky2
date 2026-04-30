package com.example.calmtask.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.calmtask.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class EveningWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val repo = AppRepository(applicationContext)
        val profile = repo.userProfileFlow.first()
        if (profile.eveningSummaryOn) {
            Log.d("EveningWorker", "Evening summary triggered.")
        }
        Result.success()
    }
}
