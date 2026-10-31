package com.trustguardai.app.data.api

import com.trustguardai.app.data.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ThreatApiService {

    @POST("api/scan/url")
    suspend fun scanUrl(@Body request: ScanRequest): ScanResult

    @POST("api/scan/ip")
    suspend fun scanIp(@Body request: ScanRequest): ScanResult

    @Multipart
    @POST("api/scan/file")
    suspend fun scanFile(@Part file: MultipartBody.Part): ScanResult

    @GET("api/threats")
    suspend fun getThreatHistory(): ThreatHistoryResponse

    @GET("api/employee/summary")
    suspend fun getEmployeeSummary(): EmployeeSummary

    @POST("api/activity")
    suspend fun logActivity(@Body activity: ActivityLog)

    @GET("api/admin/risk-overview")
    suspend fun getAdminRiskOverview(): AdminRiskOverview

    @GET("api/admin/activities")
    suspend fun getAdminActivities(): List<ActivityLog>

    @GET("api/admin/employee-risk")
    suspend fun getEmployeeRisks(): List<BehavioralRisk>

    @GET("api/alerts")
    suspend fun getAlerts(): List<Alert>

    @GET("api/alerts/{id}")
    suspend fun getAlert(@Path("id") alertId: String): Alert

    @PATCH("api/alerts/{id}")
    suspend fun updateAlertStatus(@Path("id") alertId: String, @Query("status") status: AlertStatus): Alert

    @GET("api/incidents")
    suspend fun getIncidents(): List<Incident>

    @GET("api/incidents/{id}")
    suspend fun getIncident(@Path("id") incidentId: String): Incident

    @PATCH("api/incidents/{id}")
    suspend fun updateIncidentStatus(@Path("id") incidentId: String, @Query("status") status: AlertStatus): Incident
}
