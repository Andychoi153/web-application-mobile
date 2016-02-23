package com.example.seogijeong.client;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class tcp extends ActionBarActivity {

    String ip;
    int port;
    ListView lv_ledcolor;
    TextView tv;
    String led[] = {"Red", "Green", "Blue", "Pink", "Yellow", "Skyblue", "On", "Off"};
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tcp);

        lv_ledcolor = (ListView)findViewById(R.id.lv_ledcolor);
        tv = (TextView)findViewById(R.id.tv);

        ArrayList al = new ArrayList();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
        for(int a = 0 ; a < led.length ; a++) al.add(led[a]);
        lv_ledcolor.setAdapter(adapter);

        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");
        port = intent.getIntExtra("port", 0);

        lv_ledcolor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //connect(position);
                tv.append(led[position]+"\r\n");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tcp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connect(int a) {
        try {
            socket = new Socket(ip,port);
            tv.append("Connected Success.\r\n");
            tv.append("Connected IP : "+ip+"\r\n");
            tv.append("Connected PORT : "+port+"\r\n");

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeUTF(led[a]);
            tv.append("Send : " + led[a] + "\r\n");

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            String string = inputStream.readUTF();
            tv.append("Received : "+string+"\r\n");

            socket.close();

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Connect Failed.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
