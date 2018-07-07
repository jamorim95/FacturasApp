package com.asuswork.jamor.facturasapp.Activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.asuswork.jamor.facturasapp.Database.Auxiliar.RelacaoFactura_NProdutos;
import com.asuswork.jamor.facturasapp.Database.Facturas.Factura;
import com.asuswork.jamor.facturasapp.Database.Facturas.FacturaBaseHelper;
import com.asuswork.jamor.facturasapp.Database.Facturas.FacturaDbScheme;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoBaseHelper;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoCursorWrapper;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoDbScheme;
import com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura.RelacaoFacturaProdutoBaseHelper;
import com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura.RelacaoFacturaProdutosDbScheme;
import com.asuswork.jamor.facturasapp.R;

import java.util.ArrayList;
import java.util.Calendar;

public class NovaFacturaActivity extends AppCompatActivity {


    private String mUsername;

    private EditText comentario_field;
    private EditText designacao_field;

    private Button produtos_dialog_btn;

    private ArrayList<CheckBox> listaProdutosCheckboxes;

    private SQLiteDatabase mDatabase_produto;
    private SQLiteDatabase mDatabase_factura;
    private SQLiteDatabase mDatabase_relacaoFacturaProduto;

    private Dialog produtosDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_factura);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Guardar produto", Snackbar.LENGTH_LONG)
                        .setAction("GUARDAR", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                RelacaoFactura_NProdutos relacao = getRelacaoNovoProduto();
                                if(relacao!=null) {

                                    saveAllInfoToDatabase(relacao);

                                    Toast.makeText(getApplicationContext(), "Nova factura criada com sucesso !", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("username", mUsername);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Alguns dos campos obrigatórios encontram-se vazios!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
            }
        });

        inicializarVariaveisGlobais();
        setListeners();

    }


    private void saveAllInfoToDatabase(RelacaoFactura_NProdutos relacao){
        ContentValues values_factura = getContentValues_factura(relacao.getFactura());
        mDatabase_factura.insert(FacturaDbScheme.FacturaTable.NAME, null, values_factura);

        if(!relacao.getLista_produtos().isEmpty()){
            for(String produto : relacao.getLista_produtos()){
                String produto_ID =produto.substring(1, produto.indexOf("]"));
                ContentValues values_relacao = getContentValues_relacao(relacao.getFactura().getID(), produto_ID);
                mDatabase_relacaoFacturaProduto.insert(RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.NAME, null, values_relacao);
            }
        }

    }

    private ContentValues getContentValues_relacao(String factura_ID, String produto_ID){
        ContentValues values = new ContentValues();
        values.put(RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_FACTURA, factura_ID);
        values.put(RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_PRODUTO, produto_ID);
        return values;
    }


    private ContentValues getContentValues_factura(Factura f){
        ContentValues values = new ContentValues();
        values.put(FacturaDbScheme.FacturaTable.Cols.ID, f.getID());
        values.put(FacturaDbScheme.FacturaTable.Cols.DESIGNACAO, f.getDesignacao());
        values.put(FacturaDbScheme.FacturaTable.Cols.COMENTARIO, f.getComentario());
        values.put(FacturaDbScheme.FacturaTable.Cols.DATA, f.getData());
        values.put(FacturaDbScheme.FacturaTable.Cols.USER, mUsername);
        return values;
    }


    private void inicializarVariaveisGlobais(){

        mDatabase_produto = new ProdutoBaseHelper(getApplicationContext()).getWritableDatabase();
        mDatabase_factura = new FacturaBaseHelper(getApplicationContext()).getWritableDatabase();
        mDatabase_relacaoFacturaProduto = new RelacaoFacturaProdutoBaseHelper(getApplicationContext()).getWritableDatabase();

        mUsername = getIntent().getStringExtra("username");

        comentario_field = (EditText) findViewById(R.id.novaFactura_comentario);
        designacao_field = (EditText) findViewById(R.id.novaFactura_designacao);


        inicializarProdutosDialog();


        listaProdutosCheckboxes = new ArrayList<CheckBox>();
        String[] items_produtos =  getProdutosStringArray();
        for(String str : items_produtos){
            CheckBox checkBox = new CheckBox(produtosDialog.getContext());
            checkBox.setChecked(false);
            checkBox.setText(str);
            ((LinearLayout)produtosDialog.findViewById(R.id.layout_facturas)).addView(checkBox);
            listaProdutosCheckboxes.add(checkBox);
        }


        produtos_dialog_btn = (Button) findViewById(R.id.novaFactura_produtos);
        produtos_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                produtosDialog.show();
                produtos_dialog_btn.setEnabled(false);
            }
        });

    }



    private void inicializarProdutosDialog(){

        produtosDialog = new Dialog(NovaFacturaActivity.this);
        produtosDialog.setContentView(R.layout.facturas_dialog);
        produtosDialog.setTitle("Escolha os produtos para associar à nova factura");

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


        Button b3 = new Button(produtosDialog.getContext());
        b3.setText("Inverter selecção");
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<listaProdutosCheckboxes.size(); i++){
                    CheckBox c = listaProdutosCheckboxes.get(i);
                    if(c.isChecked()){
                        c.setChecked(false);
                    }else{
                        c.setChecked(true);
                    }
                }
            }
        });
        ((LinearLayout)produtosDialog.findViewById(R.id.layout_facturas_save)).addView(b3);

        produtosDialog.setCanceledOnTouchOutside(true);
        produtosDialog.setCancelable(true);
        produtosDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                produtos_dialog_btn.setEnabled(true);
            }
        });
    }

    private String[] getProdutosStringArray(){
        String[] produtos_ID;

        Cursor c = mDatabase_produto.query(
                ProdutoDbScheme.ProdutoTable.NAME,
                null,
                ProdutoDbScheme.ProdutoTable.Cols.USER + " = ?",
                new String[]{mUsername},
                null,
                null,
                null
        );

        ProdutoCursorWrapper cursor= new ProdutoCursorWrapper(c);

        if(!cursor.moveToFirst()){
            return new String[]{};
        }

        produtos_ID = new String[cursor.getCount()];
        int count=0;

        while(!c.isAfterLast()){
            produtos_ID[count]=cursor.getProduto().toString_masterFlow();
            count++;
            c.moveToNext();
        }

        return produtos_ID;
    }


    private void setListeners(){
        comentario_field.addTextChangedListener(new TextWatcher() {

            private int curLength=-1;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                curLength = start+after;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable edit) {
                String s = edit.toString();
                if(curLength!=-1  &&  s!=null  &&  s.length()!=0  &&  s.charAt(curLength-1) == '\n'){

                    if(comentario_field.getLineCount()>5){
                        String newText = s.substring(0, curLength-1);
                        if(curLength<s.length()){
                            newText += s.substring(curLength, s.length());
                        }
                        comentario_field.setText(newText);
                        comentario_field.setSelection(newText.length());
                    }
                }
                curLength=-1;
            }
        });

    }

    private RelacaoFactura_NProdutos getRelacaoNovoProduto(){
        String designacao = designacao_field.getText().toString();
        String comentario = comentario_field.getText().toString();
        ArrayList<String> produtos = getProdutosArrayList();

        if(designacao.isEmpty()){
            return null;
        }

        Factura f = new Factura(getCurrentDataString(), comentario, mUsername, designacao);

        RelacaoFactura_NProdutos rel = new RelacaoFactura_NProdutos(f, produtos);

        return rel;
    }

    private ArrayList<String> getProdutosArrayList(){
        ArrayList<String> produtos = new ArrayList<String>();

        for(CheckBox c : listaProdutosCheckboxes){
            if(c.isChecked()){
                produtos.add(c.getText().toString());
            }
        }

        return produtos;
    }

    private String getCurrentDataString(){
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day_of_month = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);

        String time = Integer.toString(year) +
                (month<10 ? ("0" + Integer.toString(month)) : Integer.toString(month)) +
                (day_of_month<10 ? ("0" + Integer.toString(day_of_month)) : Integer.toString(day_of_month)) +
                (hour<10 ? ("0" + Integer.toString(hour)) : Integer.toString(hour)) +
                (minute<10 ? ("0" + Integer.toString(minute)) : Integer.toString(minute));

        return time;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", mUsername);
        startActivity(intent);
        this.finish();
    }
}
