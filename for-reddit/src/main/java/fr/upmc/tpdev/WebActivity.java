package fr.upmc.tpdev;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        final WebView webView = findViewById(R.id.wv_url);

        Intent thisIntent = getIntent();
        String url = thisIntent.getStringExtra("url");

        webView.loadUrl(url);
    }
}
