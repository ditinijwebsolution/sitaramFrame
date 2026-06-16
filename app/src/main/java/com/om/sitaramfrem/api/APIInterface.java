package com.om.sitaramfrem.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIInterface {

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> login(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("registration")
    Call<ResponseBody> registration(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("registration_otp")
    Call<ResponseBody> registrationOtp(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("forgot_password")
    Call<ResponseBody> forgotPassword(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("verify_otp")
    Call<ResponseBody> verifyOtp(
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("reset_password")
    Call<ResponseBody> resetPassword(
            @Field("data") String data
    );

    @POST("logout")
    Call<ResponseBody> logout(
            @Header("Authorization") String Authorization
    );

    @POST("profile")
    Call<ResponseBody> profile(
            @Header("Authorization") String Authorization
    );

    @FormUrlEncoded
    @POST("profile_update")
    Call<ResponseBody> profileUpdate(
            @Header("Authorization") String Authorization,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("change_password")
    Call<ResponseBody> changePassword(
            @Header("Authorization") String Authorization,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("order_list")
    Call<ResponseBody> orderList(
            @Header("Authorization") String Authorization,
            @Field("data") String data
    );

    @POST("item_list")
    Call<ResponseBody> itemList(
            @Header("Authorization") String Authorization
    );

    @POST("frame_no_list")
    Call<ResponseBody> getFrameNoList(
            @Header("Authorization") String Authorization
    );

    @FormUrlEncoded
    @POST("add_order")
    Call<ResponseBody> addOrder(
            @Header("Authorization") String Authorization,
            @Field("data") String data
    );

    @POST("slider_list")
    Call<ResponseBody> sliderList();

    @POST("category_list")
    Call<ResponseBody> categoryList();

    @FormUrlEncoded
    @POST("get_category_image_list")
    Call<ResponseBody> getCategoryImageList(
            @Header("Authorization") String Authorization,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("add_to_cart")
    Call<ResponseBody> addToCart(
            @Header("Authorization") String Authorization,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("edit_cart")
    Call<ResponseBody> editCart(
            @Header("Authorization") String Authorization,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("cart_list")
    Call<ResponseBody> cartList(
            @Header("Authorization") String Authorization,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("delete_from_cart")
    Call<ResponseBody> deleteFromCart(
            @Header("Authorization") String Authorization,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("")
    Call<ResponseBody> clearCart(
            @Header("Authorization") String Authorization,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("measurement_list")
    Call<ResponseBody> measurementList(
            @Header("Authorization") String Authorization,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("get_inactive_category_image_list")
    Call<ResponseBody> getInActiveCategoryImageList(
            @Header("Authorization") String Authorization,
            @Field("data") String data
    );

    @FormUrlEncoded
    @POST("checkversion")
    Call<ResponseBody> checkversion(
            @Field("data") String data
    );
}