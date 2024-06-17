package com.clockworkorange.haohsing.di

import android.content.Context
import com.clockworkorange.domain.data.local.AppDatabase
import com.clockworkorange.domain.data.remote.GlobalErrorHandler
import com.clockworkorange.domain.di.ApplicationScope
import com.clockworkorange.domain.di.DefaultDispatcher
import com.clockworkorange.haohsing.ui.GlobalErrorHandlerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideGlobalErrorHandler(): GlobalErrorHandler {
        return GlobalErrorHandlerImpl
    }

    @ApplicationScope
    @Singleton
    @Provides
    fun providesApplicationScope(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher)

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.create(context)
    }

}