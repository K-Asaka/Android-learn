package user.example.sainttropez

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerForContextMenu(imageView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.top -> {
                imageView.setImageResource(R.drawable.toppage)
                return true
            }
            R.id.lunch01 -> {
                imageView.setImageResource(R.drawable.lunch01)
                return true
            }
            R.id.lunch02 -> {
                imageView.setImageResource(R.drawable.lunch02)
                return true
            }
            R.id.dinner01 -> {
                imageView.setImageResource(R.drawable.dinner01);
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
//        when (item?.itemId) {
//            R.id.sms -> {
//                val number = "999-9999-9999"
//                val uri = Uri.parse("sms:$number")
//                var intent = Intent(Intent.ACTION_VIEW)
//                intent.data = uri
//                intent.putExtra("sms_body", "こんにちは")
//                startActivity(intent)
//                return true
//            }
//            R.id.mail -> {
//                val email: String = "nobody@example.com"
//                val subject: String = "予約問い合わせ"
//                val text: String = "以下の通り予約希望します。"
//                val uri = Uri.parse("mailto:")
//                val intent = Intent(Intent.ACTION_SENDTO)
//                intent.apply {
//                    data = uri
//                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
//                    putExtra(Intent.EXTRA_SUBJECT, subject)
//                    putExtra(Intent.EXTRA_TEXT, text)
//                }
//                if (intent.resolveActivity(packageManager) != null) {
//                    startActivity(intent)
//                }
//                return true
//            }
//            R.id.share -> {
//                val text: String = "美味しいレストランを紹介します。"
//                val intent = Intent(Intent.ACTION_SEND)
//                intent.apply {
//                    type = "text/plain"
//                    putExtra(Intent.EXTRA_TEXT, text)
//                }
//                val chooser = Intent.createChooser(intent, null)
//                if (intent.resolveActivity(packageManager) != null) {
//                    startActivity(chooser)
//                }
//                return true
//            }
//            R.id.browse -> {
//                val url: String = "http://www.yahoo.co.jp/"
//                val intent = Intent(Intent.ACTION_VIEW)
//                intent.data = Uri.parse(url)
//                if (intent.resolveActivity(packageManager) != null) {
//                    startActivity(intent)
//                }
//                return true
//            }
        // Ankoを使って書いた場合
        return when(item?.itemId) {
            R.id.sms -> sendSMS("999-9999-9999")
            R.id.mail -> email("nobody@example.com", "予約問い合わせ", "以下の通り予約希望します")
            R.id.mail -> email("nobody@example.com", "予約問い合わせ", "以下の通り予約希望します")
            R.id.share -> share("美味しいレストランを紹介します。")
            R.id.browse -> browse("http://www.yahoo.co.jp")
            else -> super.onContextItemSelected(item)
        }
    }
}