package android.pdf4664.notepad.activities;

import android.os.Bundle;
import android.pdf4664.notepad.utilities.DocUtils;
import android.pdf4664.notepad.dialogs.FileNameDialog;
import android.pdf4664.notepad.R;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class EditorActivity extends AppCompatActivity {

    private String currentFile = "untitled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentFile = intent.getExtras().getString("file");

        Toolbar toolbar = (Toolbar)findViewById(R.id.editor_toolbar);
        toolbar.setTitle(currentFile);
        setSupportActionBar(toolbar);

        openFile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_file_item: saveFile(false); break;
            case R.id.close_file_item: closeFile(); break;
            case R.id.save_as_template: {
                saveFile(true);

                Toast t = Toast.makeText(getApplicationContext(),
                        "File copied and saved as template.", Toast.LENGTH_SHORT);
                t.show();
            } break;
        }
        return true;
    }

    private void openFile() {
        if (currentFile=="untitled") {
            return;
        }

        try {
            File file = new File(DocUtils.notesPath(getApplicationContext())+currentFile);

            FileInputStream fileReader = new FileInputStream(file);
            InputStreamReader inputReader = new InputStreamReader(fileReader);
            BufferedReader reader = new BufferedReader(inputReader);

            String line = "";
            String content = "";

            while ((line=reader.readLine())!=null) {
                content+=line;
                content+="\n";
            }

            EditText editor = (EditText)findViewById(R.id.editor);
            editor.setText(content);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveFileAs() {
        Intent saveDialog = new Intent(EditorActivity.this,FileNameDialog.class);
        startActivityForResult(saveDialog,0);
    }

    private void saveFile(boolean asTemplate) {
        if (currentFile.equals("untitled")) {
            saveFileAs();
            return;
        }

        try {
            File file;
            if (asTemplate) {
                file = new File(DocUtils.templatesPath(getApplicationContext())+currentFile);
            } else {
                file = new File(DocUtils.notesPath(getApplicationContext())+currentFile);
            }

            FileOutputStream fileWriter = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fileWriter);

            EditText editor = (EditText)findViewById(R.id.editor);
            String text = editor.getText().toString();

            writer.write(text);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeFile() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode!=0) {
            return;
        }

        currentFile = data.getExtras().getString("filename");

        Toolbar title = (Toolbar) findViewById(R.id.editor_toolbar);
        title.setTitle(currentFile);

        saveFile(false);
    }
}
