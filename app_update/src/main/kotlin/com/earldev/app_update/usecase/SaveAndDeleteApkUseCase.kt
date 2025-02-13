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

/**
 * Interface defining the Use-case for saving and deleting an APK file.
 *
 * This interface provides methods for saving an APK file from an input stream and deleting it.
 * All operations are performed on a background thread and may throw exceptions in case of errors.
 */
internal interface SaveAndDeleteApkUseCase {

    /**
     * Saves the APK file from the input stream.
     *
     * @param inputStream The input stream containing the APK file data. Cannot be `null`.
     *
     * @return `true` if the file was successfully saved, otherwise `false`.
     * @throws IllegalArgumentException If the input stream is `null`.
     * @throws IllegalStateException If an error occurs during the save process.
     * @throws IOException If an I/O error occurs.
     */
    @WorkerThread
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun save(inputStream: InputStream?): Boolean

    /**
     * Deletes the saved APK file.
     *
     * @throws IOException If the file cannot be deleted.
     */
    @Throws(IOException::class)
    fun remove()
}

/**
 * Implementation of [SaveAndDeleteApkUseCase].
 *
 * @property context The [Context] of the application, necessary for accessing the file system.
 * @property apkFileNameProvider The provider for the APK file name.
 */
internal class SaveAndDeleteApkUseCaseImpl @Inject constructor(
    private val context: Context,
    private val apkFileNameProvider: ApkFileNameProvider
) : SaveAndDeleteApkUseCase {

    override fun save(inputStream: InputStream?): Boolean {
        SelfUpdateLog.logInfo("Start saving apk")
        if (inputStream == null) throw IllegalArgumentException("Input stream can not be null")

        val apkFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            apkFileNameProvider.provide()
        )

        return try {
            apkFile.outputStream().use { fileOut ->
                inputStream.copyTo(fileOut)
            }
            true
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
