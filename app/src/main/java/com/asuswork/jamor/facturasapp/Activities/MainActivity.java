package com.asuswork.jamor.facturasapp.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.asuswork.jamor.facturasapp.Activities.ListaFacturas.FacturaElementListActivity;
import com.asuswork.jamor.facturasapp.Activities.ListaProdutos.ProdutoElementListActivity;
import com.asuswork.jamor.facturasapp.Database.Produto.Produto;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoBaseHelper;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoCursorWrapper;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoDbScheme;
import com.asuswork.jamor.facturasapp.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button novoElementoBtn;
    private Button apagarElementoBtn;
    private Button editarElementoBtn;

    private Button novaFacturaBtn;
    private Button apagarFacturaBtn;
    private Button editarFacturaBtn;

    private Button listarElementosBtn;
    private Button listarFacturasBtn;

    private FloatingActionButton userSettingsBtn;

    public static SQLiteDatabase mDatabase_produtos;

    private Dialog produtosDialog;


    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarGlobalVars();

        inicializarBtnsOnClickListeners();


    }

    private void inicializarGlobalVars(){

        username = getIntent().getStringExtra("username");

        novoElementoBtn = (Button) findViewById(R.id.novoElementoBtn);
        apagarElementoBtn = (Button) findViewById(R.id.apagarElementoBtn);
        editarElementoBtn = (Button) findViewById(R.id.editarElementoBtn);
        listarElementosBtn = (Button) findViewById(R.id.listarElementosBtn);
        listarFacturasBtn = (Button) findViewById(R.id.listarFacturasBtn);
        userSettingsBtn = (FloatingActionButton) findViewById(R.id.user_settings_btn);
        novaFacturaBtn = (Button) findViewById(R.id.novaFacturaBtn);
        apagarFacturaBtn = (Button) findViewById(R.id.apagarFacturaBtn);
        editarFacturaBtn= (Button) findViewById(R.id.editarFacturaBtn);

        mDatabase_produtos = new ProdutoBaseHelper(getApplicationContext()).getReadableDatabase();
    }


    private void inicializarBtnsOnClickListeners(){
        novoElementoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NovoProdutoActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });


        apagarElementoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ApagarProdutoActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });



        editarElementoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent intent = new Intent(getApplicationContext(), EditarProdutoActivity.class);
                intent.putExtra("username", username);


                produtosDialog = new Dialog(MainActivity.this);
                produtosDialog.setContentView(R.layout.facturas_dialog);
                produtosDialog.setTitle("Escolha o produto a editar");

                ((LinearLayout)produtosDialog.findViewById(R.id.layout_facturas_save)).setOrientation(LinearLayout.HORIZONTAL);
                Button b = new Button(produtosDialog.getContext());
                b.setText("Voltar");
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        produtosDialog.dismiss();
                    }
                });
                ((LinearLayout)produtosDialog.findViewById(R.id.layout_facturas_save)).addView(b);


                produtosDialog.setCanceledOnTouchOutside(true);
                produtosDialog.setCancelable(true);

                for(final Produto p : listaProdutos()){
                    Button btn = new Button(produtosDialog.getContext());
                    btn.setText(p.toString_masterFlow());
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            produtosDialog.dismiss();
                            intent.putExtra("producto_ID", p.getID());
                            startActivity(intent);
                            finish();
                        }
                    });
                    ((LinearLayout)produtosDialog.findViewById(R.id.layout_facturas)).addView(btn);
                }

                produtosDialog.show();

            }
        });



        novaFacturaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NovaFacturaActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });


        apagarFacturaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: criar ecrã de "Apagar Factura" e chamá-lo aqui
                //TODO: se apagar uma factura, perguntar se quer apagar os produtos associados
                //TODO: fazer esta pergunta com um dialog com checkboxes à frente de cada produto associado à factura  (VER BLUETOOTH DA APP DOS FILMES)
            }
        });



        editarFacturaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: criar ecrã de "Editar Factura" e chamá-lo aqui
                //TODO: para escolher a factura a editar, mostrar um dialog com botões com todas as facturas deste utilizador
                //TODO: botão para associar vários produtos a cada factura
                //TODO: fazer esta associação, mostrar um dialog com checkboxes à frente de cada produto deste utilizador  (VER BLUETOOTH DA APP DOS FILMES)
            }
        });



        listarElementosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProdutoElementListActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });


        listarFacturasBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FacturaElementListActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();

            }
        });



        userSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Configurar utilizador", Snackbar.LENGTH_LONG)
                        .setAction("OPÇÕES", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getApplicationContext(), UserSettingsActivity.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                                finish();
                            }
                        }).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("from_main_screen", true);
        startActivity(intent);
        this.finish();
    }

    private ArrayList<Produto> listaProdutos(){
        ArrayList<Produto> lista;

        Cursor cc = mDatabase_produtos.query(
                ProdutoDbScheme.ProdutoTable.NAME,
                null,
                ProdutoDbScheme.ProdutoTable.Cols.USER + " = ?",
                new String[]{username},
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
            lista.add(c_produtos.getProduto());
            c_produtos.moveToNext();
        }

        return lista;
    }
}
