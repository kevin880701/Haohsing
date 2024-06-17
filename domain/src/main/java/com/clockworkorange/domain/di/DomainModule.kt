package com.clockworkorange.domain.di

import android.content.Context
import com.clockworkorange.domain.data.*
import com.clockworkorange.domain.data.local.AppDatabase
import com.clockworkorange.domain.data.remote.GlobalErrorHandler
import com.clockworkorange.domain.data.remote.HaohsingService
import com.clockworkorange.domain.data.remote.ServiceWrapper
import com.clockworkorange.domain.data.remote.ServiceWrapperImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DomainModule {

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun provideHaohsingService(): HaohsingService {
        return HaohsingService.create(true)
    }

    @Singleton
    @Provides
    fun provideServiceWrapper(
        service: HaohsingService,
        preferenceStorage: PreferenceStorage,
        globalErrorHandler: GlobalErrorHandler,
        gson: Gson
    ): ServiceWrapper {
        return ServiceWrapperImpl(service, preferenceStorage, globalErrorHandler, gson)
    }

    @Provides
    fun provideRegisterRepository(
        service: ServiceWrapper
    ): RegisterRepository{
        return RegisterRepositoryImpl(service)
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        @ApplicationScope applicationScope: CoroutineScope,
        preferenceStorage: PreferenceStorage,
        service: ServiceWrapper
    ): UserRepository{
        return UserRepositoryImpl(applicationScope, preferenceStorage, service)
    }

    @Provides
    fun provideZipCodeRepository(service: ServiceWrapper): CityRegionRepository {
        return CityRegionRepositoryImpl(service)
    }

    @Singleton
    @Provides
    fun provideDeviceRepository(
        @ApplicationScope applicationScope: CoroutineScope,
        service: ServiceWrapper
    ): DeviceRepository{
//        return FakeDeviceRepository()
        return DeviceRepositoryImpl(applicationScope, service)
    }

    @Singleton
    @Provides
    fun providePlaceRepository(@ApplicationScope applicationScope: CoroutineScope, service: ServiceWrapper): PlaceRepository{
//        return FakePlaceRepository()
        return PlaceRepositoryImpl(applicationScope, service)
    }

    @Singleton
    @Provides
    fun provideNotificationRepository(service: ServiceWrapper, appDatabase: AppDatabase): NotificationRepository{
        return NotificationRepositoryImpl(service, appDatabase)
    }

    @Singleton
    @Provides
    fun provideWorkOrderRepository(
        @ApplicationContext context: Context,
        service: ServiceWrapper): WorkOrderRepository{
//        return FakeWorkOrderRepository(service)
        return WorkOrderRepositoryImpl(context, service)
    }

    @Singleton
    @Provides
    fun provideTaskRepository(@ApplicationContext context: Context, service: ServiceWrapper): TaskRepository{
//        return FakeTaskRepository(service)
        return TaskRepositoryImpl(context, service)
    }

    @Provides
    fun provideVendorRepository(service: ServiceWrapper): VendorRepository {
        return VendorRepositoryImpl(service)
    }
}