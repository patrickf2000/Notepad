package org.pdf4664.notepad.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import org.pdf4664.notepad.utilities.DocUtils;
import org.pdf4664.notepad.dialogs.FileNameDialog;
import org.pdf4664.notepad.utilities.FileUtils;
import org.pdf4664.notepad.R;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

public class TemplateActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String currentListItem = "";

    final int USE_TEMPLATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);

        Toolbar toolbar = (Toolbar)findViewById(R.id.template_toolbar);
        toolbar.setTitle("Templates");
        setSupportActionBar(toolbar);

        ListView list = (ListView)findViewById(R.id.template_list);
        list.setOnItemClickListener(this);

        loadList();
    }

    private void loadList() {
        String[] contents = DocUtils.templateFileContents(getApplicationContext());

        ListView list = (ListView)findViewById(R.id.template_list);
        ArrayAdapter<String> listContents = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1,contents);
        list.setAdapter(listContents);
        registerForContextMenu(list);
    }

    private String getListItem(long id) {
        String ret = "";

        ListView fileList = (ListView)findViewById(R.id.template_list);
        for (int pos = 0; pos<fileList.getAdapter().getCount(); pos++) {
            if (fileList.getAdapter().getItemId(pos)==id) {
                ret = fileList.getAdapter().getItem(pos).toString();
            }
        }

        return ret;
    }

    private void copyTemplate(String fileName) {
        File one = new File(DocUtils.templatesPath(getApplicationContext())+currentListItem);
        File two = new File(DocUtils.notesPath(getApplicationContext())+fileName);

        FileUtils.copy(one,two);
    }

    private void useTemplate() {
        Intent intent = new Intent(TemplateActivity.this,FileNameDialog.class);
        startActivityForResult(intent,USE_TEMPLATE);
    }

    private void deleteTemplate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TemplateActivity.this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you wish to delete this template?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String path = DocUtils.templatesPath(getApplicationContext())+currentListItem;
                File file = new File(path);
                file.delete();

                loadList();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,v,menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.template_context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String list_item = getListItem(info.id);
        currentListItem = list_item;

        switch (item.getItemId()) {
            case R.id.delete_template_item: deleteTemplate(); break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ListView list = (ListView)adapterView;
        currentListItem = list.getItemAtPosition(i).toString();
        useTemplate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            String file = data.getExtras().getString("filename");

            switch (requestCode) {
                case USE_TEMPLATE: {
                    copyTemplate(file);
                    finish();
                } break;
            }
        } catch (NullPointerException e) {
        }
    }
}
