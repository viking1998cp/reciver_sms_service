package atis.vn.smsnotification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import com.zoomx.zoomx.config.ZoomX;

public class MyApplication extends Application   implements SensorEventListener {

    public static final String API_HOST = "https://dev-api-erp-v2.carpla.vn";
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

            Log.d("carpla", "adnroid 13");
            NotificationChannel notificationChannel
                    = new NotificationChannel(CHANNEL_ID, "Dainv SMS Channel Service", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager!= null){
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

    }
    private boolean isScreenShown = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final float SHAKE_THRESHOLD = 2.5f;
    private long lastShakeTime;
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isScreenShown && event.sensor == accelerometer) {
            long currentTime = System.currentTimeMillis();

            if ((currentTime - lastShakeTime) > 1000) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

                if (acceleration > SHAKE_THRESHOLD) {
                    // Xử lý khi điện thoại được lắc và màn hình chưa được hiển thị
                    // Ví dụ: hiển thị một thông báo, thực hiện một hành động nào đó, etc.

                    // Đặt biến kiểm soát để màn hình chỉ hiển thị một lần
                    isScreenShown = true;


                    lastShakeTime = currentTime;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
