package user.example.myscheduler

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MyScheduleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build()
        )
    }
}