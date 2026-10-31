package com.trustguardai.app.data.repository

import android.content.Context
import android.net.Uri
import com.trustguardai.app.data.api.RetrofitClient
import com.trustguardai.app.data.model.ActivityType
import com.trustguardai.app.data.model.ScanRequest
import com.trustguardai.app.data.model.ScanResult
import com.trustguardai.app.data.model.ScanType
import com.trustguardai.app.data.model.ThreatClassification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.security.MessageDigest

interface ThreatRepository {
    suspend fun scanUrl(url: String): ScanResult
    suspend fun scanIp(ip: String): ScanResult
    suspend fun scanFile(context: Context, uri: Uri): ScanResult
}

class ApiThreatRepository : ThreatRepository {

    private val api = RetrofitClient.apiService

    override suspend fun scanUrl(url: String): ScanResult = withContext(Dispatchers.IO) {
        val result = api.scanUrl(ScanRequest(url))
        ActivityLogger.log(ActivityType.SCAN_URL, url, result.severity)
        result
    }

    override suspend fun scanIp(ip: String): ScanResult = withContext(Dispatchers.IO) {
        val result = api.scanIp(ScanRequest(ip))
        ActivityLogger.log(ActivityType.SCAN_IP, ip, result.severity)
        result
    }

    override suspend fun scanFile(context: Context, uri: Uri): ScanResult = withContext(Dispatchers.IO) {
        val file = getFileFromUri(context, uri)
        val requestFile = file.asRequestBody(context.contentResolver.getType(uri)?.toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        
        val backendResult = api.scanFile(body)
        
        // Enrich backend result with local SHA-256 for display consistency
        val localHash = calculateSha256(context, uri) ?: "unknown"
        val updatedMetadata = backendResult.metadata.toMutableMap().apply {
            put("Local SHA-256", localHash)
        }
        
        val finalResult = backendResult.copy(metadata = updatedMetadata)
        ActivityLogger.log(ActivityType.SCAN_FILE, file.name, finalResult.severity)
        finalResult
    }

    private fun getFileFromUri(context: Context, uri: Uri): File {
        val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        } ?: "temp_file"
        
        val file = File(context.cacheDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun calculateSha256(context: Context, uri: Uri): String? {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            context.contentResolver.openInputStream(uri)?.use { input ->
                val buffer = ByteArray(8192)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    digest.update(buffer, 0, read)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            null
        }
    }
}
