package com.example.massanger.Activiry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.massanger.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding ;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mAuth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(view->{
            if(binding.editEmail.getText().toString().isEmpty()){
                binding.editEmail.setError("Enter email");
            }
            if(binding.editPass.getText().toString().isEmpty()){
                binding.editPass.setError("Enter Password");
            }
            if(!binding.editPass.getText().toString().isEmpty() && !binding.editEmail.getText().toString().isEmpty()){

                mAuth.signInWithEmailAndPassword(binding.editEmail.getText().toString(),binding.editPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(),Homepage.class));
                            finish();
                        }else{
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        binding.create.setOnClickListener(view->{
            startActivity(new Intent(getApplicationContext(),Register.class));
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}