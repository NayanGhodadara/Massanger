package com.example.massanger.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.massanger.Activiry.SpleshScreen;
import com.example.massanger.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class messageingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);


        Intent i = new Intent(getApplicationContext(), SpleshScreen.class);
        i.putExtra("uid",message.getData().get("uid"));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),Integer.parseInt(message.getData().get("notificationId")),i,PendingIntent.FLAG_IMMUTABLE);


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                String  imgUrl = message.getData().get("senderImg");
                Bitmap bitmap = getBitmapfromUrl(imgUrl);
                String title = message.getData().get("uname");
                String msg  = message.getData().get("msg");


                NotificationCompat.Builder notification = new NotificationCompat.Builder(this,"My Chanel")
                        .setChannelId("My Chanel")
                        .setContentIntent(pendingIntent)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setLargeIcon(bitmap)
                        .setSmallIcon(R.drawable.chatlogo);


                notificationManager.createNotificationChannel(new NotificationChannel(
                        "My Chanel", "ChatMe", NotificationManager.IMPORTANCE_HIGH));

                notificationManager.notify(Integer.parseInt(message.getData().get("notificationId")),notification.build());

            }

        }
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {

            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);


        } catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }
}





//                RemoteViews contentView = new  RemoteViews(getPackageName(), R.layout.notification);
//                contentView.setTextViewText(R.id.title, message.getData().get("uname"));
//                contentView.setTextViewText(R.id.text, message.getData().get("msg"));
//                        .setContent(contentView)
//                        .setCustomBigContentView(contentView)
//.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))