package me.madmagic.tss;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import me.madmagic.tss.spotify.Shuffler;

import java.util.Timer;
import java.util.TimerTask;

public class ShufflerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ("stop".equals(intent.getAction())) {
            Shuffler.stopAll();
            stopForeground(STOP_FOREGROUND_REMOVE);
            stopSelf();
            return START_NOT_STICKY;
        }
        Shuffler.doShuffle(intent.getStringExtra("href"));
        Log.d("TSS", intent.getStringExtra("href"));

        Intent i = new Intent(this, getClass());
        i.setAction("stop");
        PendingIntent pi = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT|PendingIntent.FLAG_MUTABLE);

        createNotificationChannel();
        String name = getString(R.string.app_name);
        Notification notif = new NotificationCompat.Builder(this, name)
                .setSmallIcon(R.mipmap.ic_app_round)
                .setContentTitle("True shuffling is active")
                .setWhen(0)
                .addAction(R.drawable.ic_notif, "Stop shuffle", pi)
                .build();
        startForeground(1, notif);

        return START_STICKY;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        String name = getString(R.string.app_name);
        NotificationChannel c = new NotificationChannel(name, name, NotificationManager.IMPORTANCE_MIN);
        NotificationManager nMgr = getSystemService(NotificationManager.class);
        nMgr.createNotificationChannel(c);
    }
}
