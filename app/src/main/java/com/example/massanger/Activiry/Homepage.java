package com.example.massanger.Activiry;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massanger.Fragment.fragment_call;
import com.example.massanger.Fragment.fragment_home;
import com.example.massanger.Fragment.fragment_status;
import com.example.massanger.R;
import com.example.massanger.databinding.ActivityHomepageBinding;
import com.example.massanger.module.Data_status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class Homepage extends Availability {

    ActivityHomepageBinding binding;

    statusAdapter adapter;
    Boolean IsAvailableStatus = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        ArrayList<Data_status> arrayList = new ArrayList<>();
        adapter = new statusAdapter(arrayList);

        replace(new fragment_home());
        binding.bottomNav.setSelectedItemId(R.id.chat);
        binding.bottomNav.setItemIconTintList(null);
        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.chat:
                        replace(new fragment_home());
                        break;
                    case R.id.status:
                        replace(new fragment_status());

                        break;
                    case R.id.call:
                        replace(new fragment_call());
                        break;

                }
                return true;
            }
        });

        binding.add.setOnClickListener(v->{
           startActivity(new Intent(getApplicationContext(), userlist.class));
        });
        binding.img.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), profile.class), ActivityOptions.makeSceneTransitionAnimation(Homepage.this).toBundle());
        });


        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                FirebaseFirestore.getInstance().collection("user").document(FirebaseAuth.getInstance().getUid())
                        .update("FcmToken",token);

            }
        });

        HashMap<String,String> hashMap = new HashMap<>();

        hashMap.put("notificationId",new Random(1000).toString());

        FirebaseFirestore.getInstance().collection("user").document(FirebaseAuth.getInstance().getUid())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {


                        if(!snapshot.getData().containsKey("notificationId")) {

                            FirebaseFirestore.getInstance().collection("user").document(FirebaseAuth.getInstance().getUid())
                                    .update("notificationId",new Random().nextInt(1000000));
                        }
                    }
                });


        //---------------------------------------------------------- Status --------------------------------------------------------------//

        binding.statusAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,100);
            }
        });

        FirebaseFirestore.getInstance().collection("status").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(!value.getDocuments().isEmpty()){


                        for(DocumentChange snapshot : value.getDocumentChanges()) {


                                if(snapshot.getType() ==  DocumentChange.Type.ADDED) {

                                    if (snapshot.getDocument().getId().equals(FirebaseAuth.getInstance().getUid())) {

                                        IsAvailableStatus = true;

                                        FirebaseFirestore.getInstance().collection("status").document(snapshot.getDocument().getId()).
                                                collection("statuses").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                                                    @Override
                                                    public void onSuccess(QuerySnapshot snapshot1) {

                                                        for (DocumentSnapshot snapshot2 : snapshot1.getDocuments()) {


                                                        }
                                                    }
                                                });
                                    }
                                    if (!snapshot.getDocument().getId().equals(FirebaseAuth.getInstance().getUid())) {
                                        FirebaseFirestore.getInstance().collection("status").document(snapshot.getDocument().getId()).
                                                collection("statuses").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot snapshot1) {

                                                        for (DocumentSnapshot snapshot2 : snapshot1.getDocuments()) {

                                                            arrayList.add(new Data_status( "1", "1",snapshot.getDocument().getId()));
                                                            adapter.notifyDataSetChanged();

                                                        }
                                                    }

                                                });

                                    }
                                    if(!IsAvailableStatus){

                                    }
                                }
                                if(snapshot.getType()==DocumentChange.Type.REMOVED){

                                    String deletedId =  snapshot.getDocument().getId();

                                    for(int i=0; i<arrayList.size(); i++){

                                        if(arrayList.get(i).Id.equals(deletedId)){
                                            arrayList.remove(i);
                                        }

                                    }

                                }

                                adapter.notifyItemRangeInserted(arrayList.size(),arrayList.size());
                                adapter.notifyDataSetChanged();


                        }
                }

            }

        });




        binding.recycleStatus.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        binding.recycleStatus.setAdapter(adapter);

    }


    public void replace(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment);
        fragmentTransaction.commit();
    }

    class  statusAdapter extends RecyclerView.Adapter<statusAdapter.viewHolder1>
    {

        ArrayList<Data_status> arrayList;
        public statusAdapter(ArrayList<Data_status> arrayListold) {
            arrayList = arrayListold;
        }

        @NonNull
        @Override
        public statusAdapter.viewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            LayoutInflater inflater2 = LayoutInflater.from(getApplicationContext());
            View v2 = inflater2.inflate(R.layout.status_view,parent,false);
            return  new viewHolder1(v2);


        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder1 holder, int position) {

        }




        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public  class viewHolder1 extends RecyclerView.ViewHolder {

            ImageView addStatus;

            public viewHolder1(@NonNull View itemView) {
                super(itemView);
                addStatus = itemView.findViewById(R.id.statusAdd);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == 100){

                FirebaseStorage.getInstance().getReference("status").child(FirebaseAuth.getInstance().getUid()).putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            FirebaseStorage.getInstance().getReference("status").child(FirebaseAuth.getInstance().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {


                                    HashMap<String,String> data1 = new HashMap<>();
                                    data1.put("Test","test");

                                    HashMap<String,String> data2 = new HashMap<>();
                                    data2.put("statusImg",uri.toString());
                                    data2.put("DateTime",new Date().toString());

                                    FirebaseFirestore.getInstance().collection("status").document(FirebaseAuth.getInstance().getUid())
                                                    .set(data1);
                                    FirebaseFirestore.getInstance().collection("status").document(FirebaseAuth.getInstance().getUid())
                                            .collection("statuses").document().set(data2);

                                }
                            });

                        }else{
                            Toast.makeText(Homepage.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }

    }
}