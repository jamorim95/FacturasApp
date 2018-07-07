package com.asuswork.jamor.facturasapp.Database.Produto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jamor on 28/02/2018.
 */

public class ProdutoBaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "FacturasDBase_produtos.db";

    public ProdutoBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ProdutoDbScheme.ProdutoTable.NAME + "( " +
                ProdutoDbScheme.ProdutoTable.Cols.ID + " integer primary key autoincrement, " +
                ProdutoDbScheme.ProdutoTable.Cols.NOME + ", " +
                ProdutoDbScheme.ProdutoTable.Cols.VALOR + ", " +
                ProdutoDbScheme.ProdutoTable.Cols.CATEGORIA + ", " +
                ProdutoDbScheme.ProdutoTable.Cols.DATA + ", " +
                ProdutoDbScheme.ProdutoTable.Cols.COMENTARIO + ", " +
                ProdutoDbScheme.ProdutoTable.Cols.USER + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
