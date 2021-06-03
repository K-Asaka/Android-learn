package user.example.myscheduler

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import user.example.myscheduler.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity
import javax.xml.validation.SchemaFactoryLoader

class MainActivity : AppCompatActivity() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        realm = Realm.getDefaultInstance()

        val schedules = realm.where(Schedule::class.java).findAll()
        listView.adapter = ScheduleAdapter(schedules)

        fab.setOnClickListener { view ->
            startActivity<ScheduleEditActivity>()
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val schedule = parent.getItemAtPosition(position) as Schedule
            startActivity<ScheduleEditActivity>(
                "schedule_id" to schedule.id)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}