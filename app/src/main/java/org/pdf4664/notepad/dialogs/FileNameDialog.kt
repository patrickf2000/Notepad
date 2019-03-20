package org.pdf4664.notepad.dialogs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import org.pdf4664.notepad.R
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText

class FileNameDialog : Activity(), OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.file_name_dialog_layout)

        val submit = findViewById<View>(R.id.fileDialogSubmit) as Button
        submit.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view.id != R.id.fileDialogSubmit) {
            return
        }

        val entry = findViewById<View>(R.id.fileDialogEntry) as EditText
        val result = entry.text.toString()

        if (result.isEmpty()) {
            finish()
        }

        val intent = Intent()
        intent.putExtra("filename", result)
        setResult(0, intent)

        finish()
    }
}
