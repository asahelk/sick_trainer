package org.taske.sicktrainer

import com.taske.sicktrainer.SharedModule
import android.app.Application
import di.initKoin

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin{
            androidContext(this@MainApplication)
            androidLogger()
        }

    }
}
