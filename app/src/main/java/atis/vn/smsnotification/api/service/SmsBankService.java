package atis.vn.smsnotification.api.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import atis.vn.smsnotification.api.response.SmsResponse;
import atis.vn.smsnotification.model.SMS;

public interface SmsBankService {
    @POST("/banking/incoming-sms")
    Call<SmsResponse> sendSms(@Body SMS sms);

    @POST("/banking/incoming-sms")
    Call<SmsResponse> sendSmsBackup(@Body SMS ping);

}
