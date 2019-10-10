package com.developer.pinedo.masterapp.FCM;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.ReceiveActivity;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.models.CardOrders;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MessagingService extends FirebaseMessagingService {
    private static final String TAG="MessagingService";
    private static final String IP = Utils.IP;
    private static final String EMDT_CHANNEL_ID="MASTER_PUSH";
    private static final String EMDT_CHANNEL_NAME="MASTER_PUSH CHANNEL";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();


        if(remoteMessage.getNotification()!=null){
            if(remoteMessage.getData().size()>0){

            }

            showNotificaction(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    Integer.parseInt(remoteMessage.getData().get("order_id")));

        }

    }



    private void showNotificaction(String title, String body, int order_id) {
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

            CardOrders chef =new CardOrders(order_id);

            Intent i= new Intent(this,ReceiveActivity.class);
            Bundle b=new Bundle();
            b.putSerializable("order",chef);
            b.putString("id",String.valueOf(order_id));

            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_ONE_SHOT);


            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = new Notification.Builder(getApplicationContext(),"channel_01")
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.m_logo)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                    .setSound(uri)
                .build();



        NotificationChannel channel = new NotificationChannel("channel_01", "test", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("channel_01");

        notificationManager.createNotificationChannel(channel);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0,notification);
    }


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {

    }
}
