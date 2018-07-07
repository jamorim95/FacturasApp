package com.asuswork.jamor.facturasapp.Activities.ListaProdutos.MasterFlowDummy;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.asuswork.jamor.facturasapp.Database.Produto.Produto;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoCursorWrapper;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoDbScheme;
import com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura.RelacaoFacturaProdutoCursorWrapper;
import com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura.RelacaoFacturaProdutosDbScheme;
import com.asuswork.jamor.facturasapp.Database.Users.UsersCursorWrapper;
import com.asuswork.jamor.facturasapp.Database.Users.UsersDbScheme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListaProdutosContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ListaProdutosItem> ITEMS = new ArrayList<ListaProdutosItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ListaProdutosItem> ITEM_MAP = new HashMap<String, ListaProdutosItem>();

    public static SQLiteDatabase mDatabase_users;
    public static SQLiteDatabase mDatabase_produtos;
    public static SQLiteDatabase mDatabase_relacaoFacturaProdutos;
    public static String username;

    private static boolean iniciado=false;


    static {

    }

    public static void iniciar(){

        if(iniciado){
            return;
        }
        iniciado=true;

        ArrayList<Produto> listaProdutos = listaProdutos();

        // Add some sample items.
        for (int i = 0; i < listaProdutos.size(); i++) {
            //addItem(createDummyItem(i));
            ListaProdutosItem item = new ListaProdutosItem(listaProdutos.get(i));


            Cursor c = mDatabase_relacaoFacturaProdutos.query(
                    RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.NAME,
                    new String[]{RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_FACTURA},
                    RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_PRODUTO + " = ?",
                    new String[]{listaProdutos.get(i).getID()},
                    null,
                    null,
                    "1 ASC"
            );

            RelacaoFacturaProdutoCursorWrapper c_relacao= new RelacaoFacturaProdutoCursorWrapper(c);

            String str = "";

            if(c_relacao.moveToFirst()){
                while(!c_relacao.isAfterLast()){

                    if(!str.isEmpty()){
                        str+=", ";
                    }
                    str += c_relacao.getSingleFactura_ID();

                    c_relacao.moveToNext();
                }
            }

            item.setFacturasID(str);

            //mDatabase_produtos
            addItem(item);
        }
    }

    private static ArrayList<Produto> listaProdutos(){
        ArrayList<Produto> lista;

        Cursor cc = mDatabase_produtos.query(
                ProdutoDbScheme.ProdutoTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        ProdutoCursorWrapper c_produtos = new ProdutoCursorWrapper(cc);

        if(!c_produtos.moveToFirst()){
            return new ArrayList<Produto>();
        }

        lista = new ArrayList<Produto>();

        while(!c_produtos.isAfterLast()){
            Produto p = c_produtos.getProduto();
            String checkUser = p.getUser();
            if(checkUser.equals(username)  ||  isUserVisible(checkUser)){
                lista.add(p);
            }
            c_produtos.moveToNext();
        }

        return lista;
    }

    private static boolean isUserVisible(String user){
        Cursor c = mDatabase_users.query(
                UsersDbScheme.UsersTable.NAME,
                new String[]{UsersDbScheme.UsersTable.Cols.IS_PUBLIC},
                UsersDbScheme.UsersTable.Cols.USERNAME + " = ?",
                new String[]{user},
                null,
                null,
                null
        );

        UsersCursorWrapper cursor= new UsersCursorWrapper(c);

        if(!cursor.moveToFirst()){
            return false;
        }

        return cursor.getString(cursor.getColumnIndex(UsersDbScheme.UsersTable.Cols.IS_PUBLIC)).equals("y");
    }

    private static void addItem(ListaProdutosItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getProdutoID(), item);
        //ITEM_MAP.put("[ID: " + item.getProdutoID() + "]", item);
    }

    /*private static ListaProdutosItem createDummyItem(int position) {
        return new ListaProdutosItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }*/

    /*private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }*/

    /**
     * A dummy item representing a piece of content.
     */
    public static class ListaProdutosItem {
        /*public final String id;
        public final String content;
        public final String details;

        public ListaProdutosItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }*/

        private final Produto produto;
        private String facturasID;


        public ListaProdutosItem(Produto produto) {
            this.produto=produto;
        }

        @Override
        public String toString() {
            return produto.toString_name_ID() +
                    ":\n\n\t> DESIGNAÇÃO: " + produto.getNome() +
                    "\n\t> CATEGORIA: " + produto.CATEGORIAS_PRODUTO[produto.getCategoria()-1] +
                    "\n\t> VALOR: " + produto.getValor() +
                    "€\n\t> FACTURAS: " + (facturasID.isEmpty() ? "-none-" : "[" + facturasID + "]") +
                    "\n\t> DATA: " + produto.getData_formatted() +
                    "\n\t> USER: " + produto.getUser() +
                    "\n\t> COMENTÁRIO: " + produto.getComentario();
        }

        public Produto getProduto(){
            return produto;
        }

        public String getProdutoID(){
            return produto.getID();
        }

        public String getFacturasID(){
            return facturasID;
        }

        public void setFacturasID(String s){
            this.facturasID=s;
        }
    }
}
