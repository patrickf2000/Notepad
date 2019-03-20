package org.pdf4664.notepad.activities

import android.content.Intent
import android.os.Bundle
import org.pdf4664.notepad.utilities.DocUtils
import org.pdf4664.notepad.dialogs.FileNameDialog
import org.pdf4664.notepad.R
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
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

    private lateinit var fileContents: Array<String?>
    private var currentFile: String? = ""
    private var currentListItem = ""
    private lateinit var drawer: DrawerLayout

    private val NEW_FILE = 1
    private val NEW_FOLDER = 2
    private val RENAME_FILE = 3

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

    private fun renameFile() {
        val path = DocUtils.notesPath(applicationContext) + currentListItem
        val file = File(path)

        val file2 = File(DocUtils.notesPath(applicationContext), currentFile!!)
        file.renameTo(file2)

        loadList()
    }

    private fun deleteFile() {
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

    private fun createFile() {
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

    private fun createFolder() {
        try {
            val file = DocUtils.notesPath(applicationContext) + currentFile!!

            val f = File(file)
            f.mkdir()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        loadList()
    }

    private fun showWebView(file_name: String) {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer.openDrawer(GravityCompat.START)
            }

            R.id.new_folder -> {
                val fileDialog = Intent(this, FileNameDialog::class.java)
                startActivityForResult(fileDialog, NEW_FOLDER)
            }

            R.id.nav_up -> {
                DocUtils.navUp()
                loadList()
            }
        }
        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.chooser_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val listItem = getListItem(info.id)
        currentListItem = listItem

        when (item.itemId) {
            R.id.rename_item -> {
                val fileDialog = Intent(this@ChooserActivity, FileNameDialog::class.java)
                startActivityForResult(fileDialog, RENAME_FILE)
            }
            R.id.delete_item -> {
                deleteFile()
            }
            R.id.show_web_view -> {
                showWebView(listItem)
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
                val aboutText = "Notepad\n" +
                        "Version 1.0\n\n" +
                        "Notepad is licensed under the BSD license."

                val toast = Toast.makeText(applicationContext, aboutText, Toast.LENGTH_LONG)
                toast.show()
            }
        }

        drawer.closeDrawers()
        return true
    }


    override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        val selected = fileContents[i]

        val path = DocUtils.notesPath(applicationContext) + selected!!
        val f = File(path)
        if (f.isDirectory) {
            DocUtils.prefix += selected + File.separator
            loadList()
            return
        }

        val editor = Intent(this@ChooserActivity, EditorActivity::class.java)
        editor.putExtra("file", selected)
        startActivity(editor)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != 0) {
            return
        }

        try {
            currentFile = data?.extras?.getString("filename")
        } catch (e: Exception) {
            return
        }

        when (requestCode) {
            NEW_FILE -> createFile()
            NEW_FOLDER -> createFolder()
            RENAME_FILE -> renameFile()
        }
    }

    override fun onResume() {
        super.onResume()
        loadList()
    }
}
