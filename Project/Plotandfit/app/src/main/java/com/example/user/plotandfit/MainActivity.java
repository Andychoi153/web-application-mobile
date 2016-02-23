package com.example.user.plotandfit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Acztivity {

    WebView webViewMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        webViewMain = (WebView) findViewById(R.id.webViewMain);
        webSettings webSettings = webViewMain.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webViewMain.setWebViewClient(new WebViewClient() {

        });
        webViewMain.setWebChromeClient(new WebChromeClient() {

        });
        webViewMain.loadUrl("http://andychoi.gonetis.com:8888/upex.php");



    }
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        if (webViewMain.canGoBack()) {
            webViewMain.goBack();
        } else {
            finish();
        }
    }



}
