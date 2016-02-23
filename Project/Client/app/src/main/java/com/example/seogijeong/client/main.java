package com.example.seogijeong.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class main extends FragmentActivity {

    String ip = String.valueOf(R.string.ip);
    int port = R.string.port;

    private Socket socket;

    int dummy = 0, co2 = 0, dust = 0, humi = 0, temp = 0;
    TextView textView_total, textview_co2, textView_dust, textView_humi, textView_temp, textView_receive;
    EditText editText_ip, editText_port, editText_send;
    ImageButton imageButton_led;
    Button button_send;

    int led_on_off = 0;

    DataInputStream inputstream;
    DataOutputStream outputstream;
    String msg;
    Byte State_Socket = 0;
    LinearLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        write_value();
        connect();

    }

    public void main_imagebutton_led_click(View view)
    {
        led_on_off ^= 1;
        write_value();

        if (outputstream == null) return;
        try {
            outputstream.write(led_on_off);
            outputstream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.add(0, 0, 0, R.string.connect);
        menu.add(0, 1, 1, R.string.repeat);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        AlertDialog.Builder alert_main_menu = new AlertDialog.Builder(main.this);
        switch (id) {
            case 0:
                dummy = 40;
                write_value();
//                alert_main_menu.setMessage("진심입니까?").setCancelable(false)
//                        .setPositiveButton("아뇨", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        })
//                        .setNegativeButton("네", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(getApplicationContext(), "ㅈㅅ 미구현임", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                AlertDialog alertDialog = alert_main_menu.create();
//                alertDialog.setTitle("접속 확인");
//                alertDialog.setCancelable(false);
//                alertDialog.show();

                break;

            case 1:
//                Toast.makeText(getApplicationContext(), "미구현이라고", Toast.LENGTH_SHORT).show();
//                alert_main_menu.setMessage("자동 모드는 1초마다 갱신함 ㅇㅋ?\r\n자동 끌려면 한번 더 누르셈\r\n추가 통신 요금없으니 걱정 ㄴㄴ").setCancelable(false)
//                        .setPositiveButton("ㅇㅋ", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                auto_repeat ^= 1;
//                            }
//                        })
//                        .setNegativeButton("아몰랑", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        });
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (State_Socket != 0) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(getApplicationContext(), "종료합..ㄴ..ㅣ...ㄷ......", Toast.LENGTH_SHORT).show();
        finish();
    }


//    public void click_tcp_connect(View v)
//    {
//        ip = et_ip.getText().toString();
//        port = Integer.parseInt(et_port.getText().toString());
//
//        Intent intent = new Intent(getApplicationContext(), tcp.class);
//        intent.putExtra("ip", ip);
//        intent.putExtra("port", port);
//        startActivity(intent);
//    }

    public void main_send_click(View view) {
        editText_send = (EditText)findViewById(R.id.main_edittext_send);
        String str = editText_send.getText().toString();

        if (outputstream == null) return;
        try {
            outputstream.writeBytes(str);
            outputstream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {

        editText_ip = (EditText)findViewById(R.id.main_edittext_ip);
        editText_port = (EditText)findViewById(R.id.main_edittext_port);

        ip = editText_ip.getText().toString();
        port = Integer.valueOf(editText_port.getText().toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, port);
                    State_Socket = 1;
                    inputstream = new DataInputStream(socket.getInputStream());
                    outputstream = new DataOutputStream(socket.getOutputStream());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            while(true)
            {
                try {
                    dummy = inputstream.read();
                    msg = String.valueOf((char)dummy);

                    temp++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run () {
                            // TODO Auto-generated method stub
                            write_value();}});

                } catch (IOException e) {
                    e.printStackTrace();
                }

            //서버로부터 읽어들인 메시지msg를 TextView에 출력..
            //안드로이드는 오직 main Thread 만이 UI를 변경할 수 있기에
            //네트워크 작업을 하는 이 Thread에서는 TextView의 글씨를 직접 변경할 수 없음.
            //runOnUiThread()는 별도의 Thread가 main Thread에게 UI 작업을 요청하는 메소드임.

            }
            }}).start();
    }

    public void write_value() {

//        dummy = Integer.valueOf(String.valueOf(message));
//        dust = dummy/1000000;
//        humi = dummy/1000%1000;
//        temp = dummy%1000;
        dust = dummy;
        humi = 20*(1+dummy/100);
//        temp = 28*(1+dummy/100);

        textView_total = (TextView)findViewById(R.id.main_textview_total);
        textview_co2 = (TextView)findViewById(R.id.main_textview_co2);
        textView_dust = (TextView)findViewById(R.id.main_textview_dust);
        textView_humi = (TextView)findViewById(R.id.main_textview_humi);
        textView_temp = (TextView)findViewById(R.id.main_textview_temp);
        textView_receive = (TextView)findViewById(R.id.main_textview_receive);
        background = (LinearLayout)findViewById(R.id.main_background);
        imageButton_led = (ImageButton)findViewById(R.id.main_imagebutton_led);

        textView_receive.setTextSize(18);
        textView_receive.setText("From Server : "+msg);

//        textview_co2.setText("CO2\r\n"+co2+" ppm");
        if (led_on_off == 1) {
            textview_co2.setText("LED On");
            textview_co2.setTextColor(0xffffff6c);
        }
        else {
            textview_co2.setText("LED Off");
            textview_co2.setTextColor(0xff937800);
        }
        textview_co2.setTextSize(20);

        textView_dust.setText("미세먼지\r\n" + dust + " µg/m³");
        textView_dust.setTextSize(14);

        textView_humi.setText("습도\r\n"+humi + " %");
        textView_humi.setTextSize(14);

        textView_temp.setText("온도\r\n"+temp + " °C");
        textView_temp.setTextSize(14);

        textView_total.setTextSize(30);
        if(dust < 51) {
            textView_total.setText("좋음");
            textView_total.setTextColor(0xcc0054ff);
            textView_total.setBackgroundColor(Color.argb(200, 255, 255, 255));
            background.setBackgroundColor(Color.argb(200, 178, 204, 255));
            imageButton_led.setImageResource(R.drawable.state1);

        }
        else if(dust < 101) {
            textView_total.setText("보통");
            textView_total.setTextColor(0xccabf200);
            textView_total.setBackgroundColor(Color.argb(200, 255, 255, 255));
            background.setBackgroundColor(Color.argb(200, 171, 242, 0));
            imageButton_led.setImageResource(R.drawable.state2);
        }
//        else if(dust < 151) {
//            textView_total.setText("나쁨");
//            textView_total.setTextColor(0xccffbb00);
//            textView_total.setBackgroundColor(Color.argb(200, 255, 255, 255));
//            background.setBackgroundColor(Color.argb(200, 255, 187, 0));
//        }
        else {
            textView_total.setText("나쁨");
            textView_total.setTextColor(0xccff0000);
            textView_total.setBackgroundColor(Color.argb(200, 255, 255, 255));
            background.setBackgroundColor(Color.argb(255, 255, 0, 0));
            imageButton_led.setImageResource(R.drawable.state3);
        }
    }
}
