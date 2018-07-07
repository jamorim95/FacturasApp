package com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.asuswork.jamor.facturasapp.Database.Produto.Produto;
import com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura.RelacaoFacturaProduto;
import com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura.RelacaoFacturaProdutosDbScheme;

/**
 * Created by jamor on 28/02/2018.
 */

public class RelacaoFacturaProdutoCursorWrapper extends CursorWrapper {


    public RelacaoFacturaProdutoCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public RelacaoFacturaProduto getRelacaoFacturaProduto(){
        RelacaoFacturaProduto relacao = new RelacaoFacturaProduto();

        String produto_ID = getString(getColumnIndex(RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_PRODUTO));


        Produto p = new Produto();
        p.setID(produto_ID);
        relacao.setProduto(p);

        return relacao;
    }

    public String getSingleFactura_ID(){
        String factura_ID = getString(getColumnIndex(RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_FACTURA));

        return factura_ID;
    }

}
