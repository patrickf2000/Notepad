package org.pdf4664.notepad.activities

import android.os.Bundle
import org.pdf4664.notepad.utilities.DocUtils
import org.pdf4664.notepad.dialogs.FileNameDialog
import org.pdf4664.notepad.R
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.Toast

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class EditorActivity : AppCompatActivity(), TextWatcher {

    private var currentFile: String? = "untitled"
    private var modified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val intent = intent
        currentFile = intent.extras!!.getString("file")

        val toolbar = findViewById<View>(R.id.editor_toolbar) as Toolbar
        toolbar.title = currentFile
        setSupportActionBar(toolbar)

        val editor = findViewById<View>(R.id.editor) as EditText
        editor.addTextChangedListener(this)

        openFile()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.editor_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_file_item -> saveFile(false)
            R.id.close_file_item -> closeFile()
            R.id.save_as_template -> {
                saveFile(true)

                val t = Toast.makeText(applicationContext,
                        "File copied and saved as template.", Toast.LENGTH_SHORT)
                t.show()
            }
        }
        return true
    }

    private fun openFile() {
        if (currentFile === "untitled") {
            return
        }

        try {
            val file = File(DocUtils.notesPath(applicationContext) + currentFile!!)

            val fileReader = FileInputStream(file)
            val inputReader = InputStreamReader(fileReader)
            val reader = BufferedReader(inputReader)

            var line = reader.readLine()
            var content = ""

            while ( line != null) {
                content += line
                content += "\n"
                line = reader.readLine()
            }

            val editor = findViewById<View>(R.id.editor) as EditText
            editor.setText(content)

            modified = false
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun saveFileAs() {
        val saveDialog = Intent(this@EditorActivity, FileNameDialog::class.java)
        startActivityForResult(saveDialog, 0)
    }

    private fun saveFile(asTemplate: Boolean) {
        if (currentFile == "untitled") {
            saveFileAs()
            return
        }

        try {
            val file: File
            if (asTemplate) {
                file = File(DocUtils.templatesPath(applicationContext) + currentFile!!)
            } else {
                file = File(DocUtils.notesPath(applicationContext) + currentFile!!)
            }

            val fileWriter = FileOutputStream(file)
            val writer = OutputStreamWriter(fileWriter)

            val editor = findViewById<View>(R.id.editor) as EditText
            val text = editor.text.toString()

            writer.write(text)
            writer.flush()
            writer.close()

            modified = false
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun closeFile() {
        if (modified) {
            val builder = AlertDialog.Builder(this@EditorActivity)
            builder.setTitle("Confirm Close")
            builder.setMessage("This file has not been saved!")

            builder.setPositiveButton("Save") { dialogInterface, i ->
                saveFile(false)
                finish()
            }

            builder.setNegativeButton("Close") { dialogInterface, i -> finish() }

            builder.setNeutralButton("Cancel") { dialogInterface, i -> }

            val dialog = builder.create()
            dialog.show()
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode != 0) {
            return
        }

        currentFile = data.extras!!.getString("filename")

        val title = findViewById<View>(R.id.editor_toolbar) as Toolbar
        title.title = currentFile

        saveFile(false)
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    override fun afterTextChanged(editable: Editable) {
        modified = true
    }
}
