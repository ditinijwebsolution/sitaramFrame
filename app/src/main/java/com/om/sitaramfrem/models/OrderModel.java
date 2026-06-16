package com.om.sitaramfrem.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class OrderModel implements Parcelable {
    private String frameNo = "";
    private String frameName = "";
    private String itemId = "";
    private String itemName = "";
    private String frameMeasure = "";
    private String quantity = "";
    private String notes = "";
    private Uri imgUri = null;
    private String imgPath = "";
    private String categoryImageId = "";

    public OrderModel() {
    }

    protected OrderModel(Parcel in) {
        frameNo = in.readString();
        frameName = in.readString();
        itemId = in.readString();
        itemName = in.readString();
        frameMeasure = in.readString();
        quantity = in.readString();
        notes = in.readString();
        imgPath = in.readString();
        imgUri = in.readParcelable(Uri.class.getClassLoader());
        categoryImageId = in.readString();
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };

    public void setImgUri(Uri imgUri) {
        this.imgUri = imgUri;
    }

    public Uri getImgUri() {
        return imgUri;
    }

    public String getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(String frameNo) {
        this.frameNo = frameNo;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getFrameMeasure() {
        return frameMeasure;
    }

    public void setFrameMeasure(String frameMeasure) {
        this.frameMeasure = frameMeasure;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCategoryImageId() {
        return categoryImageId;
    }

    public void setCategoryImageId(String categoryImageId) {
        this.categoryImageId = categoryImageId;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getFrameName() {
        return frameName;
    }

    public void setFrameName(String frameName) {
        this.frameName = frameName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(frameNo);
        parcel.writeString(frameName);
        parcel.writeString(itemId);
        parcel.writeString(itemName);
        parcel.writeString(frameMeasure);
        parcel.writeString(quantity);
        parcel.writeString(notes);
        parcel.writeString(imgPath);
        parcel.writeParcelable(imgUri, i);
        parcel.writeString(categoryImageId);
    }
}
