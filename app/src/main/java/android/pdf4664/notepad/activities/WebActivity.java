package android.pdf4664.notepad.activities;

import android.content.Intent;
import android.pdf4664.notepad.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        String content = intent.getExtras().getString("file");

        WebView web = (WebView)findViewById(R.id.web_view);
        web.loadData(content, "text/html; charset=utf-8", "utf-8");
    }
}
