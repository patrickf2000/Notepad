package org.pdf4664.notepad.activities

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import org.pdf4664.notepad.R
import org.pdf4664.notepad.utilities.DocUtils
import java.io.File

class FileListAdapter : BaseAdapter {

    private var activity : Activity
    private var data: Array<String?>
    private var inflater : LayoutInflater

    constructor(a: Activity, d: Array<String?>) {
        this.activity = a
        this.data = d
        this.inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(pos: Int, v: View?, vg: ViewGroup?): View {
        var view: View = inflater.inflate(R.layout.list_row, null)

        val title : TextView = view.findViewById(R.id.file_name)
        val img : ImageView = view.findViewById(R.id.list_img)


        val item = data.get(pos)
        title.text = item

        val path = DocUtils.notesPath(activity) + item
        if (File(path).isDirectory) {
            img.setImageResource(R.drawable.folder)
        } else {
            img.setImageResource(R.drawable.file)
        }

        return view
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        val l : Long = 0
        return l
    }

    override fun getCount(): Int {
        return data.size;
    }

}