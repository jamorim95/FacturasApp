package com.asuswork.jamor.facturasapp.Database.Produto;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by jamor on 28/02/2018.
 */

public class ProdutoCursorWrapper extends CursorWrapper {


    public ProdutoCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Produto getProduto(){
        Produto f = new Produto();

        String ID = getString(getColumnIndex(ProdutoDbScheme.ProdutoTable.Cols.ID));
        String nome = getString(getColumnIndex(ProdutoDbScheme.ProdutoTable.Cols.NOME));
        String valor = getString(getColumnIndex(ProdutoDbScheme.ProdutoTable.Cols.VALOR));
        int categoria = getInt(getColumnIndex(ProdutoDbScheme.ProdutoTable.Cols.CATEGORIA));
        String data = getString(getColumnIndex(ProdutoDbScheme.ProdutoTable.Cols.DATA));
        String comentario = getString(getColumnIndex(ProdutoDbScheme.ProdutoTable.Cols.COMENTARIO));
        String user = getString(getColumnIndex(ProdutoDbScheme.ProdutoTable.Cols.USER));

        f.setID(ID);
        f.setNome(nome);
        f.setValor(valor);
        f.setCategoria(categoria);
        f.setData(data);
        f.setComentario(comentario);
        f.setUser(user);

        return f;
    }

    public String getProdutoID(){
        String ID = getString(getColumnIndex(ProdutoDbScheme.ProdutoTable.Cols.ID));
        return ID;
    }
}
