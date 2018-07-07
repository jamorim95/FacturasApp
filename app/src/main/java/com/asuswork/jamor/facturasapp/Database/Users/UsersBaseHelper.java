package com.asuswork.jamor.facturasapp.Database.Users;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jamor on 28/02/2018.
 */

public class UsersBaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "FacturasDBase_users.db";

    public UsersBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + UsersDbScheme.UsersTable.NAME + "( " +
                UsersDbScheme.UsersTable.Cols.ID + " integer primary key autoincrement, " +
                UsersDbScheme.UsersTable.Cols.USERNAME + ", " +
                UsersDbScheme.UsersTable.Cols.PASSWORD + ", " +
                UsersDbScheme.UsersTable.Cols.IS_PUBLIC + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
