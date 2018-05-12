package ru.courierhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Ivan on 17.03.2018.
 */

public class StatsDBHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "deliveriesToStatsServer.db";
    public static final String TABLE_NAME = "deliveries";

    public static final String COLUMN_ID = "_id"; // should be auto incremented
    public static final String COLUMN_CLIENT_PHONE_NUMBER = "_clientPhoneNumber";
    public static final String COLUMN_DELIVERY_ADDRESS = "_deliveryAddress";
    public static final String COLUMN_ITEM_NAME = "_itemName";
    public static final String COLUMN_ITEM_PRICE = "_itemPrice";

    public static final String COLUMN_DELIVERY_DATE = "_deliveryDate"; // as a primary key

    private Delivery delivery;

    public StatsDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryCreateTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                // On an INSERT, if the ROWID or INTEGER PRIMARY KEY column is not explicitly given a value,
                // then it will be filled automatically with an unused integer,
                // usually one more than the largest ROWID currently in use.
                // This is true regardless of whether or not the AUTOINCREMENT keyword is used.
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_CLIENT_PHONE_NUMBER + " TEXT, " +
                COLUMN_DELIVERY_ADDRESS + " TEXT, " +
                COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_ITEM_PRICE + " REAL, " +
                COLUMN_DELIVERY_DATE + " TEXT " +
                ");";
        sqLiteDatabase.execSQL(queryCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.close();
    }

    public void addDelivery(Delivery delivery){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CLIENT_PHONE_NUMBER, delivery.getClientPhoneNumber());
        contentValues.put(COLUMN_DELIVERY_ADDRESS, delivery.getDeliveryAddress());
        contentValues.put(COLUMN_ITEM_NAME, delivery.getItemName());
        contentValues.put(COLUMN_ITEM_PRICE, delivery.getItemPrice());
        contentValues.put(COLUMN_DELIVERY_DATE, delivery.getDeliveryDate());

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
    }

    public void deleteDelivery(Delivery delivery){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " +
                COLUMN_DELIVERY_ADDRESS + " = \"" + delivery.getDeliveryAddress() + "\" AND " +
                COLUMN_CLIENT_PHONE_NUMBER + " = \"" + delivery.getClientPhoneNumber() + "\" AND " +
                COLUMN_DELIVERY_DATE + " = \"" + delivery.getDeliveryDate() + "\";");
        sqLiteDatabase.close();
    }

//    public void deleteAll(){
//        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NAME + " WHERE 1;");
//        sqLiteDatabase.close();
//    }

    public int getDeliveriesCount(){
        int result = -1;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT COUNT ( " + COLUMN_ID + " ) AS ALL_DELIVERIES_COUNT FROM " + TABLE_NAME + " ;";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        try {
            if (cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndex("ALL_DELIVERIES_COUNT"));
            }
        } finally {
            cursor.close();
        }
        sqLiteDatabase.close();
        return result;
    }

    public ArrayList<Delivery> getAllDeliveries(){

        ArrayList<Delivery> deliveries = new ArrayList<>();

        // In 99% cases there are no differences whether to call getReadable or getWritable,
        // because it returns the same (writable object) in that 99%
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + ";";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_ID)) != null) {
                    String clientPhoneNumber = cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_PHONE_NUMBER));
                    String deliveryAddress = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_ADDRESS));
                    String deliveryDate = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_DATE));
                    String itemName = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_NAME));
                    String itemPrice = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_PRICE));

                    cursor.moveToNext();
                    delivery = new Delivery(
                            clientPhoneNumber,
                            deliveryAddress,
                            deliveryDate,
                            itemName,
                            itemPrice
                    );
                    deliveries.add(delivery);
                }
            }
        } finally {
            cursor.close();
        }

        sqLiteDatabase.close();
        return deliveries;
    }
}
