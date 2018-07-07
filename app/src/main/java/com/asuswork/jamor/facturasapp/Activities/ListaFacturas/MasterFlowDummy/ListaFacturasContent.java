package com.asuswork.jamor.facturasapp.Activities.ListaFacturas.MasterFlowDummy;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.asuswork.jamor.facturasapp.Database.Facturas.Factura;
import com.asuswork.jamor.facturasapp.Database.Facturas.FacturaCursorWrapper;
import com.asuswork.jamor.facturasapp.Database.Facturas.FacturaDbScheme;
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

public class ListaFacturasContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ListaFacturasItem> ITEMS = new ArrayList<ListaFacturasItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ListaFacturasItem> ITEM_MAP = new HashMap<String, ListaFacturasItem>();

    //private static final int COUNT = 25;

    public static SQLiteDatabase mDatabase_facturas;
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

        ArrayList<Factura> listaFacturas = listaFacturas();


        // Add some sample items.
        for (int i = 0; i < listaFacturas.size(); i++) {
            //addItem(createDummyItem(i));
            ListaFacturasItem item = new ListaFacturasItem(listaFacturas.get(i));
            String str_produtos = "";


            Cursor c = mDatabase_relacaoFacturaProdutos.query(
                    RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.NAME,
                    new String[]{RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_PRODUTO},
                    RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_FACTURA + " = ?",
                    new String[]{listaFacturas.get(i).getID()},
                    null,
                    null,
                    "1 ASC"
            );

            RelacaoFacturaProdutoCursorWrapper c_relacao= new RelacaoFacturaProdutoCursorWrapper(c);

            if(c_relacao.moveToFirst()){
                while(!c_relacao.isAfterLast()) {
                    Cursor cc = mDatabase_produtos.query(
                            ProdutoDbScheme.ProdutoTable.NAME,
                            null,
                            ProdutoDbScheme.ProdutoTable.Cols.ID + " = ?",
                            new String[]{c_relacao.getRelacaoFacturaProduto().getProduto().getID()},
                            null,
                            null,
                            null
                    );

                    ProdutoCursorWrapper c_produtos = new ProdutoCursorWrapper(cc);

                    c_produtos.moveToFirst();
                    Produto p = c_produtos.getProduto();

                    str_produtos += p.toString() + "\n";

                    c_relacao.moveToNext();
                }

                item.setProducts_details(str_produtos);
            }
            //mDatabase_produtos
            addItem(item);
        }
    }

    private static ArrayList<Factura> listaFacturas(){
        Cursor c = mDatabase_facturas.query(
                FacturaDbScheme.FacturaTable.NAME,
                null,
                null,
                null,
                null,
                null,
                FacturaDbScheme.FacturaTable.Cols.USER + " ASC"
        );


        FacturaCursorWrapper cursor= new FacturaCursorWrapper(c);

        if(!cursor.moveToFirst()){
            return new ArrayList<Factura>();
        }

        ArrayList<Factura> lista = new ArrayList<Factura>();

        while(!cursor.isAfterLast()){
            String checkUser = cursor.getString(cursor.getColumnIndex(FacturaDbScheme.FacturaTable.Cols.USER));
            if(checkUser.equals(username)  ||  isUserVisible(checkUser)){
                lista.add(cursor.getFactura());
            }
            cursor.moveToNext();
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

    private static void addItem(ListaFacturasItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.factura.getID(), item);
    }


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
    public static class ListaFacturasItem {
        /*public final String id;
        public final String content;
        public final String details;

        public ListaFacturasItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }*/

        private final Factura factura;
        private String products_details;


        public ListaFacturasItem(Factura factura) {
            this.factura=factura;
        }

        @Override
        public String toString() {
            return factura.toString();
        }

        public Factura getFactura(){
            return factura;
        }

        public String getFacturaID(){
            return factura.getID();
        }

        public String getProducts_details(){
            return "PRODUTOS DA FACTURA [" + factura.toString() + "]:\n\n" + products_details;
        }

        public void setProducts_details(String s){
            this.products_details=s;
        }

    }
}
