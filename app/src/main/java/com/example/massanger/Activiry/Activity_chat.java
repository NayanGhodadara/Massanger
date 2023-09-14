package com.example.massanger.Activiry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.massanger.Services.ApiService;
import com.example.massanger.R;
import com.example.massanger.databinding.ActivityChatBinding;
import com.example.massanger.module.Data_ViewType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Activity_chat extends Availability {

    public FirebaseFirestore firebaseFirestore;
    public ActivityChatBinding binding;
    public List<Data_ViewType> arrayList1;
    myAdapter2 adapter;
    String ConId = null;
    String str_SenderImg, str_SenderName;
    String ReceiverToken,ReceiverName;
    Boolean BoolAvailable;
    String notificationID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getWindow().setStatusBarColor(getResources().getColor(R.color.statusBar));

        firebaseFirestore = FirebaseFirestore.getInstance();

        adapter = new myAdapter2();
        arrayList1 = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        binding.recycle.setLayoutManager(linearLayoutManager);
        binding.recycle.setAdapter(adapter);
        getSenderAndReceiverData();


        firebaseFirestore.collection("user").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                str_SenderName = snapshot.get("Name").toString();
                str_SenderImg = snapshot.get("img").toString();
                notificationID = snapshot.get("notificationId").toString();
            }
        });



        //Online offline Code........................................................

        firebaseFirestore.collection("user").document(getIntent().getStringExtra("Id")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null) {

                    ReceiverToken = value.get("FcmToken").toString();
                    ReceiverName = value.get("Name").toString();
                    binding.txtnameReceiver.setText(ReceiverName);
                    Picasso.with(getApplicationContext()).load(value.get("img").toString()).into(binding.ReceiverImg);

                    if (value.get("Availability").toString().equals("1")) {
                        binding.v1.setVisibility(View.VISIBLE);
                        binding.txtAvailability.setText("online");
                        BoolAvailable = true;
                    } else {
                        binding.v1.setVisibility(View.INVISIBLE);
                        binding.txtAvailability.setText("offline");
                        BoolAvailable = false;

                    }

                }
            }
        });


//   ---------------------------------------------Add Data in Firestore----------------------------------------------

        binding.send.setOnClickListener(v -> {

            if (!binding.editMsg.getText().toString().isEmpty()) {

                HashMap<String, Object> data = new HashMap<>();
                data.put("Message", binding.editMsg.getText().toString());
                data.put("Date", new Date());
                data.put("SenderId", FirebaseAuth.getInstance().getUid());
                data.put("ReceiverId", getIntent().getStringExtra("Id"));

                firebaseFirestore.collection("Chat")
                        .add(data);

                if (ConId != null) {

                    firebaseFirestore.collection("Conversion").document(ConId).update("LastMessage", data.get("Message").toString(), "Date", new Date());

                } else {
                    HashMap<String, Object> data2 = new HashMap<>();
                    data2.put("LastMessage", binding.editMsg.getText().toString());
                    data2.put("Date", new Date());
                    data2.put("SenderId", FirebaseAuth.getInstance().getUid());
                    data2.put("ReceiverId", getIntent().getStringExtra("Id"));
                    data2.put("SenderName", str_SenderName);
                    data2.put("ReceiverName", getIntent().getStringExtra("Name"));
                    data2.put("SenderImg", str_SenderImg);
                    data2.put("ReceiverImg", getIntent().getStringExtra("Img"));

                    firebaseFirestore.collection("Conversion").add(data2).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            ConId = documentReference.getId();
                        }
                    });

                }


                if(!BoolAvailable){

                    try {

                        //1)--------------------------------------------

                        JSONArray token = new JSONArray();
                        token.put(ReceiverToken);

                        //2)--------------------------------------------

                        JSONObject obj1 = new JSONObject();
                        obj1.put("uname",str_SenderName);
                        obj1.put("msg",binding.editMsg.getText().toString());
                        obj1.put("uid",FirebaseAuth.getInstance().getUid());
                        obj1.put("senderImg",str_SenderImg);
                        obj1.put("notificationId",notificationID);


                        //3)--------------------------------------------

                        JSONObject mainObj = new JSONObject();
                        mainObj.put("data", obj1);
                        mainObj.put("registration_ids",token);

                        //--------------------------------------------


                        sendNotification(mainObj.toString());



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                binding.editMsg.setText(null);
            }
        });

    }

    public  void sendNotification(String body){

        HashMap<String,String> data = new HashMap<>();

        data = new HashMap<>();
        data.put("Authorization","key=AAAAiyTQXg0:APA91bEjV-GmG1Wb8i8KgbI3RGU2jB--xSj_a3-SJko6RRxXPGa8Lns_dCYoveCpu7NgxryMbMlFgJsoI1oZk6KYZI5e34DnIRrM0smfCBKcJikeW9tta6nNbxW6hBRqBTaQvA3m3xi-");
        data.put("Content-Type", "application/json");

        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com/fcm/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

        retrofit.create(ApiService.class).sendMessage(data, body).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                        if(response.isSuccessful()){

                            if(response.body()!=null){
                                try {

                                    JSONObject obj = new JSONObject(response.body());
                                    JSONArray jsonArray =  obj.getJSONArray("results");

                                    if(obj.getInt("failure")==1){

                                        Toast.makeText(Activity_chat.this,
                                                ((JSONObject) jsonArray.get(0)).getString("error"), Toast.LENGTH_SHORT).show();
                                        return;

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                        }else{
                            Toast.makeText(Activity_chat.this,"Response : "+ response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                        Toast.makeText(Activity_chat.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public  void getSenderAndReceiverData() {

        firebaseFirestore.collection("Chat").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(value!=null) {

                            for (DocumentChange snapshot1 : value.getDocumentChanges()) {

                                if (snapshot1.getType() == DocumentChange.Type.ADDED) {


                                    if(snapshot1.getDocument().get("SenderId").toString().equals(FirebaseAuth.getInstance().getUid()) &&
                                            snapshot1.getDocument().get("ReceiverId").toString().equals(getIntent().getStringExtra("Id"))) {

                                        arrayList1.add(new Data_ViewType("sender", snapshot1.getDocument().get("Message").toString(),
                                                snapshot1.getDocument().getDate("Date")));

                                    }
                                    if(snapshot1.getDocument().get("SenderId").toString().equals(getIntent().getStringExtra("Id")) &&
                                            snapshot1.getDocument().get("ReceiverId").toString().equals(FirebaseAuth.getInstance().getUid())) {

                                        arrayList1.add(new Data_ViewType("receiver", snapshot1.getDocument().get("Message").toString(),getIntent().getStringExtra("Img"),
                                                snapshot1.getDocument().getDate("Date")));

                                    }

                                    Collections.sort(arrayList1,(obj1, obj2)-> obj1.dateObj.compareTo(obj2.dateObj));

                                    if (arrayList1.size() == 0) {
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        adapter.notifyItemRangeInserted(arrayList1.size(), arrayList1.size());
                                        binding.recycle.smoothScrollToPosition(arrayList1.size() - 1);

                                    }
                                }
                            }

                            if(ConId==null){

                                firebaseFirestore.collection("Conversion").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot snapshot) {

                                        for(DocumentSnapshot snapshot1 : snapshot.getDocuments()){

                                            if(snapshot1.get("SenderId").equals(FirebaseAuth.getInstance().getUid()) &&
                                                    snapshot1.get("ReceiverId").equals(getIntent().getStringExtra("Id")) ){

                                                ConId = snapshot1.getId();

                                            }
                                            if(snapshot1.get("ReceiverId").equals(FirebaseAuth.getInstance().getUid()) &&
                                                    snapshot1.get("SenderId").equals(getIntent().getStringExtra("Id")) ){

                                                ConId = snapshot1.getId();

                                            }

                                        }

                                    }
                                });

                            }


                        }

                    }
                });



    }





    public class myAdapter2 extends RecyclerView.Adapter {


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            switch (viewType) {
                case 0:
                    LayoutInflater inflater1 = LayoutInflater.from(parent.getContext());
                    View v1 = inflater1.inflate(R.layout.design_sender, parent, false);
                    return new ViewHolder1(v1);
                case 1:
                    LayoutInflater inflater2 = LayoutInflater.from(parent.getContext());
                    View v2 = inflater2.inflate(R.layout.design_reciver, parent, false);
                    return new ViewHolder2(v2);

                default:
                    return null;
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (arrayList1.get(position).viewType) {
                case "sender":
                    ((ViewHolder1) holder).txtname.setText(arrayList1.get(position).txtName);
                    ((ViewHolder1) holder).txtTime.setText("" + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(arrayList1.get(position).dateObj));
                    break;
                case "receiver":
                    ((ViewHolder2) holder).txtname.setText(arrayList1.get(position).txtName);
                    ((ViewHolder2) holder).txtTime.setText("" + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(arrayList1.get(position).dateObj));
                    Picasso.with(getApplicationContext()).load(arrayList1.get(position).img).into(((ViewHolder2) holder).img);
                    break;

            }
        }


        @Override
        public int getItemCount() {
            return arrayList1.size();
        }

        public class ViewHolder1 extends RecyclerView.ViewHolder {

            TextView txtname, txtTime;

            public ViewHolder1(@NonNull View itemView) {
                super(itemView);
                txtname = itemView.findViewById(R.id.txtSenderMsg);
                txtTime = itemView.findViewById(R.id.txtTime);
            }
        }

        public class ViewHolder2 extends RecyclerView.ViewHolder {
            TextView txtname, txtTime;
            ImageView img;

            public ViewHolder2(@NonNull View itemView) {
                super(itemView);
                txtname = itemView.findViewById(R.id.txtReceiverMsg);
                img = itemView.findViewById(R.id.img);
                txtTime = itemView.findViewById(R.id.txtTime);
            }
        }


        @Override
        public int getItemViewType(int position) {

            switch (arrayList1.get(position).viewType) {
                case "sender":
                    return 0;
                case "receiver":
                    return 1;
                default:
                    return -1;
            }

        }

    }
}