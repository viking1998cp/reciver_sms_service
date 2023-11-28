package atis.vn.smsnotification;

import static atis.vn.smsnotification.MyApplication.CHANNEL_ID;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ForegroundAppService extends Service {

    private static final String TAG = "atis-dev";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "ForegroundAppService onCreate ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotification();

        return START_STICKY;
    }

    private void sendNotification(){


        try
        {
            Intent intent = new Intent(this, MainActivity.class);
            @SuppressLint("UnspecifiedImmutableFlag")

            int INTENT_FLAG = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

            PendingIntent pendingIntent
                    = PendingIntent.getActivities(this, 0, new Intent[]{intent}, INTENT_FLAG);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Atis sms bank service")
                    .setContentText("Atis sms bank service: Running ...")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);
        }catch (Exception e){
                Log.d("ex999", e.toString());
        }

//        Log.d()


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "ForegroundAppService onDestroy");
    }
}
