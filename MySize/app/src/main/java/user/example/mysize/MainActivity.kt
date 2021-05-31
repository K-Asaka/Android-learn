package user.example.mysize

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        pref.apply {
            val editNeck = getString("NECK", "")
            val editSleeve = getString("SLEEVE", "")
            val editWaist = getString("WAIST", "")
            val editInseam = getString("INSEAM", "")

            neck.setText(editNeck)
            sleeve.setText(editSleeve)
            waist.setText(editWaist)
            inseam.setText(editInseam)
        }

        save.setOnClickListener { onSaveTapped() }

        heightButton.setOnClickListener {
            startActivity<HeightActivity>()
        }
    }

    private fun onSaveTapped() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()
        editor.putString("NECK", neck.text.toString())
            .putString("SLEEVE", sleeve.text.toString())
            .putString("WAIST", waist.text.toString())
            .putString("INSEAM", inseam.text.toString())
            .apply()
    }
}