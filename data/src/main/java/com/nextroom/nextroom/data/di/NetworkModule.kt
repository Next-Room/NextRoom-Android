package com.nextroom.nextroom.data.di

import android.content.Context
import com.nextroom.nextroom.FlavorExtraFunction
import com.nextroom.nextroom.data.BuildConfig
import com.nextroom.nextroom.data.datasource.AuthDataSource
import com.nextroom.nextroom.data.datasource.TokenDataSource
import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.network.AuthAuthenticator
import com.nextroom.nextroom.data.network.AuthInterceptor
import com.nextroom.nextroom.data.network.ResultCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    @Named("authRetrofit")
    fun provideAuthRetrofit(
        @Named("authOkHttpClient") okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    @Named("defaultRetrofit")
    fun provideDefaultRetrofit(
        @Named("defaultOkHttpClient") okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    @Named("defaultApiService")
    fun provideDefaultApiService(
        @Named("defaultRetrofit") retrofit: Retrofit,
    ): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthApiService(
        @Named("authRetrofit") retrofit: Retrofit,
    ): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    @Named("authOkHttpClient")
    fun provideAuthOkHttpClient(
        authInterceptor: AuthInterceptor,
        authAuthenticator: AuthAuthenticator,
        flipperInterceptor: Interceptor?,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .authenticator(authAuthenticator)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .apply {
                flipperInterceptor?.let {
                    addInterceptor(it)
                }
            }
            .build()
    }

    @Singleton
    @Provides
    @Named("defaultOkHttpClient")
    fun provideDefaultOkHttpClient(
        flipperInterceptor: Interceptor?,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .apply {
                flipperInterceptor?.let {
                    addInterceptor(it)
                }
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(tokenDataSource: TokenDataSource): AuthInterceptor {
        return AuthInterceptor(tokenDataSource)
    }

    @Singleton
    @Provides
    fun provideAuthAuthenticator(
        tokenDataSource: TokenDataSource,
        authDataSource: AuthDataSource,
        @Named("defaultApiService") apiService: ApiService,
    ): AuthAuthenticator {
        return AuthAuthenticator(tokenDataSource, authDataSource, apiService)
    }

    @Singleton
    @Provides
    fun provideFlipperInterceptor(
        flavorExtraFunction: FlavorExtraFunction,
    ): Interceptor? {
        return flavorExtraFunction.getFlipperInterceptor()
    }

    @Singleton
    @Provides
    fun provideFlavorExtraFunction(
        @ApplicationContext context: Context,
    ): FlavorExtraFunction {
        return FlavorExtraFunction(context)
    }
}
