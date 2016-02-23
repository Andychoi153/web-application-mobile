package com.example.user.ndsocket_tcp;

import java.io.*;
import java.net.*;

import android.app.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    EditText mEditAddr;
    EditText mEditPort;
    EditText mEditSend;
    TextView mTextMessage;
    Socket mSock = null;
    BufferedReader mReader;
    BufferedWriter mWriter;
    String mRecvData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditAddr = (EditText)findViewById(R.id.editAddr);
        mEditPort = (EditText)findViewById(R.id.editPort);
        mEditSend = (EditText)findViewById(R.id.editSend);
        mTextMessage = (TextView)findViewById(R.id.textMessage);
    }

    // 접속 종료
    public void onBtnClose() {
        try {
            if( mSock != null ) {
                // 쓰레드 중지
                mCheckRecv.stop();
                // 접속 종료 메시지를 서버에 전달
                mEditSend.setText("Disconnect");
                onBtnSend();
                // 소켓 접속 종료
                mSock.close();
                mSock = null;
            }
        } catch(Exception e) {
            Log.d("tag", "Socket close error.");
        }
    }

    // 접속 시작
    public void onBtnConnect() {
        try {
            // 접속 종료
            onBtnClose();
            // IP 주소와 포트번호를 구한다
            String serverAddr = "192.168.3.142";
            String strPort = mEditPort.getText().toString();
            int nPort = 51000;

            // 서버와 접속 시작
            mSock = new Socket(serverAddr, nPort);
            mWriter = new BufferedWriter(new OutputStreamWriter(mSock.getOutputStream()));
            mReader = new BufferedReader(new InputStreamReader(mSock.getInputStream()));
            mCheckRecv.start();
        } catch(Exception e) {
            Log.d("tag", "Socket connect error.");
        }
    }

    // 서버에 데이터 전송
    public void onBtnSend() {
        try {
            // EditText 에서 메시지를 구한 다음 기존 메시지를 삭제한다
            String strSend = mEditSend.getText().toString();
            mEditSend.setText("");
            // 서버로 메시지 전송
            PrintWriter out = new PrintWriter(mWriter, true);
            out.println(strSend);
        } catch(Exception e) {
            Log.d("tag", "Data send error.");
        }
    }

    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.btnConnect :
                // 접속 시작
                onBtnConnect();
                break;
            case R.id.btnClose :
                // 접속 종료
                onBtnClose();
                break;
            case R.id.btnSend :
                // 서버에 데이터 전송
                onBtnSend();
                break;
        }
    }

    // 메시지를 화면에 표시하는 이벤트 핸들러
    Handler mReceiver = new Handler() {
        public void handleMessage(Message msg) {
            mTextMessage.setText(mRecvData);
        }
    };

    // 서버에서 메시지를 수신하는 쓰레드
    private Thread mCheckRecv = new Thread() {
        public void run() {
            try {
                while (true) {
                    // 입력 스트림에서 메시지를 읽는다
                    mRecvData = mReader.readLine();
                    // 이벤트 핸들러에 이벤트를 전달
                    mReceiver.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                Log.d("tag", "Receive error");
            }
        }
    };

}