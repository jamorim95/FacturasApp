package com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jamor on 28/02/2018.
 */

public class RelacaoFacturaProdutoBaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "RelacaoFacturaProdutosDBase.db";

    public RelacaoFacturaProdutoBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.NAME + "( " +
                RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_FACTURA + ", " +
                RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_PRODUTO + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
