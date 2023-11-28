package atis.vn.smsnotification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApplication extends Application {

    public static final String API_HOST = "https://api.odii.xyz";
    public static final String API_SECRET = "your_secret_key";
    public static final String CHANNEL_ID = "dainv_sms_channel_service";
    public static final String MATCH_TRANS_CODE_REGEX = "ATISVN\\w{5,7}";

    @Override
    public void onCreate() {
        super.onCreate();
        createChannelNotification();
    }

    private void createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(CHANNEL_ID, "Dainv SMS Channel Service", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager!= null){
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

    }
}
