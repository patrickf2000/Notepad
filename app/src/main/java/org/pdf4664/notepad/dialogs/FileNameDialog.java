package org.pdf4664.notepad.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.pdf4664.notepad.R;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FileNameDialog extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_name_dialog_layout);

        Button submit = (Button)findViewById(R.id.fileDialogSubmit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()!=R.id.fileDialogSubmit) {
            return;
        }

        EditText entry = (EditText)findViewById(R.id.fileDialogEntry);
        String result = entry.getText().toString();

        if (result.isEmpty()) {
            finish();
        }

        Intent intent = new Intent();
        intent.putExtra("filename",result);
        setResult(0,intent);

        finish();
    }
}
