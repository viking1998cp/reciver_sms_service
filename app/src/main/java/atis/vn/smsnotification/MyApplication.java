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
    public static final String API_SECRET = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJmR2g3cld6eVE1VjZlaHFRbndwcFh6MzRLQU50WXB3LXhEY0FYY190a0Q4In0.eyJleHAiOjE3MzI2Nzc5MzksImlhdCI6MTcwMTE0MTkzOSwianRpIjoiOTIwNGM1OTktYjNhYS00M2M4LWI5YjQtYzBkODdmYjI1YTZhIiwiaXNzIjoiaHR0cHM6Ly9kZXYtc3NvLWFwaS5jYXJwbGEudm4vcmVhbG1zL21hc3RlciIsImF1ZCI6WyJoaW5vLXJlYWxtIiwibWFzdGVyLXJlYWxtIiwiYWNjb3VudCJdLCJzdWIiOiIwMjNlYTkxOC1kYzc3LTQyMDctYmUwYi01N2FhMTM0YjMwMTQiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJzdXBlci1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiNWMzM2U0NTAtOTQ0MC00NmRhLWIzYTUtYTlkZWNjYzA3MjZkIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJjcmVhdGUtcmVhbG0iLCJkZWZhdWx0LXJvbGVzLW1hc3RlciIsIm9mZmxpbmVfYWNjZXNzIiwiYWRtaW4iLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7Imhpbm8tcmVhbG0iOnsicm9sZXMiOlsidmlldy1yZWFsbSIsInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwibWFuYWdlLWlkZW50aXR5LXByb3ZpZGVycyIsImltcGVyc29uYXRpb24iLCJjcmVhdGUtY2xpZW50IiwibWFuYWdlLXVzZXJzIiwicXVlcnktcmVhbG1zIiwidmlldy1hdXRob3JpemF0aW9uIiwicXVlcnktY2xpZW50cyIsInF1ZXJ5LXVzZXJzIiwibWFuYWdlLWV2ZW50cyIsIm1hbmFnZS1yZWFsbSIsInZpZXctZXZlbnRzIiwidmlldy11c2VycyIsInZpZXctY2xpZW50cyIsIm1hbmFnZS1hdXRob3JpemF0aW9uIiwibWFuYWdlLWNsaWVudHMiLCJxdWVyeS1ncm91cHMiXX0sIm1hc3Rlci1yZWFsbSI6eyJyb2xlcyI6WyJ2aWV3LWlkZW50aXR5LXByb3ZpZGVycyIsInZpZXctcmVhbG0iLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsImNyZWF0ZS1jbGllbnQiLCJtYW5hZ2UtdXNlcnMiLCJxdWVyeS1yZWFsbXMiLCJ2aWV3LWF1dGhvcml6YXRpb24iLCJxdWVyeS1jbGllbnRzIiwicXVlcnktdXNlcnMiLCJtYW5hZ2UtZXZlbnRzIiwibWFuYWdlLXJlYWxtIiwidmlldy1ldmVudHMiLCJ2aWV3LXVzZXJzIiwidmlldy1jbGllbnRzIiwibWFuYWdlLWF1dGhvcml6YXRpb24iLCJtYW5hZ2UtY2xpZW50cyIsInF1ZXJ5LWdyb3VwcyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsInNpZCI6IjVjMzNlNDUwLTk0NDAtNDZkYS1iM2E1LWE5ZGVjY2MwNzI2ZCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IkRpbmggQsO5aSBUaOG7iyAiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiIwMzk2OTI2NTUzIiwiZ2l2ZW5fbmFtZSI6IkRpbmgiLCJmYW1pbHlfbmFtZSI6IkLDuWkgVGjhu4sgIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FDZzhvY0xrVVpaQy1mS2JTQzBGa25zVHFxMGU5aXNsZVlsMEV0aER4VGM3UG5ZPXM5Ni1jIiwiZW1haWwiOiJkaW5oYnRAY2FycGxhLnZuIn0.USb9EXJyXmNdY1XBXi_nLHId4N_YrBqtnnxeUWSAXBGs2KszgNb6qWQ_zSDFIp5hSlT7UrQvdEFD3kenO-GMZAFyJ0kUjFNf-QHJkbadATaNZVXUSEqpD7jEu5gH5L5XdOCFsccHhlgI2D-IeHLtz7KKco-on9ZY3KRwfiOYInjj_6tb1xpnIAzGQPm4hVmrN4f_uBTB53fw38s8XEofQjUoW61phlVXMX9e4PvTNDChL_KJlfyd61-B2JLsdtkITP7Bf2-jjVoh2zePJXWSp_LHPY1yy-9i2ySprA6L_YDoPpikg6vYHcuDY5moyf75fKAzazbLJ8Ia8KIV-DqyAg";
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
