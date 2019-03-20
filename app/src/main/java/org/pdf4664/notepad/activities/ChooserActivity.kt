package org.pdf4664.notepad.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import org.pdf4664.notepad.utilities.DocUtils
import org.pdf4664.notepad.dialogs.FileNameDialog
import org.pdf4664.notepad.R
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class ChooserActivity : AppCompatActivity(), AdapterView.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    internal lateinit var fileContents: Array<String?>
    internal var currentFile: String? = ""
    internal var currentListItem = ""
    internal lateinit var drawer: DrawerLayout

    internal val NEW_FILE = 1
    internal val RENAME_FILE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chooser)

        val toolbar = findViewById<View>(R.id.chooser_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        drawer = findViewById<View>(R.id.chooser_layout) as DrawerLayout

        val drawerArea = findViewById<View>(R.id.nav_area) as NavigationView
        drawerArea.setNavigationItemSelectedListener(this)

        val newFileButton = findViewById<View>(R.id.new_file_button) as FloatingActionButton
        newFileButton.setOnClickListener {
            val fileNameDialog = Intent(this@ChooserActivity, FileNameDialog::class.java)
            startActivityForResult(fileNameDialog, NEW_FILE)
        }

        DocUtils.checkNotesPath(applicationContext)
        DocUtils.checkTemplatesPath(applicationContext)
        loadList()
    }

    private fun loadList() {
        fileContents = DocUtils.notesFileContents(applicationContext)

        val fileList = findViewById<View>(R.id.file_list) as ListView
        val contents = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileContents)
        fileList.adapter = contents
        fileList.onItemClickListener = this
        registerForContextMenu(fileList)

        val emptyText = findViewById<View>(R.id.empty_text) as TextView
        fileList.emptyView = emptyText
    }

    private fun getListItem(id: Long): String {
        var ret = ""

        val fileList = findViewById<View>(R.id.file_list) as ListView
        for (pos in 0 until fileList.adapter.count) {
            if (fileList.adapter.getItemId(pos) == id) {
                ret = fileList.adapter.getItem(pos).toString()
            }
        }

        return ret
    }

    private fun rename_file() {
        val path = DocUtils.notesPath(applicationContext) + currentListItem
        val file = File(path)

        val file2 = File(DocUtils.notesPath(applicationContext), currentFile!!)
        file.renameTo(file2)

        loadList()
    }

    private fun delete_file() {
        val alertDialog = AlertDialog.Builder(this@ChooserActivity)
        alertDialog.setTitle("Confirm Delete")
        alertDialog.setMessage("Are you sure you wish to delete this note?")

        alertDialog.setPositiveButton("Delete") { dialogInterface, i ->
            val path = DocUtils.notesPath(applicationContext) + currentListItem
            val file = File(path)
            val del = file.delete()

            if (!del) {
                Log.e("FILE ERROR", "Unable to delete file.")
            }

            loadList()
        }

        alertDialog.setNegativeButton("Cancel") { dialogInterface, i -> }

        val dialog = alertDialog.create()
        dialog.show()
    }

    private fun create_file() {
        try {
            val file = DocUtils.notesPath(applicationContext) + currentFile!!

            val fileWriter = FileOutputStream(file)
            val writer = OutputStreamWriter(fileWriter)
            writer.write("")
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        loadList()
    }

    private fun show_web_view(file_name: String) {
        var text = "<h1>Hello!</h1><h2>Test!</h2>"

        try {
            val path = DocUtils.notesPath(applicationContext) + file_name
            val file = File(path)

            val fileInputStream = FileInputStream(file)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val reader = BufferedReader(inputStreamReader)

            var line = reader.readLine()
            text = ""

            while (line != null) {
                text += line
                text += "\n"
                line = reader.readLine()
            }

            reader.close()
        } catch (e: Exception) {
            val t = Toast.makeText(applicationContext, "Unable to open file", Toast.LENGTH_SHORT)
            t.show()
        }

        val intent = Intent(this@ChooserActivity, WebActivity::class.java)
        intent.putExtra("file", text)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chooser_toolbar, menu)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)

        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.chooser_context_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer.openDrawer(GravityCompat.START)
            }
        }
        return true
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val list_item = getListItem(info.id)
        currentListItem = list_item

        when (item.itemId) {
            R.id.rename_item -> {
                val fileDialog = Intent(this@ChooserActivity, FileNameDialog::class.java)
                startActivityForResult(fileDialog, RENAME_FILE)
            }
            R.id.delete_item -> {
                delete_file()
            }
            R.id.show_web_view -> {
                show_web_view(list_item)
            }
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_templates -> {
                val intent = Intent(this@ChooserActivity, TemplateActivity::class.java)
                startActivity(intent)
            }
            R.id.about_item -> {
                val about_text = "Notepad\n" +
                        "Version 1.0\n\n" +
                        "Notepad is licensed under the BSD license."

                val toast = Toast.makeText(applicationContext, about_text, Toast.LENGTH_LONG)
                toast.show()
            }
        }

        drawer.closeDrawers()
        return true
    }


    override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        val selected = fileContents[i]

        val editor = Intent(this@ChooserActivity, EditorActivity::class.java)
        editor.putExtra("file", selected)
        startActivity(editor)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode != 0) {
            return
        }

        try {
            currentFile = data.extras!!.getString("filename")
        } catch (e: Exception) {
            return
        }

        when (requestCode) {
            NEW_FILE -> create_file()
            RENAME_FILE -> rename_file()
        }
    }

    override fun onResume() {
        super.onResume()
        loadList()
    }
}
