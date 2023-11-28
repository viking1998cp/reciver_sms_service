package atis.vn.smsnotification;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.zoomx.zoomx.config.Config;
import com.zoomx.zoomx.config.ZoomX;

import java.util.ArrayList;

import atis.vn.smsnotification.api.response.SmsResponse;
import atis.vn.smsnotification.api.service.RetrofitClient;
import atis.vn.smsnotification.api.service.SmsBankService;
import atis.vn.smsnotification.database.SmsDatabase;
import atis.vn.smsnotification.model.SMS;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST = 1;

    private static final String TAG = "atis-dev";
    private SmsDatabase smsDatabase;
    private Button btnStartService;
    private Button btnStopService;
    private Button btnSendAll;
    private TextView txtNoMes;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView mRecyclerView;
    private CommonItemAdapter adapter;

    private SmsBankService smsBankService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ZoomX.init(new Config.Builder(this).build());
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = findViewById(R.id.rcv_common);
        txtNoMes = findViewById(R.id.txt_no_pending);
        btnSendAll = findViewById(R.id.btn_send_all);
        btnStartService = findViewById(R.id.btn_start_svc);
        btnStopService = findViewById(R.id.btn_stop_svc);
        smsDatabase = new SmsDatabase(this);
        smsBankService = RetrofitClient.getHttpClient(this);

        btnSendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendAllSMS();

            }
        });

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startForegroundSvc();
                setupService();
            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopForegroundSvc();
                setupService();
            }
        });

        initListview();
        checkPermission();
        setupService();

        checkAvailSMS();

    }

    private void initListview() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkAvailSMS();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        adapter = new CommonItemAdapter(this);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ClickAppItemListener() {
            @Override
            public void onClick(SMS sms) {
                Log.e(TAG, "setOnItemClickListener: " + sms.getCode());
                showDialogClickItemSMS(sms);
            }
        });
    }


    private void startForegroundSvc() {
        Intent intent = new Intent(this, ForegroundAppService.class);
        startService(intent);

    }

    private void stopForegroundSvc() {
        Intent intent = new Intent(this, ForegroundAppService.class);
        stopService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "size: " + grantResults.length);
    }


    private void checkPermission() {
        String[] listPermission = new String[]{android.Manifest.permission.READ_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.POST_NOTIFICATIONS,

        };
        boolean isOn = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == getPackageManager().PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == getPackageManager().PERMISSION_GRANTED) {

        } else {

            isOn = true;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == getPackageManager().PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == getPackageManager().PERMISSION_GRANTED) {

        } else {

            isOn = true;
        }

        if ((isOn)) {
            ActivityCompat.requestPermissions(this, listPermission, MY_PERMISSIONS_REQUEST);
        }
    }

    private void setupService() {
        boolean isServiceRunning = IsMyServiceRunning(ForegroundAppService.class);
        Log.d("BBB", isServiceRunning+"");
        if (isServiceRunning) {
            btnStartService.setEnabled(false);
            btnStopService.setEnabled(true);
        } else {
            btnStartService.setEnabled(true);
            btnStopService.setEnabled(false);
        }
    }

    private boolean IsMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void checkAvailSMS() {
        ArrayList<SMS> smsList = smsDatabase.findManySms("1=1");
        int numberOfSMS = smsList.size();
        Toast.makeText(this, "Số tin nhắn đang tồn đọng: " + numberOfSMS,
                Toast.LENGTH_LONG).show();
        txtNoMes.setText("Tin nhắn đang chờ (" + numberOfSMS + ")");
        if (!smsList.isEmpty()) {
            adapter.updateList(smsList);
        }
    }

    private void showDialogClickItemSMS(final SMS sms) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("From: " + sms.getSender());
        builder.setMessage("Đã thử gửi: " + sms.getAttempt() + " lần | Trạng thái: " + sms.getStatus()
                + "\nCode: " + sms.getCode() + "\nFull: " + sms.getContent());
        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                smsDatabase.deleteSMSById(sms.getId());
                checkAvailSMS();
                Toast.makeText(MainActivity.this, "Delete sms code: " + sms.getCode(), Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Gửi lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendSMS(sms);
                Toast.makeText(MainActivity.this, "Sending code: " + sms.getCode(), Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        AlertDialog diag = builder.create();
        diag.show();
    }

    private void sendSMS(final SMS sms) {
        Call<SmsResponse> call = smsBankService.sendSms(sms);
        call.enqueue(new Callback<SmsResponse>() {
            @Override
            public void onResponse(Call<SmsResponse> call, Response<SmsResponse> response) {
                SmsResponse smsResponse = response.body();
                if (response.isSuccessful()) {
                    if (smsResponse.getSuccess()) {
                        Log.e(TAG, "Send to server success");
                        smsDatabase.deleteSMSById(sms.getId());
                    } else {
                        Log.e(TAG, "Send to server Fail");
                        smsDatabase.updateSMSById(sms.getId(), 2, (sms.getAttempt() + 1));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Send code: " + sms.getCode() + " Fail", Toast.LENGTH_LONG).show();
                    Gson gson = new Gson();
                    try {
                        SmsResponse mError = gson.fromJson(response.errorBody().string(), SmsResponse.class);
                        Toast.makeText(MainActivity.this, mError.getErrorCode(), Toast.LENGTH_LONG).show();
                        // todo: check last send time
                    } catch (Exception e) {
                        Log.e(TAG, "on parse error Response: ", e);
                    }
                    smsDatabase.updateSMSById(sms.getId(), 2, (sms.getAttempt() + 1));
                }
            }

            @Override
            public void onFailure(Call<SmsResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Send code: " + sms.getCode() + " ERROR", Toast.LENGTH_LONG).show();
                smsDatabase.updateSMSById(sms.getId(), 2, (sms.getAttempt() + 1));
            }
        });
    }

    private void resendAllSMS() {
        Log.e(TAG, "run: SEN ALL");
        ArrayList<SMS> smsList = smsDatabase.findManySms("1=1");
        if (smsList.isEmpty()) return;
        for (final SMS itemSMS : smsList) {
            sendSMS(itemSMS);
        }
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Log.e(TAG, "run: RELOAD SEN ALL");
                        checkAvailSMS();
                    }
                }, smsList.size() * 300L);
    }
}
