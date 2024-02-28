package di

import com.taske.sicktrainer.SharedModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.ksp.generated.module

//expect fun platformModule(): Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
//        modules(commonModule())
        modules(SharedModule().module)
        appDeclaration()
    }

// called by iOS client
fun initKoin() = initKoin() {}

fun commonModule() = module {
//    includes(platformModule())

//    singleOf(::ConfettiRepository)
//    singleOf(::AppSettings)
//    singleOf(::ApolloClientCache)

//    androidLogger()
//    androidContext()
//    modules(defaultModule)
}

//expect fun getDatabaseName(conference: String, uid: String?): String
