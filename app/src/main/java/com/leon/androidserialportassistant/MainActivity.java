package com.leon.androidserialportassistant;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

public class MainActivity extends AppCompatActivity {

    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private TextView mTextRecv;
    private EditText mEditSend;
    private Button mButtonSerialPort;
    private Button mButtonSend;
    private FragmentManager mFragmentManager;
    private SerialPort mSerialPort;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Set setting fragment */
        mFragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.layoutSetting,new SettingFragment(),"SETTING_FRAGMENT");
        fragmentTransaction.commit();

        mTextRecv = (TextView) findViewById(R.id.tvRecv);
        mEditSend = (EditText) findViewById(R.id.etSend);
        mButtonSerialPort = (Button) findViewById(R.id.btnSerialPort);
        mButtonSerialPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingFragment fragment = (SettingFragment) mFragmentManager.findFragmentByTag("SETTING_FRAGMENT");
                if (mSerialPort == null) {
                    /* Read serial port parameters */
                    SharedPreferences sharedPreferences = getSharedPreferences("com.leon.androidserialportassistant_preferences",MODE_PRIVATE);
                    String path = sharedPreferences.getString("DEVICE","");
                    int baudrate = Integer.decode(sharedPreferences.getString("BAUDRATE", "-1"));
                    /* Check parameters */
                    if ( (path.length() == 0) || (baudrate == -1)) {
                        throw new InvalidParameterException();
                    }
                    try {
                        /* Open serial port */
                        mSerialPort = new SerialPort(new File(path),baudrate,0);
                        mInputStream = mSerialPort.getInputStream();
                        mOutputStream = mSerialPort.getOutputStream();
                        mButtonSerialPort.setText("关闭串口");
                        /* Start read serial port thread */
                        new RecvThread().start();
                        /* Disable setting fragment parameters */
                        fragment.mListBaudrate.setEnabled(false);
                        fragment.mListDevice.setEnabled(false);

                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "串口打开失败！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                     /* Close serial port */
                    mSerialPort.close();
                    mSerialPort = null;
                    mButtonSerialPort.setText("打开串口");
                    /* Enable setting fragment parameters */
                    fragment.mListDevice.setEnabled(true);
                    fragment.mListBaudrate.setEnabled(true);
                }
            }
        });

        mButtonSend = (Button) findViewById(R.id.btnSend);
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Check serial port */
                if (mSerialPort ==null) {
                    Toast.makeText(MainActivity.this, "请先打开串口", Toast.LENGTH_SHORT).show();
                    return;
                }
                /* Check messages */
                String msg = mEditSend.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(MainActivity.this, "输入为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO: 2016-05-05 setting_send 
                /* Write serial port */
                try {
                    mOutputStream.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private class RecvThread extends Thread {

        @Override
        public void run() {
            while (!isInterrupted()) {
                int size;
                byte[] buffer = new byte[64];
                if (mInputStream == null) {
                    return;
                }
                try {
                    size = mInputStream.read(buffer);
                    if (size>0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void onDataReceived(final byte[] buffer, final int size) {
        // TODO: 2016-05-05 setting_recv 
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextRecv.append(new String(buffer), 0, size);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mSerialPort!=null) {
                    Toast.makeText(MainActivity.this, "请先关闭串口", Toast.LENGTH_SHORT).show();
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }
}
