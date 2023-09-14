package com.example.massanger.Activiry;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.massanger.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    com.example.massanger.databinding.ActivityRegisterBinding binding;
    FirebaseFirestore firebaseFirestore;

    Uri imguri;
    String str_img="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseFirestore = FirebaseFirestore.getInstance();


        binding.img.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "s"), 100);
        });




            binding.btnSignup.setOnClickListener(view -> {

                if (binding.editName.getText().toString().isEmpty()) {
                    binding.editName.setError("Enter Name");
                }
                if (binding.editEmail.getText().toString().isEmpty()) {
                    binding.editEmail.setError("Enter Email");
                }
                if (binding.editPass.getText().toString().isEmpty()) {
                    binding.editPass.setError("Enter Password");
                }
                if (binding.editConPass.getText().toString().isEmpty()) {
                    binding.editConPass.setError("Enter password");
                }
                if (!binding.editPass.getText().toString().equals(binding.editConPass.getText().toString())) {
                    binding.editConPass.setError("Password not match");
                }
                if (!binding.editPass.getText().toString().isEmpty()
                        && !binding.editConPass.getText().toString().isEmpty()
                        && !binding.editEmail.getText().toString().isEmpty()
                        && !binding.editName.getText().toString().isEmpty()
                        && binding.editPass.getText().toString().equals(binding.editConPass.getText().toString())) {


                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("Name", binding.editName.getText().toString());
                    hashMap.put("Email", binding.editEmail.getText().toString());
                    hashMap.put("Password", binding.editPass.getText().toString());
                    hashMap.put("img",str_img);


                    if (imguri != null && !binding.editPass.getText().toString().isEmpty() &&
                    !binding.editEmail.getText().toString().isEmpty() &&
                    !binding.editName.getText().toString().isEmpty() && !binding.editConPass.getText().toString().isEmpty()) {





                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.editEmail.getText().toString(),binding.editPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    FirebaseStorage.getInstance().getReference("user").child(FirebaseAuth.getInstance().getUid())
                                            .putFile(imguri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                    if (task.isSuccessful()) {



                                                        FirebaseStorage.getInstance().getReference("user").child(FirebaseAuth.getInstance().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {


                                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                                hashMap.put("Name", binding.editName.getText().toString());
                                                                hashMap.put("Email", binding.editEmail.getText().toString());
                                                                hashMap.put("Password", binding.editPass.getText().toString());
                                                                hashMap.put("img", uri.toString());


                                                                String id = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();


                                                                //-----------------------------------------Add Record ---------------------------------------------------



                                                                firebaseFirestore.collection("user").document(id).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if (task.isSuccessful()) {
                                                                            startActivity(new Intent(getApplicationContext(), Login.class));
                                                                            finish();
                                                                        } else {
                                                                            msg(task.getException().getMessage());
                                                                        }

                                                                    }
                                                                });

                                                            }

                                                        });
                                                    } else {
                                                        msg(task.getException().getMessage());
                                                    }
                                                }
                                });

                    }else{
                        Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }




                }
            });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {

                if (data != null) {
                    imguri = data.getData();
                    str_img = imguri.toString();
                    binding.img.setImageURI(imguri);
                }
            }
        }
    }
    public void msg(String msg1){
        Toast.makeText(this, msg1, Toast.LENGTH_SHORT).show();
    }
}
