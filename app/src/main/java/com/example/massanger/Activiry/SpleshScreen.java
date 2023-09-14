package com.example.massanger.Activiry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.massanger.R;
import com.google.firebase.auth.FirebaseAuth;

public class SpleshScreen extends AppCompatActivity {

    String msgId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);



        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseAuth.getInstance().getCurrentUser()==null) {

                    startActivity(new Intent(SpleshScreen.this, Login.class));
                    finish();

                }else{

                    if(getIntent().getExtras()!=null) {

                        msgId = getIntent().getStringExtra("uid");

                        Intent mainActivity = new Intent(getApplicationContext(),Homepage.class);
                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mainActivity);

                        Intent i = new Intent(getApplicationContext(),Activity_chat.class);
                        i.putExtra("Id",msgId);
                        startActivity(i);
                        finish();

                    }else {
                        Intent i = new Intent(SpleshScreen.this, Homepage.class);
                        i.putExtra("msgUid", msgId);
                        startActivity(i);
                        finish();
                    }

                }
            }
        },2000);




    }
}