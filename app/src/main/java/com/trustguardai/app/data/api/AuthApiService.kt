package com.trustguardai.app.data.api

import com.trustguardai.app.data.model.LoginRequest
import com.trustguardai.app.data.model.Token
import com.trustguardai.app.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Token

    @GET("api/auth/me")
    suspend fun getMe(): User
}
