package com.example.massanger.Activiry;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massanger.R;
import com.example.massanger.databinding.ActivityUserlistBinding;
import com.example.massanger.module.Data_user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class userlist extends Availability {

    FirebaseFirestore firebaseFirestore;
    ArrayList<Data_user> arrayList;
    myAdapter adapter;
    ActivityUserlistBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        arrayList = new ArrayList<>();
        adapter = new myAdapter();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot snapshot : task.getResult()) {

                        if(!snapshot.getId().equals(FirebaseAuth.getInstance().getUid())) {
                            arrayList.add(new Data_user(snapshot.get("Name").toString(),
                                    snapshot.get("img").toString(),
                                    snapshot.getId()));
                            adapter.notifyDataSetChanged();
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        LinearLayoutManager l1 = new LinearLayoutManager(getApplicationContext());
        binding.recycle2.setLayoutManager(l1);
        binding.recycle2.setAdapter(adapter);



    }

    class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder> {

        @NonNull
        @Override
        public myAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.design1, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull myAdapter.ViewHolder holder, int position) {
            holder.txtName.setText(arrayList.get(position).name);
            Picasso.with(userlist.this).load(arrayList.get(position).img).into(holder.img);
            holder.itemView.setOnClickListener(v->{
                Intent i = new Intent(getApplicationContext(),Activity_chat.class);
                i.putExtra("Name",arrayList.get(position).name);
                i.putExtra("Img",arrayList.get(position).img);
                i.putExtra("Id",arrayList.get(position).id);
                startActivity(i);
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img;
            TextView txtName;

            public ViewHolder(@NonNull View iv) {
                super(iv);

                img = iv.findViewById(R.id.img);
                txtName = iv.findViewById(R.id.txtname);

            }
        }
    }
}