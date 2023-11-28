package atis.vn.smsnotification;

import static atis.vn.smsnotification.MyApplication.MATCH_TRANS_CODE_REGEX;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import atis.vn.smsnotification.api.response.SmsResponse;
import atis.vn.smsnotification.api.service.RetrofitClient;
import atis.vn.smsnotification.api.service.SmsBankService;
import atis.vn.smsnotification.database.SmsDatabase;
import atis.vn.smsnotification.model.SMS;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmsReceiver extends BroadcastReceiver {
    private SmsBankService smsBankService;
    private static final String TAG = "atis-dev";
    private SmsDatabase smsDatabase;

    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public void onReceive(Context context, Intent intent) {
        smsDatabase = new SmsDatabase(context);
        smsBankService = RetrofitClient.getHttpClient();
        if (!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
            return;


        Bundle bundle = intent.getExtras();
        Object[] data = (Object[]) bundle.get("pdus");

        if (data != null) {
            String content = "";
            for (int i = 0; i < data.length; ++i) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) data[i]);
                content += sms.getMessageBody();
            }

            Log.e(TAG, "content: " + content);
            String checkResult = checkMatchBankSMS(content);

            if (checkResult.equals("")) {
                Log.e(TAG, content + " :Không phải SMS bank transfer");
                return;
            }

            SmsMessage currenSMS = SmsMessage.createFromPdu((byte[]) data[0]);

            String sender = currenSMS.getOriginatingAddress();
            long time = currenSMS.getTimestampMillis();
            String received_time = convertLongToISOStringTime(time);
            Date now = new Date();
            String send_time = convertLongToISOStringTime(now);

            SMS sms = new SMS();
            sms.setType("SMS");
            sms.setContent(content);
            sms.setSender(sender);
            sms.setCode(checkResult);
            sms.setReceivedTime(received_time);
            sms.setSendTime(send_time);
            sms.setStatus(1);
            sms.setAttempt(1);

            //add sms in sqlite
            boolean addResult = smsDatabase.addSms(sms);
            if (addResult) {
                ArrayList<SMS> smsList = smsDatabase.findManySms("attempt < 5");
                if (smsList.isEmpty()) return;
                for (final SMS itemSMS : smsList) {
                    sendSMS(itemSMS);
                }
            }
        }

    }

    private String convertLongToISOStringTime(Object time) {
        return timeFormat.format(time);
    }

    private void sendSMS(final SMS sms) {
        Call<SmsResponse> call = smsBankService.sendSms(sms);
        call.enqueue(new Callback<SmsResponse>() {
            @Override
            public void onResponse(Call<SmsResponse> call, Response<SmsResponse> response) {
                SmsResponse smsResponse = response.body();
                if (response.isSuccessful()) {

                    if (smsResponse.getSuccess()) {
                        Log.e(TAG, "Tin nhan da duoc luu");
                        smsDatabase.deleteSMSById(sms.getId());
//                        smsDatabase.deleteSMSById(smsId);
                    } else {
                        Log.e(TAG, "Tin nhan chua duoc luu");
                        smsDatabase.updateSMSById(sms.getId(), 2, (sms.getAttempt() + 1));
                    }
                } else {
                    Gson gson = new Gson();
                    try {
                        SmsResponse mError = gson.fromJson(response.errorBody().string(), SmsResponse.class);
                        Log.e(TAG, "DEO LUU CODE = " + mError.getErrorCode());
                        // todo: check last send time
                        smsDatabase.updateSMSById(sms.getId(), 2, (sms.getAttempt() + 1));
//                        if(!mError.getSuccess()){
//                            listener.onSuccess();
//                        }
                    } catch (Exception e) {
                        // handle failure to read error
                        Log.e(TAG, "on parse error Response: ", e);
                        smsDatabase.updateSMSById(sms.getId(), 2, (sms.getAttempt() + 1));
                    }
                }
            }

            @Override
            public void onFailure(Call<SmsResponse> call, Throwable t) {
                // remote addr down
                Log.e(TAG, "Gap loi: " + t);
                smsDatabase.updateSMSById(sms.getId(), 2, (sms.getAttempt() + 1));
            }
        });
    }

    private String checkMatchBankSMS(String content) {
        Pattern pattern = Pattern.compile(MyApplication.MATCH_TRANS_CODE_REGEX);
        Matcher matcher = pattern.matcher(content.toUpperCase());
        String result = "";
        while (matcher.find()) {
            Log.e(TAG,"Found the text " + matcher.group() + " starting at index " +
                    matcher.start() + " and ending at index " + matcher.end());
            result = matcher.group().trim();
        }
        return result;
    }

}
