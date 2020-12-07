package com.phoenix.music_application.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionServices extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("Tracks_Tracks")
        .putExtra("actionname", intent.getAction()));
    }
}
