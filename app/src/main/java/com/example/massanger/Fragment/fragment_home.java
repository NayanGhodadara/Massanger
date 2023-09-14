package com.example.massanger.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massanger.Activiry.Activity_chat;
import com.example.massanger.R;
import com.example.massanger.databinding.FragmentHomeBinding;
import com.example.massanger.module.Data_user2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;


public class fragment_home extends Fragment {
    ArrayList<Data_user2> arrayList;
    View v;
    FragmentHomeBinding binding;
    myAdapter adapter;
    FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        v =  binding.getRoot();



        arrayList = new ArrayList<>();
        adapter = new myAdapter();


        firebaseFirestore = FirebaseFirestore.getInstance();


        firebaseFirestore.collection("Conversion").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

            if(value!=null) {

                for (DocumentChange documentChange : value.getDocumentChanges()) {


                    String SenderId = documentChange.getDocument().getString("SenderId");
                    String ReceiverId = documentChange.getDocument().getString("ReceiverId");


                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        if (SenderId.equals(FirebaseAuth.getInstance().getUid())) {

                            arrayList.add(new Data_user2(documentChange.getDocument().getString("ReceiverName")
                                    ,documentChange.getDocument().getString("ReceiverImg"),
                                    documentChange.getDocument().get("SenderId").toString(),
                                    documentChange.getDocument().getDate("Date"),
                                    documentChange.getDocument().get("LastMessage").toString(),
                                    documentChange.getDocument().get("ReceiverId").toString(),
                                    documentChange.getDocument().get("ReceiverId").toString()));

                        }
                        if (ReceiverId.equals(FirebaseAuth.getInstance().getUid())) {
                            arrayList.add(new Data_user2(documentChange.getDocument().get("SenderName").toString()
                                    , documentChange.getDocument().get("SenderImg").toString(),
                                    documentChange.getDocument().get("SenderId").toString(),
                                    documentChange.getDocument().getDate("Date"),
                                    documentChange.getDocument().get("LastMessage").toString(),
                                    documentChange.getDocument().get("ReceiverId").toString(),
                                    documentChange.getDocument().get("SenderId").toString()));
                        }

                    }  if (documentChange.getType() == DocumentChange.Type.MODIFIED) {

                        for(int i=0; i<arrayList.size(); i++){

                            if(arrayList.get(i).senderid.equals(SenderId)  &&  arrayList.get(i).receiverid.equals(ReceiverId))
                            {
                                arrayList.get(i).msg =  documentChange.getDocument().get("LastMessage").toString();
                                arrayList.get(i).date =  documentChange.getDocument().getDate("Date");

                            }
                        }

                    }

                }
                Collections.sort(arrayList,(a1, a2)-> a2.date.compareTo(a1.date));
                LinearLayoutManager l1 = new LinearLayoutManager(getActivity());
                binding.recycle.smoothScrollToPosition(0);
                adapter.notifyDataSetChanged();
                binding.recycle.setLayoutManager(l1);
                binding.recycle.setAdapter(adapter);

            }
            }
        });



        return v;
    }


    class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder> {

        @NonNull
        @Override
        public myAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.design2, parent, false);
            return new myAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull myAdapter.ViewHolder holder, int position) {

            holder.txtName.setText(arrayList.get(position).name);
            if(arrayList.get(position).msg.length()>20){
                holder.txtLastMsg.setText(arrayList.get(position).msg.substring(0,20)+"......");
            }else{
                holder.txtLastMsg.setText(arrayList.get(position).msg);
            }

            holder.txtDate.setText(new SimpleDateFormat("hh:mm a").format(arrayList.get(position).date));
            if(arrayList.get(position).img!=null) {
                Picasso.with(getActivity()).load(arrayList.get(position).img).into(holder.img);
            }else{
                holder.img.setImageResource(R.drawable.user);
            }
            holder.itemView.setOnClickListener(v->{
                Intent i = new Intent(getActivity(), Activity_chat.class);
                i.putExtra("Name",arrayList.get(position).name);
                i.putExtra("Img",arrayList.get(position).img);
                i.putExtra("Id",arrayList.get(position).id); // this is second user id not uid
                startActivity(i);
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img;
            TextView txtName,txtDate,txtLastMsg;

            public ViewHolder(@NonNull View iv) {
                super(iv);

                img = iv.findViewById(R.id.img);
                txtName = iv.findViewById(R.id.txtname);
                txtDate = iv.findViewById(R.id.txtDate);
                txtLastMsg = iv.findViewById(R.id.txtLastMsg);

            }
        }
    }
}