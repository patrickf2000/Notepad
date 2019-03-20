package org.pdf4664.notepad.utilities

import android.content.Context
import android.util.Log

import java.io.File

object DocUtils {

    var prefix : String = ""

    private fun dirContents(c: Context, loadTemplates: Boolean): Array<String?> {
        val file: File

        if (loadTemplates) {
            file = File(DocUtils.templatesPath(c.applicationContext))
        } else {
            file = File(DocUtils.notesPath(c.applicationContext))
        }

        val fs = file.listFiles()

        val fileContents = arrayOfNulls<String>(fs.size)
        for (i in fs.indices) {
            if (fs[i].name === "null") {
                continue
            }
            fileContents[i] = fs[i].name
        }

        return fileContents
    }

    //Utilities for the notes folder
    fun notesPath(c: Context): String {
        var path = c.filesDir.absolutePath
        path += File.separator
        path += "notes" + File.separator
        path += prefix
        Log.i("PATH_INFO",path)
        return path
    }

    fun navUp() {
        prefix = prefix.dropLast(1)

        if (!prefix.contains("/")) {
            prefix = ""
            return
        }

        val last = prefix.lastIndexOf("/")
        prefix = prefix.substring(0,last)
        prefix += File.separator
    }

    fun checkNotesPath(c: Context) {
        val f = File(notesPath(c.applicationContext))
        if (!f.exists()) {
            f.mkdirs()
        }
    }

    fun notesFileContents(c: Context): Array<String?> {
        return dirContents(c, false)
    }

    //Utilities for the templates folder
    fun templatesPath(c: Context): String {
        var path = c.filesDir.absolutePath
        path += File.separator
        path += "templates" + File.separator
        return path
    }

    fun checkTemplatesPath(c: Context) {
        val f = File(templatesPath(c.applicationContext))
        if (!f.exists()) {
            f.mkdirs()
        }
    }

    fun templateFileContents(c: Context): Array<String?> {
        return dirContents(c, true)
    }
}
