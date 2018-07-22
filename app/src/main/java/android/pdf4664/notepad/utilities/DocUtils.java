package android.pdf4664.notepad.utilities;

import android.content.Context;

import java.io.File;

public class DocUtils {

    private static String[] dirContents(Context c, boolean loadTemplates) {
        File file;

        if (loadTemplates) {
            file = new File(DocUtils.templatesPath(c.getApplicationContext()));
        } else {
            file = new File(DocUtils.notesPath(c.getApplicationContext()));
        }

        File[] fs = file.listFiles();

        String[] fileContents = new String[fs.length];
        for (int i = 0; i<fs.length; i++) {
            if (fs[i].getName()=="null") {
                continue;
            }
            fileContents[i] = fs[i].getName();
        }

        return fileContents;
    }

    //Utilities for the notes folder
    public static String notesPath(Context c) {
        String path = c.getFilesDir().getAbsolutePath();
        path+= File.separator;
        path+="notes"+File.separator;
        return path;
    }

    public static void checkNotesPath(Context c) {
        File f = new File(notesPath(c.getApplicationContext()));
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public static String[] notesFileContents(Context c) {
        return dirContents(c,false);
    }

    //Utilities for the templates folder
    public static String templatesPath(Context c) {
        String path = c.getFilesDir().getAbsolutePath();
        path+= File.separator;
        path+="templates"+File.separator;
        return path;
    }

    public static void checkTemplatesPath(Context c) {
        File f = new File(templatesPath(c.getApplicationContext()));
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public static String[] templateFileContents(Context c) {
        return dirContents(c,true);
    }
}
