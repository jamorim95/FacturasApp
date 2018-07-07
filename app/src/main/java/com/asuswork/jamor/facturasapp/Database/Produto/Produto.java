package com.asuswork.jamor.facturasapp.Database.Produto;

/**
 * Created by jamor on 28/02/2018.
 */

public class Produto {

    final static public String[] CATEGORIAS_PRODUTO = {"Snacks", "Doces", "Salgados", "Bebidas (s/ álcool)", "Bebidas (c/ álcool)", "Comida refeição", "Jornal", "Raspadinha"};

    private String ID;
    private String nome;
    private String valor;
    private int categoria;
    private String data;
    private String comentario;
    private String user;


    public Produto(){}

    public Produto(String ID, String nome, String valor, int categoria, String data, String comentario, String user){

        this.ID = ID;
        this.nome = nome;
        this.valor = valor;
        this.categoria = categoria;
        this.data = data;
        this.comentario = comentario;
        this.user = user;

    }

    public Produto(String nome, String valor, int categoria, String data, String comentario, String user){
        this.nome = nome;
        this.valor = valor;
        this.categoria = categoria;
        this.data = data;
        this.comentario = comentario;
        this.user = user;

    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public String getData() {
        return data;
    }

    public String getData_formatted() {
        return data.substring(0,4) + "/" + data.substring(4,6) + "/" + data.substring(6,8) + "  " + data.substring(8,10) + ":" + data.substring(10);
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

    @Override
    public String toString(){
        return nome + "\t" + valor + "€\t" + data;
    }

    public String toString_masterFlow(){
        return nome + "  " + valor + "€";
    }

    public String toString_name_ID_valor(){
        return "[ID: " + ID + "] " + toString_masterFlow();
    }


    public String toString_name_ID(){
        return "[ID: " + ID + "] " + nome;
    }
}
