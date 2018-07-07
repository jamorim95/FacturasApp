package com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura;

import com.asuswork.jamor.facturasapp.Database.Facturas.Factura;
import com.asuswork.jamor.facturasapp.Database.Produto.Produto;

import java.util.ArrayList;

/**
 * Created by jamor on 02/05/2018.
 */

public class RelacaoFacturaProduto {

    private Produto produto;
    private ArrayList<String> facturas;

    public RelacaoFacturaProduto(){}

    public RelacaoFacturaProduto(Produto produto, ArrayList<String> facturas){
        this.produto=produto;
        this.facturas=facturas;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public ArrayList<String> getListaFacturas() {
        return facturas;
    }

    public void setFactura(ArrayList<String> facturas) {
        this.facturas = facturas;
    }
}
