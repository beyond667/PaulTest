package com.paul.test.server.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper  extends SQLiteOpenHelper {
    private static final String DB_NAME = "persons.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "table_person";
    private static final String ID = "_id";
    private static final String NAME = "name";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + "(" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + "," + NAME
                + " CHAR(10) )";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

