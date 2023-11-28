package atis.vn.smsnotification.api.service;

import static atis.vn.smsnotification.MyApplication.API_HOST;
import static atis.vn.smsnotification.MyApplication.API_SECRET;

import android.content.Context;

import com.zoomx.zoomx.networklogger.ZoomXLoggerInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static SmsBankService getHttpClient( Context context){
        return RetrofitClient.getClient(API_HOST, context).create(SmsBankService.class);
    }

    public static Retrofit getClient(String baseUrl, Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient. addInterceptor(new ZoomXLoggerInterceptor(context.getApplicationContext()));
        // add your other interceptors …
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Authorization", API_SECRET).addHeader("app_version", "v1_check_sms").build();


                return chain.proceed(request);
            }
        });

        httpClient.connectTimeout(60, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        return retrofit;
    }
}
