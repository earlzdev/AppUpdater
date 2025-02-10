package com.earldev.app_update.apk

import android.content.Context
import android.os.Environment
import com.earldev.app_update.ApkFileNameProvider
import com.earldev.app_update.api.UpdateStateHolder
import com.earldev.app_update.UpdateStep
import com.earldev.app_update.api.models.UnsecuredApkException
import com.earldev.app_update.datastore.SelfUpdateStore
import com.earldev.app_update.models.UpdateJob
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

internal class ApkSecurityChecker(
    nextHandler: ApkHandler,
    private val context: Context,
    private val apkFileNameProvider: ApkFileNameProvider
) : ApkHandler(nextHandler) {

    override fun canHandle(job: UpdateJob): Boolean {
        return job.downloaded
    }

    override suspend fun handle(job: UpdateJob) {
        val remoteChecksum: String = requireNotNull(SelfUpdateStore.remoteChecksum()) {
            "No remote checksum"
        }
        val calculatedChecksum: String = requireNotNull(calculateChecksum()) {
            "Cannot calculate checksum"
        }
        if (calculatedChecksum == remoteChecksum) {
            UpdateStateHolder.emit(UpdateStep.SecurityCheck(success = true))
            super.handle(job.copy(secured = true))
        } else {
            throw UnsecuredApkException()
        }
    }

    private fun calculateChecksum(): String? = try {
        val apkFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            apkFileNameProvider.provide()
        )
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(apkFile).use { fis ->
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        digest.digest().joinToString("") { "%02x".format(it) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}