package com.trustguardai.app.data.repository

import com.trustguardai.app.data.api.RetrofitClient
import com.trustguardai.app.data.model.ActivityLog
import com.trustguardai.app.data.model.ActivityType
import com.trustguardai.app.data.model.ThreatSeverity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ActivityLogger {
    private val api = RetrofitClient.apiService
    private val scope = CoroutineScope(Dispatchers.IO)
    
    // In a real app, this would come from a SessionManager or Auth state
    private const val CURRENT_USER_ID = "agent_smith"

    fun log(type: ActivityType, resource: String? = null, severity: ThreatSeverity = ThreatSeverity.SAFE) {
        scope.launch {
            try {
                val log = ActivityLog(
                    userId = CURRENT_USER_ID,
                    activityType = type,
                    resource = resource,
                    severity = severity
                )
                api.logActivity(log)
            } catch (e: Exception) {
                // Fail silently in demo, or log to console
            }
        }
    }
}
