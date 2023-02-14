package com.example.remoteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remoteapp.databinding.ActivityMainBinding;

public class Main2Activity extends AppCompatActivity {
    private static Activity activity1;
    private ActivityMainBinding binding;
    private TextView pass;
    private Button btnStop;
    public static Activity getThis() {
        return (Activity) activity1;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        activity1 = (Activity) this;

        TCPConnection conn= SocketService.tcpConn;
        if(conn != null){

            btnStop = findViewById(R.id.buttonStop);
            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pass = findViewById(R.id.editTextPassword2);
                    if( pass.getText().toString().equals(conn.getPassword()) ){
                        Toast.makeText(Main2Activity.this, " -- STOP SERVICE -- ", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Main2Activity.this, "Contraseña Incorrecta", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Log.i("TEST_1", " ### CONN = NULL ###" );
        }

    }
}