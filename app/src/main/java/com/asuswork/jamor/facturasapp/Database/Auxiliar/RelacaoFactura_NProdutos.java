package com.asuswork.jamor.facturasapp.Database.Auxiliar;

import com.asuswork.jamor.facturasapp.Database.Facturas.Factura;

import java.util.ArrayList;

/**
 * Created by jamor on 13/05/2018.
 */

public class RelacaoFactura_NProdutos {

    private Factura factura;
    private ArrayList<String> lista_produtos;

    public RelacaoFactura_NProdutos(){}

    public RelacaoFactura_NProdutos(Factura f, ArrayList<String> lista){
        factura=f;
        lista_produtos=lista;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public ArrayList<String> getLista_produtos() {
        return lista_produtos;
    }

    public void setLista_produtos(ArrayList<String> lista_produtos) {
        this.lista_produtos = lista_produtos;
    }
}
