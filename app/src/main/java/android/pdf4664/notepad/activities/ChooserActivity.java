package android.pdf4664.notepad.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.pdf4664.notepad.utilities.DocUtils;
import android.pdf4664.notepad.dialogs.FileNameDialog;
import android.pdf4664.notepad.R;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ChooserActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    String[] fileContents;
    String currentFile = "";
    String currentListItem = "";
    DrawerLayout drawer;

    final int NEW_FILE = 1;
    final int RENAME_FILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        Toolbar toolbar = (Toolbar)findViewById(R.id.chooser_toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout)findViewById(R.id.chooser_layout);

        NavigationView drawerArea = (NavigationView)findViewById(R.id.nav_area);
        drawerArea.setNavigationItemSelectedListener(this);

        FloatingActionButton newFileButton = (FloatingActionButton)findViewById(R.id.new_file_button);
        newFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fileNameDialog = new Intent(ChooserActivity.this,FileNameDialog.class);
                startActivityForResult(fileNameDialog,NEW_FILE);
                create_file();
            }
        });

        DocUtils.checkNotesPath(getApplicationContext());
        DocUtils.checkTemplatesPath(getApplicationContext());
        loadList();
    }

    private void loadList() {
        fileContents = DocUtils.notesFileContents(getApplicationContext());

        ListView fileList = (ListView)findViewById(R.id.file_list);
        ArrayAdapter<String> contents = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,fileContents);
        fileList.setAdapter(contents);
        fileList.setOnItemClickListener(this);
        registerForContextMenu(fileList);

        TextView emptyText = (TextView)findViewById(R.id.empty_text);
        fileList.setEmptyView(emptyText);
    }

    private String getListItem(long id) {
        String ret = "";

        ListView fileList = (ListView)findViewById(R.id.file_list);
        for (int pos = 0; pos<fileList.getAdapter().getCount(); pos++) {
            if (fileList.getAdapter().getItemId(pos)==id) {
                ret = fileList.getAdapter().getItem(pos).toString();
            }
        }

        return ret;
    }

    private void rename_file() {
        String path = DocUtils.notesPath(getApplicationContext())+currentListItem;
        File file = new File(path);

        File file2 = new File(DocUtils.notesPath(getApplicationContext()),currentFile);
        file.renameTo(file2);

        loadList();
    }

    private void delete_file() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChooserActivity.this);
        alertDialog.setTitle("Confirm Delete");
        alertDialog.setMessage("Are you sure you wish to delete this note?");

        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String path = DocUtils.notesPath(getApplicationContext())+currentListItem;
                File file = new File(path);
                boolean del = file.delete();

                if (!del) {
                    Log.e("FILE ERROR","Unable to delete file.");
                }

                loadList();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void create_file() {
        try {
            String file = DocUtils.notesPath(getApplicationContext())+currentFile;

            FileOutputStream fileWriter = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fileWriter);
            writer.write("");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadList();
    }

    private void show_web_view(String file_name) {
        String text = "<h1>Hello!</h1><h2>Test!</h2>";

        try {
            String path = DocUtils.notesPath(getApplicationContext())+file_name;
            File file = new File(path);

            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = "";
            text = "";

            while ((line=reader.readLine())!=null) {
                text+=line;
                text+="\n";
            }

            reader.close();
        } catch (Exception e) {
            Toast t = Toast.makeText(getApplicationContext(),"Unable to open file",Toast.LENGTH_SHORT);
            t.show();
        }

        Intent intent = new Intent(ChooserActivity.this,WebActivity.class);
        intent.putExtra("file",text);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chooser_toolbar,menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,v,menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chooser_context_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_file_item: {
                Intent fileNameDialog = new Intent(ChooserActivity.this,FileNameDialog.class);
                startActivityForResult(fileNameDialog,NEW_FILE);
                create_file();
            } break;
            case android.R.id.home: {
                drawer.openDrawer(GravityCompat.START);
            } break;
        }
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String list_item = getListItem(info.id);
        currentListItem = list_item;

        switch (item.getItemId()) {
            case R.id.rename_item: {
                Intent fileDialog = new Intent(ChooserActivity.this,FileNameDialog.class);
                startActivityForResult(fileDialog,RENAME_FILE);
            } break;
            case R.id.delete_item: {
                delete_file();
            } break;
            case R.id.show_web_view: {
                show_web_view(list_item);
            } break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_templates: {
                Intent intent = new Intent(ChooserActivity.this,TemplateActivity.class);
                startActivity(intent);
            } break;
            case R.id.about_item: {
                String about_text = "Notepad\n" +
                        "Version 1.0\n\n" +
                        "Notepad is licensed under the BSD license.";

                Toast toast = Toast.makeText(getApplicationContext(),about_text,Toast.LENGTH_LONG);
                toast.show();
            } break;
        }

        drawer.closeDrawers();
        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String selected = fileContents[i];

        Intent editor = new Intent(ChooserActivity.this,EditorActivity.class);
        editor.putExtra("file",selected);
        startActivity(editor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode!=0) {
            return;
        }

        currentFile = data.getExtras().getString("filename");

        switch (requestCode) {
            case NEW_FILE: create_file(); break;
            case RENAME_FILE: rename_file(); break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }
}
