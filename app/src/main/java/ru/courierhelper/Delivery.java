package ru.courierhelper;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Ivan on 12.09.17.
 */

public class Delivery implements Parcelable {

    private String clientName;
    private String clientPhoneNumber;
    private String deliveryAddress;
    private String longLat;
    private String deliveryUndergroundStation1;
    private String deliveryUndergroundStation1Color;
    private String deliveryUndergroundStation1Distance;
    private String deliveryUndergroundStation2;
    private String deliveryUndergroundStation2Color;
    private String deliveryUndergroundStation2Distance;
    private String clientComment;
    private String deliveryTimeLimit;
    private String itemName;
    private String itemPrice; // Cannot be Double due to Parcelable
    private String deliveryDate;
    private int deliveryStatus;


    /**
     * This constructor is for a just added delivery
     * Here the current date is obtained
     */

    public Delivery(String clientName,
                    String clientPhoneNumber,
                    String deliveryAddress,
                    String clientComment,
                    String deliveryTimeLimit,
                    String itemName,
                    String itemPrice) {
        this.clientName = clientName;
        this.clientPhoneNumber = clientPhoneNumber;
        this.deliveryAddress = deliveryAddress.replace(", Россия", "");
        this.clientComment = clientComment;
        this.deliveryTimeLimit = deliveryTimeLimit;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss"); // set the locale
        this.deliveryDate = simpleDateFormat.format(calendar.getTime());
        this.deliveryStatus = 0;
    }

    public Delivery(String clientName, String clientPhoneNumber, String deliveryAddress, String longLat, String deliveryUndergroundStation1, String deliveryUndergroundStation1Color, String deliveryUndergroundStation1Distance, String deliveryUndergroundStation2, String deliveryUndergroundStation2Color, String deliveryUndergroundStation2Distance, String clientComment, String deliveryTimeLimit, String itemName, String itemPrice, String deliveryDate, int deliveryStatus) {
        this.clientName = clientName;
        this.clientPhoneNumber = clientPhoneNumber;
        this.deliveryAddress = deliveryAddress;
        this.longLat = longLat;
        this.deliveryUndergroundStation1 = deliveryUndergroundStation1;
        this.deliveryUndergroundStation1Color = deliveryUndergroundStation1Color;
        this.deliveryUndergroundStation1Distance = deliveryUndergroundStation1Distance;
        this.deliveryUndergroundStation2 = deliveryUndergroundStation2;
        this.deliveryUndergroundStation2Color = deliveryUndergroundStation2Color;
        this.deliveryUndergroundStation2Distance = deliveryUndergroundStation2Distance;
        this.clientComment = clientComment;
        this.deliveryTimeLimit = deliveryTimeLimit;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.deliveryDate = deliveryDate;
        this.deliveryStatus = deliveryStatus;
    }

    protected Delivery(Parcel in) {
        clientName = in.readString();
        clientPhoneNumber = in.readString();
        deliveryAddress = in.readString();
        longLat = in.readString();
        deliveryUndergroundStation1 = in.readString();
        deliveryUndergroundStation1Color = in.readString();
        deliveryUndergroundStation1Distance = in.readString();
        deliveryUndergroundStation2 = in.readString();
        deliveryUndergroundStation2Color = in.readString();
        deliveryUndergroundStation2Distance = in.readString();
        clientComment = in.readString();
        deliveryTimeLimit = in.readString();
        itemName = in.readString();
        itemPrice = in.readString();
        deliveryDate = in.readString();
        deliveryStatus = in.readInt();
    }

    public static final Creator<Delivery> CREATOR = new Creator<Delivery>() {
        @Override
        public Delivery createFromParcel(Parcel in) {
            return new Delivery(in);
        }

        @Override
        public Delivery[] newArray(int size) {
            return new Delivery[size];
        }
    };

    public String getLongLat() {
        return longLat;
    }

    public void setLongLat(String longLat) {
        this.longLat = longLat;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryUndergroundStation1() {
        return deliveryUndergroundStation1;
    }

    public void setDeliveryUndergroundStation1(String deliveryUndergroundStation1) {
        this.deliveryUndergroundStation1 = deliveryUndergroundStation1;
    }

    public String getDeliveryUndergroundStation1Color() {
        return deliveryUndergroundStation1Color;
    }

    public void setDeliveryUndergroundStation1Color(String deliveryUndergroundStation1Color) {
        this.deliveryUndergroundStation1Color = deliveryUndergroundStation1Color;
    }

    public String getDeliveryUndergroundStation2() {
        return deliveryUndergroundStation2;
    }

    public void setDeliveryUndergroundStation2(String deliveryUndergroundStation2) {
        this.deliveryUndergroundStation2 = deliveryUndergroundStation2;
    }

    public String getDeliveryUndergroundStation2Color() {
        return deliveryUndergroundStation2Color;
    }

    public void setDeliveryUndergroundStation2Color(String deliveryUndergroundStation2Color) {
        this.deliveryUndergroundStation2Color = deliveryUndergroundStation2Color;
    }

    public String getClientComment() {
        return clientComment;
    }

    public void setClientComment(String clientComment) {
        this.clientComment = clientComment;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public int getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(int deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getDeliveryUndergroundStation1Distance() {
        return deliveryUndergroundStation1Distance;
    }

    public void setDeliveryUndergroundStation1Distance(String deliveryUndergroundStation1Distance) {
        this.deliveryUndergroundStation1Distance = deliveryUndergroundStation1Distance;
    }

    public String getDeliveryUndergroundStation2Distance() {
        return deliveryUndergroundStation2Distance;
    }

    public void setDeliveryUndergroundStation2Distance(String deliveryUndergroundStation2Distance) {
        this.deliveryUndergroundStation2Distance = deliveryUndergroundStation2Distance;
    }

    public String getDeliveryTimeLimit() {
        return deliveryTimeLimit;
    }

    public void setDeliveryTimeLimit(String deliveryTimeLimit) {
        this.deliveryTimeLimit = deliveryTimeLimit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(clientName);
        parcel.writeString(clientPhoneNumber);
        parcel.writeString(deliveryAddress);
        parcel.writeString(longLat);
        parcel.writeString(deliveryUndergroundStation1);
        parcel.writeString(deliveryUndergroundStation1Color);
        parcel.writeString(deliveryUndergroundStation1Distance);
        parcel.writeString(deliveryUndergroundStation2);
        parcel.writeString(deliveryUndergroundStation2Color);
        parcel.writeString(deliveryUndergroundStation2Distance);
        parcel.writeString(clientComment);
        parcel.writeString(deliveryTimeLimit);
        parcel.writeString(itemName);
        parcel.writeString(itemPrice);
        parcel.writeString(deliveryDate);
        parcel.writeInt(deliveryStatus);
    }

}
