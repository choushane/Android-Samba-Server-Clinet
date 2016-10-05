package com.samba.nanohttp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import com.samba.R;

import java.io.IOException;

/**
 * Created by shane on 9/30/16.
 */

public class HttpService extends Service {
        public Context context = this;
        public Handler handler = null;
        public static Runnable runnable = null;
        PowerManager powerManager;
        PowerManager.WakeLock wakeLock;
        WifiManager.WifiLock wifiLock;

        private MyHTTPD httpd;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // Start the httpd.
            try {
                httpd = new MyHTTPD();
                httpd.start();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Service failed to start.", Toast.LENGTH_LONG).show();
            }

            // Keep the CPU awake (but not the screen).
            powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Httpd");
            wakeLock.acquire();

            // Keep the WIFI turned on.
            WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "Httpd");
            wifiLock.acquire();

            // Become a foreground service:
            // http://developer.android.com/guide/components/services.html#Foreground
            // https://android.googlesource.com/platform/development/+/master/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, com.samba.Samba.class), 0);
            // Set the info for the views that show in the notification panel.
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_sync_black_24dp)  // the status icon
                    .setTicker("My service")  // the status text
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentTitle("Http")  // the label
                    .setContentText("My service")  // the contents of the entry
                    .setContentIntent(contentIntent)  // The intent to send when clicked
                    .build();
            startForeground(1, notification);

            return Service.START_STICKY;
        }

        @Override
        public void onDestroy() {
            stopForeground(true);
            wakeLock.release();
            wifiLock.release();
            httpd.stop();
        }

}
