package com.om.sitaramfrem.api;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

public class APIClient {

    private static APIInterface instance;

    Context context;

    public static APIInterface getInstance() {
        if (instance == null) {
            //TODO 60 to 30 second at everywhere
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        public Request request;
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            request = original.newBuilder()
                                    //.header("Content-Type", "application/x-www-form-urlencoded")
                                    .method(original.method(), original.body())
                                    .build();
                            return chain.proceed(request);
                        }
                    })
                    .addInterceptor(new TimberLoggingInterceptor())
                    .build();


            instance = new Retrofit.Builder()
                    .baseUrl(ApiConstant.BASE_URL)
                    .client(okHttpClient)
                    //.addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(APIInterface.class);
        }
        return instance;
    }
}