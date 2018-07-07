package com.asuswork.jamor.facturasapp.Database.Facturas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jamor on 28/02/2018.
 */

public class FacturaBaseHelper  extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "FacturasDBase_facturas.db";

    public FacturaBaseHelper (Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + FacturaDbScheme.FacturaTable.NAME + " ( " +
                FacturaDbScheme.FacturaTable.Cols.ID + " integer primary key autoincrement, " +
                FacturaDbScheme.FacturaTable.Cols.DESIGNACAO + ", " +
                FacturaDbScheme.FacturaTable.Cols.COMENTARIO + ", " +
                FacturaDbScheme.FacturaTable.Cols.DATA + ", " +
                FacturaDbScheme.FacturaTable.Cols.USER + " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
