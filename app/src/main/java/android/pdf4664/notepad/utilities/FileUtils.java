package android.pdf4664.notepad.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtils {

    public static void copy(File file1, File file2) {
        String templateContent = fileContent(file1);
        writeToFile(file2,templateContent);
    }

    public static String fileContent(File file) {
        String ret = "";

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = "";
            while ((line=reader.readLine())!=null) {
                ret+=line;
                ret+="\n";
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static void writeToFile(File file, String content) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);

            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
