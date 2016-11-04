package com.haykuproductions.krome;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

import static com.haykuproductions.krome.R.id.imageView;

public class MainActivity extends AppCompatActivity {

    WebView wv;
    AutoCompleteTextView tv;
    AdminSQL db;
    ArrayAdapter<String> adapter;
    ImageView im;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wv = (WebView) findViewById(R.id.webView);
        tv = (AutoCompleteTextView) findViewById(R.id.etUrl);
        im = (ImageView) findViewById(R.id.imageView);

        wv.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView ue, String url, Bitmap e){
                tv.setText(url);
            }
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                Toast.makeText(getApplicationContext(), "ERROR CARGANDO", Toast.LENGTH_SHORT).show();
            }
        });

        tv.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        searchWeb();
                        return true;
                    }
                }
                return false;
            }
        });

        db = new AdminSQL(getApplicationContext());
        actualizaAdapter();
        tv.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id){
            case R.id.btHome:     wv.loadUrl("https://www.google.es"); im.setVisibility(View.GONE); break;
            case R.id.btPrev:     goBack();break;
            case R.id.btNext:     goForward();break;
            case R.id.btReload:   wv.reload();break;
            case R.id.btBorrar:   borrarHistorial();break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void searchWeb(){
        String url = tv.getText().toString();
        if(url.substring(0,4).equals("http")){
            wv.loadUrl(url);
            im.setVisibility(View.GONE);

        }else{
            wv.loadUrl("http://"+url);
            im.setVisibility(View.GONE);
        }

        guardaUrl(tv.getText().toString());
        actualizaAdapter();
    }

    public void borrarHistorial(){
        db.borraTodo();
        actualizaAdapter();
        Toast.makeText(getApplicationContext(), "HISTORIAL BORRADO", Toast.LENGTH_SHORT).show();
    }

    public void guardaUrl(String url){
        db.agregar(url);
    }

    public void actualizaAdapter(){
        Vector v = db.obtenerSugerencias();
        String[] str = new String[v.size()];
        for(int i=0; i<v.size(); i++){
            str[i]=v.get(i).toString();
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, str);
        tv.setAdapter(adapter);
    }

    public void goBack(){
        if(wv.canGoBack())
            wv.goBack();
    }

    public void goForward(){
        if(wv.canGoForward())
            wv.goForward();
    }
}
