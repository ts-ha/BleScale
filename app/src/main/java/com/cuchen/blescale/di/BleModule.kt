package com.cuchen.blescale.di

import android.content.Context
import com.cuchen.blescale.ble.BleManager
import com.cuchen.blescale.usecases.TemperatureAndHumidityReceiveManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BleModule {

    @Provides
    @Singleton
    fun provideBleManager(@ApplicationContext context: Context): TemperatureAndHumidityReceiveManager {
        return BleManager(context)
    }

}