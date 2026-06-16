package com.om.sitaramfrem.api;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class TimberLoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
       /* String requestLog = String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers());*/


        String requestLog ="";
        //YLog.d(String.format("Sending request %s on %s%n%s",
        //        request.url(), chain.connection(), request.headers()));
        if(request.method().compareToIgnoreCase("post")==0){
           // requestLog ="\n"+requestLog+"\n"+bodyToString(request);

            requestLog = String.format("%s?%s%n%s",
                    request.url(),bodyToString(request), request.headers());

        }
        Log.i("LoginAPI", "request\n" + requestLog);

        Response response = chain.proceed(request);

        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            Log.e("LoginAPI", "Login API Raw Response: empty body, code=" + response.code());
            return response;
        }

        String responseBodyString = responseBody.string();
        Log.e("LoginAPI", "Login API Raw Response: " + responseBodyString);

        Response newResponse = response.newBuilder()
                .body(ResponseBody.create(responseBody.contentType(), responseBodyString.getBytes()))
                .build();

        long t2 = System.nanoTime();
        String responseLog = String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers());

      //  String bodyString = response.body().string();

     //  Log.d("TAG","response"+"\n"+responseLog+"\n"+bodyString);

        return newResponse;
    }

    private static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            assert copy.body() != null;
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
