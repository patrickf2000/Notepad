package org.pdf4664.notepad.activities

import org.pdf4664.notepad.R
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView

class WebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        val intent = intent
        val content = intent.extras!!.getString("file")

        val web = findViewById<View>(R.id.web_view) as WebView
        web.loadData(content, "text/html; charset=utf-8", "utf-8")
    }
}
