package org.pdf4664.notepad.utilities

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object FileUtils {

    fun copy(file1: File, file2: File) {
        val templateContent = fileContent(file1)
        writeToFile(file2, templateContent)
    }

    private fun fileContent(file: File): String {
        var ret = ""

        var fileInputStream: FileInputStream? = null
        try {
            fileInputStream = FileInputStream(file)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val reader = BufferedReader(inputStreamReader)

            var line = reader.readLine()
            while (line != null) {
                ret += line
                ret += "\n"
                line = reader.readLine()
            }

            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ret
    }

    private fun writeToFile(file: File, content: String) {
        try {
            val outputStream = FileOutputStream(file)
            val writer = OutputStreamWriter(outputStream)

            writer.write(content)
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
