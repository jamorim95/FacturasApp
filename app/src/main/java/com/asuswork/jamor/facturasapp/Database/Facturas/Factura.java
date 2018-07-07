package com.asuswork.jamor.facturasapp.Database.Facturas;

/**
 * Created by jamor on 02/05/2018.
 */

public class Factura {

    private String ID;
    private String designacao;
    private String data;
    private String comentario;
    private String user;

    public Factura(){}

    public Factura(String ID, String data, String comenario, String user, String designacao){
        this.ID=ID;
        this.data=data;
        this.comentario=comenario;
        this.user=user;
        this.designacao=designacao;
    }

    public Factura(String data, String comenario, String user, String designacao){
        this.data=data;
        this.comentario=comenario;
        this.user=user;
        this.designacao=designacao;
    }

    public String getDesignacao() {
        return designacao;
    }

    public void setDesignacao(String designacao) {
        this.designacao = designacao;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString(){
        String str = ID + " - " + designacao;
        return str;
    }
}
