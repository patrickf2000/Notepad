package org.pdf4664.notepad.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import org.pdf4664.notepad.utilities.DocUtils
import org.pdf4664.notepad.dialogs.FileNameDialog
import org.pdf4664.notepad.utilities.FileUtils
import org.pdf4664.notepad.R
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

import java.io.File

class TemplateActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var currentListItem = ""

    internal val USE_TEMPLATE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template)

        val toolbar = findViewById<View>(R.id.template_toolbar) as Toolbar
        toolbar.title = "Templates"
        setSupportActionBar(toolbar)

        val list = findViewById<View>(R.id.template_list) as ListView
        list.onItemClickListener = this

        loadList()
    }

    private fun loadList() {
        val contents = DocUtils.templateFileContents(applicationContext)

        val list = findViewById<View>(R.id.template_list) as ListView
        val listContents = ArrayAdapter(this,
                android.R.layout.simple_expandable_list_item_1, contents)
        list.adapter = listContents
        registerForContextMenu(list)
    }

    private fun getListItem(id: Long): String {
        var ret = ""

        val fileList = findViewById<View>(R.id.template_list) as ListView
        for (pos in 0 until fileList.adapter.count) {
            if (fileList.adapter.getItemId(pos) == id) {
                ret = fileList.adapter.getItem(pos).toString()
            }
        }

        return ret
    }

    private fun copyTemplate(fileName: String?) {
        val one = File(DocUtils.templatesPath(applicationContext) + currentListItem)
        val two = File(DocUtils.notesPath(applicationContext) + fileName!!)

        FileUtils.copy(one, two)
    }

    private fun useTemplate() {
        val intent = Intent(this@TemplateActivity, FileNameDialog::class.java)
        startActivityForResult(intent, USE_TEMPLATE)
    }

    private fun deleteTemplate() {
        val builder = AlertDialog.Builder(this@TemplateActivity)
        builder.setTitle("Confirm Delete")
        builder.setMessage("Are you sure you wish to delete this template?")

        builder.setPositiveButton("Delete") { dialogInterface, i ->
            val path = DocUtils.templatesPath(applicationContext) + currentListItem
            val file = File(path)
            file.delete()

            loadList()
        }

        builder.setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.cancel() }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.template_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val list_item = getListItem(info.id)
        currentListItem = list_item

        when (item.itemId) {
            R.id.delete_template_item -> deleteTemplate()
        }
        return true
    }

    override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        val list = adapterView as ListView
        currentListItem = list.getItemAtPosition(i).toString()
        useTemplate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        try {
            val file = data.extras!!.getString("filename")

            when (requestCode) {
                USE_TEMPLATE -> {
                    copyTemplate(file)
                    finish()
                }
            }
        } catch (e: NullPointerException) {
        }

    }
}
