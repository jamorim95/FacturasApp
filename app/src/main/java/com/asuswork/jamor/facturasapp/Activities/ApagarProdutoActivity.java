// cenas
package com.asuswork.jamor.facturasapp.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.asuswork.jamor.facturasapp.Database.Produto.Produto;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoBaseHelper;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoCursorWrapper;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoDbScheme;
import com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura.RelacaoFacturaProdutoBaseHelper;
import com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura.RelacaoFacturaProdutosDbScheme;
import com.asuswork.jamor.facturasapp.R;

import java.util.ArrayList;

public class ApagarProdutoActivity extends AppCompatActivity {

    private String mUsername;

    private SQLiteDatabase mDatabase_produto;
    private SQLiteDatabase mDatabase_relacaoFacturaProduto;

    private LinearLayout layout_btns;

    private ArrayList<CheckBox> lista_checkbox;
    private ArrayList<Produto> lista_produtos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apagar_produto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Apagar produtos", Snackbar.LENGTH_LONG)
                        .setAction("APAGAR", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean someChecked = false;
                                for(CheckBox box : lista_checkbox){
                                    if(box.isChecked()){
                                        someChecked = true;
                                        String texto_box = box.getText().toString();
                                        String produto_ID =texto_box.substring(texto_box.indexOf(" ")+1, texto_box.indexOf("]"));

                                        deleteProduto(produto_ID);
                                    }
                                }
                                if(!someChecked){
                                    Toast.makeText(getApplicationContext(), "Tem que selecionar pelo menos 1 produto para apagar !", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Produto apagado com sucesso !", Toast.LENGTH_SHORT).show();
                                    restartScreen();
                                }
                            }
                        }).show();
            }
        });


        inicializarVariaveisGlobais();
    }

    private void inicializarVariaveisGlobais(){

        mDatabase_produto = new ProdutoBaseHelper(getApplicationContext()).getWritableDatabase();
        mDatabase_relacaoFacturaProduto = new RelacaoFacturaProdutoBaseHelper(getApplicationContext()).getWritableDatabase();

        mUsername = getIntent().getStringExtra("username");

        layout_btns = (LinearLayout) findViewById(R.id.apagar_produto_lista_btns);

        getListaProdutos();
        createListaCheckbox();

    }

    private void deleteProduto(String produto_ID){
        mDatabase_produto.delete(ProdutoDbScheme.ProdutoTable.NAME, ProdutoDbScheme.ProdutoTable.Cols.ID + " = ?", new String[]{produto_ID});
        mDatabase_relacaoFacturaProduto.delete(RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.NAME, RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_PRODUTO + " = ?", new String[]{produto_ID});
    }


    private void getListaProdutos(){

        lista_produtos = new ArrayList<Produto>();

        Cursor cc = mDatabase_produto.query(
                ProdutoDbScheme.ProdutoTable.NAME,
                null,
                ProdutoDbScheme.ProdutoTable.Cols.USER + " = ?",
                new String[]{mUsername},
                null,
                null,
                null
        );

        ProdutoCursorWrapper c_produtos = new ProdutoCursorWrapper(cc);

        if(!c_produtos.moveToFirst()){
            return;
        }

        while(!c_produtos.isAfterLast()){
            Produto p = c_produtos.getProduto();

            lista_produtos.add(p);

            c_produtos.moveToNext();
        }
    }

    private void createListaCheckbox(){

        lista_checkbox = new ArrayList<CheckBox>();

        for(Produto p : lista_produtos){
            CheckBox box = new CheckBox(layout_btns.getContext());
            box.setChecked(false);
            box.setText(p.toString_name_ID_valor());
            lista_checkbox.add(box);
            layout_btns.addView(box);
        }

    }

    private void restartScreen(){
        Intent intent = new Intent(getApplicationContext(), ApagarProdutoActivity.class);
        intent.putExtra("username", mUsername);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("username", mUsername);
        startActivity(intent);
        finish();
    }
}
