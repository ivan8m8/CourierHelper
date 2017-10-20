package ru.courierhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Ivan on 12.09.17.
 * https://www.youtube.com/watch?v=Jcmp09LkU-I#t=72.457875
 */

public class DBHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "allUserDeliveries.db";
    public static final String TABLE_NAME = "deliveries";

    public static final String COLUMN_ID = "_id"; // should be auto incremented
    public static final String COLUMN_CLIENT_NAME = "_clientName";
    public static final String COLUMN_CLIENT_PHONE_NUMBER = "_clientPhoneNumber";
    public static final String COLUMN_DELIVERY_ADDRESS = "_deliveryAddress";
    public static final String COLUMN_LONG_LAT = "_longLat";
    public static final String COLUMN_DELIVERY_UNDERGROUND_STATION1 = "_deliveryUndergroundStation1";
    public static final String COLUMN_DELIVERY_UNDERGROUND_STATION1_DISTANCE = "_deliveryUndergroundStation1Distance";
    public static final String COLUMN_DELIVERY_UNDERGROUND_STATION2 = "_deliveryUndergroundStation2";
    public static final String COLUMN_DELIVERY_UNDERGROUND_STATION2_DISTANCE = "_deliveryUndergroundStation2Distance";
    public static final String COLUMN_DELIVERY_UNDERGROUND_STATION1_COLOR = "_deliveryUndergroundStation1Color";
    public static final String COLUMN_DELIVERY_UNDERGROUND_STATION2_COLOR = "_deliveryUndergroundStation2Color";
    public static final String COLUMN_CLIENT_COMMENT = "_clientComment";
    public static final String COLUMN_DELIVERY_TIME_LIMIT = "_deliveryTimeLimit";
    public static final String COLUMN_ITEM_NAME = "_itemName";
    public static final String COLUMN_ITEM_PRICE = "_itemPrice";
    public static final String COLUMN_DELIVERY_DATE = "_deliveryDate";
    public static final String COLUMN_DELIVERY_STATUS = "_deliveryStatus";

    private Delivery delivery;

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryCreateTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_CLIENT_NAME + " TEXT, " +
                COLUMN_CLIENT_PHONE_NUMBER + " TEXT, " +
                COLUMN_DELIVERY_ADDRESS + " TEXT, " +
                COLUMN_LONG_LAT + " TEXT, " +
                COLUMN_DELIVERY_UNDERGROUND_STATION1 + " TEXT, " +
                COLUMN_DELIVERY_UNDERGROUND_STATION2 + " TEXT, " +
                COLUMN_DELIVERY_UNDERGROUND_STATION1_DISTANCE + " TEXT, " +
                COLUMN_DELIVERY_UNDERGROUND_STATION2_DISTANCE + " TEXT, " +
                COLUMN_DELIVERY_UNDERGROUND_STATION1_COLOR + " TEXT, " +
                COLUMN_DELIVERY_UNDERGROUND_STATION2_COLOR + " TEXT, " +
                COLUMN_CLIENT_COMMENT + " TEXT, " +
                COLUMN_DELIVERY_TIME_LIMIT + " TEXT, " +
                COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_ITEM_PRICE + " REAL, " +
                COLUMN_DELIVERY_DATE + " TEXT, " +
                COLUMN_DELIVERY_STATUS + " INTEGER " +
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
        contentValues.put(COLUMN_CLIENT_NAME, delivery.getClientName());
        contentValues.put(COLUMN_CLIENT_PHONE_NUMBER, delivery.getClientPhoneNumber());
        contentValues.put(COLUMN_DELIVERY_ADDRESS, delivery.getDeliveryAddress());
        contentValues.put(COLUMN_LONG_LAT, delivery.getLongLat());
        contentValues.put(COLUMN_DELIVERY_UNDERGROUND_STATION1, delivery.getDeliveryUndergroundStation1());
        contentValues.put(COLUMN_DELIVERY_UNDERGROUND_STATION2, delivery.getDeliveryUndergroundStation2());
        contentValues.put(COLUMN_DELIVERY_UNDERGROUND_STATION1_DISTANCE, delivery.getDeliveryUndergroundStation1Distance());
        contentValues.put(COLUMN_DELIVERY_UNDERGROUND_STATION2_DISTANCE, delivery.getDeliveryUndergroundStation2Distance());
        contentValues.put(COLUMN_DELIVERY_UNDERGROUND_STATION1_COLOR, delivery.getDeliveryUndergroundStation1Color());
        contentValues.put(COLUMN_DELIVERY_UNDERGROUND_STATION2_COLOR, delivery.getDeliveryUndergroundStation2Color());
        contentValues.put(COLUMN_CLIENT_COMMENT, delivery.getClientComment());
        contentValues.put(COLUMN_DELIVERY_TIME_LIMIT, delivery.getDeliveryTimeLimit());
        contentValues.put(COLUMN_ITEM_NAME, delivery.getItemName());
        contentValues.put(COLUMN_ITEM_PRICE, delivery.getItemPrice());
        contentValues.put(COLUMN_DELIVERY_DATE, delivery.getDeliveryDate());
        contentValues.put(COLUMN_DELIVERY_STATUS, delivery.getDeliveryStatus());

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

    public void completeDelivery(Delivery delivery){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("UPDATE " + TABLE_NAME + " SET " +
                COLUMN_DELIVERY_STATUS + " = 1 WHERE " +
                COLUMN_CLIENT_PHONE_NUMBER + " = \"" + delivery.getClientPhoneNumber() + "\" AND " +
                COLUMN_DELIVERY_ADDRESS + " = \"" + delivery.getDeliveryAddress() + "\" AND " +
                COLUMN_DELIVERY_DATE + " = \"" + delivery.getDeliveryDate() + "\";");
        sqLiteDatabase.close();
    }


    /*
     * 0 for uncompleted
     * 1 for completed
     */

    public ArrayList<Delivery> getAllDeliveries(int status){
        ArrayList<Delivery> deliveryArrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DELIVERY_STATUS + " = " + "\"" + status + "\"";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex(COLUMN_ID)) != null) {
                String clientComment = cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_COMMENT));
                String deliveryTimelimit = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_TIME_LIMIT));
                String clientName = cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME));
                String clientPhoneNumber = cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_PHONE_NUMBER));
                String deliveryAddress = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_ADDRESS));
                String longLat = cursor.getString(cursor.getColumnIndex(COLUMN_LONG_LAT));
                String deliveryDate = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_DATE));
                String deliveryUndergroundStation1 = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_UNDERGROUND_STATION1));
                String deliveryUndergroundStation1Color = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_UNDERGROUND_STATION1_COLOR));
                String deliveryUndergroundStation1Distance = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_UNDERGROUND_STATION1_DISTANCE));
                String deliveryUndergroundStation2Distance = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_UNDERGROUND_STATION2_DISTANCE));
                String deliveryUndergroundStation2 = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_UNDERGROUND_STATION2));
                String deliveryUndergroundStation2Color = cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_UNDERGROUND_STATION2_COLOR));
                String itemName = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_NAME));
                String itemPrice = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_PRICE));

                cursor.moveToNext();
                delivery = new Delivery(clientName,
                        clientPhoneNumber,
                        deliveryAddress,
                        longLat,
                        deliveryUndergroundStation1,
                        deliveryUndergroundStation1Color,
                        deliveryUndergroundStation1Distance,
                        deliveryUndergroundStation2,
                        deliveryUndergroundStation2Color,
                        deliveryUndergroundStation2Distance,
                        clientComment,
                        deliveryTimelimit,
                        itemName,
                        itemPrice,
                        deliveryDate,
                        status);
                deliveryArrayList.add(delivery);
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return  deliveryArrayList;
    }

    public double getSumTotalByDate(String date){
        double result = -1;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT SUM ( " + COLUMN_ITEM_PRICE + " ) AS SUM_TOTAL_BY_DATE FROM " + TABLE_NAME + " WHERE " +
                COLUMN_DELIVERY_STATUS + " = " + "\"1\" AND "
                + COLUMN_DELIVERY_DATE + " LIKE " + "\"" + date + "%\"";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            result = cursor.getDouble(cursor.getColumnIndex("SUM_TOTAL_BY_DATE"));
        }
        cursor.close();
        return result;
    }

    public String getLastWorkDay(){
        String result = null;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT " + COLUMN_DELIVERY_DATE + " AS LAST_WORK_DAY FROM " + TABLE_NAME +
                " ORDER BY " + COLUMN_DELIVERY_DATE + " DESC LIMIT 1;";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndex("LAST_WORK_DAY"));
        }
        if (result != null){
            result = result.split(" ")[0];
        }
        cursor.close();
        return result;
    }

    public int getNumberOfCompletedDeliveriesByDate(String date){
        int result = -1;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT COUNT (*) AS NUMBER_OF_COMPLETED_DELIVERIES_BY_DATE FROM " + TABLE_NAME + " WHERE "
                + COLUMN_DELIVERY_STATUS + " = 1 AND "
                + COLUMN_DELIVERY_DATE + " LIKE " + "\"" + date + "%\"";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex("NUMBER_OF_COMPLETED_DELIVERIES_BY_DATE"));
        }
        cursor.close();
        return result;
    }

    public int getAllCompletedDeliveriesCount(){
        int result = -1;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT COUNT ( " + COLUMN_ID + " ) AS ALL_COMPLETED_DELIVERIES_COUNT FROM " + TABLE_NAME
                + " WHERE " + COLUMN_DELIVERY_STATUS + " = " + "\"1\";";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            result = cursor.getInt(cursor.getColumnIndex("ALL_COMPLETED_DELIVERIES_COUNT"));
        }
        cursor.close();
        sqLiteDatabase.close();
        return result;
    }

    public double getAllCompletedDeliveriesSum(){
        double result = -1;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT SUM ( " + COLUMN_ITEM_PRICE + " ) AS ALL_COMPLETED_DELIVERIES_SUM FROM " + TABLE_NAME
                + " WHERE " + COLUMN_DELIVERY_STATUS + " = " + "\"1\";";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            result = cursor.getDouble(cursor.getColumnIndex("ALL_COMPLETED_DELIVERIES_SUM"));
        }
        cursor.close();
        sqLiteDatabase.close();
        return result;
    }

    public String getMostHauntedUndergroundStation(){
        String result = null;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT " + COLUMN_DELIVERY_UNDERGROUND_STATION1 + " AS MOST_HAUNTED_UNDERGROUND_STATION FROM "
                + TABLE_NAME + " WHERE " + COLUMN_DELIVERY_STATUS + " = " + "\"1\" GROUP BY ( "
                + COLUMN_DELIVERY_UNDERGROUND_STATION1 + " ) ORDER BY COUNT ("
                + COLUMN_DELIVERY_UNDERGROUND_STATION1 + " ) DESC LIMIT 1";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndex("MOST_HAUNTED_UNDERGROUND_STATION"));
        }
        cursor.close();
        sqLiteDatabase.close();
        return result;
    }

    public int getMostHauntedUndergroundStationCount(){
        int result = -1;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT COUNT ( " + COLUMN_DELIVERY_UNDERGROUND_STATION1 + " ) AS MOST_HAUNTED_UNDERGROUND_STATION_COUNT FROM "
                + TABLE_NAME + " WHERE " + COLUMN_DELIVERY_STATUS + " = " + "\"1\" GROUP BY ( "
                + COLUMN_DELIVERY_UNDERGROUND_STATION1 + " ) ORDER BY COUNT ("
                + COLUMN_DELIVERY_UNDERGROUND_STATION1 + " ) DESC LIMIT 1";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            result = cursor.getInt(cursor.getColumnIndex("MOST_HAUNTED_UNDERGROUND_STATION_COUNT"));
        }
        cursor.close();
        sqLiteDatabase.close();
        return result;
    }

    public String getMostHauntedUndergroundLine(){
        String result = null;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT " + COLUMN_DELIVERY_UNDERGROUND_STATION1_COLOR + " AS MOST_HAUNTED_UNDERGROUND_LINE FROM "
                + TABLE_NAME + " WHERE " + COLUMN_DELIVERY_STATUS + " = " + "\"1\" GROUP BY ( "
                + COLUMN_DELIVERY_UNDERGROUND_STATION1_COLOR + " ) ORDER BY COUNT ("
                + COLUMN_DELIVERY_UNDERGROUND_STATION1_COLOR + " ) DESC LIMIT 1";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndex("MOST_HAUNTED_UNDERGROUND_LINE"));
        }
        if (result != null) {
            switch (result) {
                case "#cc0000":
                    result = "Сокольническая";
                    break;
                case "#0a6f20":
                    result = "Замоскворецкая";
                    break;
                case "#003399":
                    result = "Арбатско-Покровская";
                    break;
                case "#0099cc":
                    result = "Филёвская";
                    break;
                case "#7f0000":
                    result = "Кольцевая";
                    break;
                case "#ff7f00":
                    result = "Калужско-Рижская";
                    break;
                case "#92007b":
                    result = "Таганско-Краснопресненская";
                    break;
                case "#ffdd03":
                    result = "Калининская";
                    break;
                case "#A2A5B4":
                    result = "Серпуховско-Тимирязевская";
                    break;
                case "#99cc33":
                    result = "Люблинско-Дмитровская";
                    break;
                case "#29b1a6":
                    result = "Каховская";
                    break;
                case "#b2dae7":
                    result = "Бутовская";
                    break;
                case "#F47D87":
                    result = "Московское центральное кольцо";
                    break;
                default:
                    result = "Не удалось определить";
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return result;
    }

    public int getMostHauntedUndergroundLineCount(){
        int result = -1;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT COUNT( " + COLUMN_DELIVERY_UNDERGROUND_STATION1_COLOR + " ) AS MOST_HAUNTED_UNDERGROUND_LINE_COUNT FROM "
                + TABLE_NAME + " WHERE " + COLUMN_DELIVERY_STATUS + " = " + "\"1\" GROUP BY ( "
                + COLUMN_DELIVERY_UNDERGROUND_STATION1_COLOR + " ) ORDER BY COUNT ("
                + COLUMN_DELIVERY_UNDERGROUND_STATION1_COLOR + " ) DESC LIMIT 1";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            result = cursor.getInt(cursor.getColumnIndex("MOST_HAUNTED_UNDERGROUND_LINE_COUNT"));
        }
        cursor.close();
        sqLiteDatabase.close();
        return result;
    }

    // < 99999999 should be replaced with != ""
    public double getMostExpensiveOrder(){
        double result = -1;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT " + COLUMN_ITEM_PRICE + " AS MOST_EXPENSIVE_ORDER FROM "
                + TABLE_NAME + " WHERE ( " + COLUMN_DELIVERY_STATUS + " = "
                + "\"1\" AND " + COLUMN_ITEM_PRICE + " < 99999999999999999 ) ORDER BY "
                + COLUMN_ITEM_PRICE + " DESC LIMIT 1;";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            result = cursor.getDouble(cursor.getColumnIndex("MOST_EXPENSIVE_ORDER"));
        }
        cursor.close();
        return result;
    }

    public double getCheapestOrder(){
        double result = -1;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "SELECT MIN (NULLIF ( " + COLUMN_ITEM_PRICE + " ,0)) AS CHEAPEST_ORDER FROM " + TABLE_NAME
                + " WHERE " + COLUMN_DELIVERY_STATUS + " = " + "\"1\" LIMIT 1;";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()){
            result = cursor.getDouble(cursor.getColumnIndex("CHEAPEST_ORDER"));
        }
        cursor.close();
        return result;
    }

    public void editDelivery(
            String clientName,
            String clientPhoneNumber,
            String deliveryAddress,
            String longLat,
            String deliveryUndergroundStation1,
            String deliveryUndergroundStation1Color,
            String deliveryUndergroundStation1Distance,
            String deliveryUndergroundStation2,
            String deliveryUndergroundStation2Color,
            String deliveryUndergroundStation2Distance,
            String clientComment,
            String deliveryTimeLimit,
            String itemName,
            String itemPrice,
            String deliveryDate
    ){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "UPDATE "
                + TABLE_NAME
                + " SET "
                + COLUMN_CLIENT_NAME + " = \"" + clientName + "\" , "
                + COLUMN_CLIENT_PHONE_NUMBER + " = \"" + clientPhoneNumber + "\" , "
                + COLUMN_DELIVERY_ADDRESS + " = \"" + deliveryAddress + "\" , "
                + COLUMN_LONG_LAT + " = \"" + longLat + "\" , "
                + COLUMN_DELIVERY_UNDERGROUND_STATION1 + " = \"" + deliveryUndergroundStation1 + "\" , "
                + COLUMN_DELIVERY_UNDERGROUND_STATION1_COLOR + " = \"" + deliveryUndergroundStation1Color + "\" , "
                + COLUMN_DELIVERY_UNDERGROUND_STATION1_DISTANCE + " = \"" + deliveryUndergroundStation1Distance + "\" , "
                + COLUMN_DELIVERY_UNDERGROUND_STATION2 + " = \"" + deliveryUndergroundStation2 + "\" , "
                + COLUMN_DELIVERY_UNDERGROUND_STATION2_COLOR + " = \"" + deliveryUndergroundStation2Color + "\" , "
                + COLUMN_DELIVERY_UNDERGROUND_STATION2_DISTANCE + " = \"" + deliveryUndergroundStation2Distance + "\" , "
                + COLUMN_CLIENT_COMMENT + " = \"" + clientComment + "\" , "
                + COLUMN_DELIVERY_TIME_LIMIT + " = \"" + deliveryTimeLimit + "\" , "
                + COLUMN_ITEM_NAME + " = \"" + itemName + "\" , "
                + COLUMN_ITEM_PRICE + " = \"" + itemPrice + "\""
                + " WHERE "
                + COLUMN_DELIVERY_DATE + " = \"" + deliveryDate + "\" ;";
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public void editDelivery(
            String clientName,
            String clientPhoneNumber,
            String clientComment,
            String deliveryTimeLimit,
            String itemName,
            String itemPrice,
            String deliveryDate
    ){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = "UPDATE "
                + TABLE_NAME
                + " SET "
                + COLUMN_CLIENT_NAME + " = \"" + clientName + "\" , "
                + COLUMN_CLIENT_PHONE_NUMBER + " = \"" + clientPhoneNumber + "\" , "
                + COLUMN_CLIENT_COMMENT + " = \"" + clientComment + "\" , "
                + COLUMN_DELIVERY_TIME_LIMIT + " = \"" + deliveryTimeLimit + "\" , "
                + COLUMN_ITEM_NAME + " = \"" + itemName + "\" , "
                + COLUMN_ITEM_PRICE + " = \"" + itemPrice + "\""
                + " WHERE "
                + COLUMN_DELIVERY_DATE + " = \"" + deliveryDate + "\" ;";
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

}
