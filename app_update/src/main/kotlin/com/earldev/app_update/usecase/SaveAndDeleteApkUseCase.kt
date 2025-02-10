package com.earldev.app_update.usecase

import android.content.Context
import android.os.Environment
import androidx.annotation.WorkerThread
import com.earldev.app_update.ApkFileNameProvider
import com.earldev.app_update.utils.SelfUpdateLog
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

internal interface SaveAndDeleteApkUseCase {

    @WorkerThread
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun save(inputStream: InputStream?)

    fun remove()
}

internal class SaveAndDeleteApkUseCaseImpl @Inject constructor(
    private val context: Context,
    private val apkFileNameProvider: ApkFileNameProvider
) : SaveAndDeleteApkUseCase {

    override fun save(inputStream: InputStream?) {
        SelfUpdateLog.logInfo("Start saving apk")
        if (inputStream == null) throw IllegalArgumentException("Input stream can not be null")

        val apkFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            apkFileNameProvider.provide()
        )

        try {
            apkFile.outputStream().use { fileOut ->
                inputStream.copyTo(fileOut)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        } finally {
            SelfUpdateLog.logInfo("Apk save ends")
        }
    }

    override fun remove() {
        SelfUpdateLog.logInfo("Start deleting apk")

        val apkFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            apkFileNameProvider.provide()
        )

        if (apkFile.exists()) {
            if (apkFile.delete()) {
                SelfUpdateLog.logInfo("Apk deleted successfully")
            } else {
                SelfUpdateLog.logError("Failed to delete apk")
                throw IOException("Failed to delete APK file")
            }
        } else {
            SelfUpdateLog.logInfo("Apk file not found, nothing to delete")
        }
    }
}