package com.asuswork.jamor.facturasapp.Activities;

import android.app.Dialog;
import android.content.ContentValues;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.asuswork.jamor.facturasapp.Database.Facturas.FacturaBaseHelper;
import com.asuswork.jamor.facturasapp.Database.Facturas.FacturaCursorWrapper;
import com.asuswork.jamor.facturasapp.Database.Facturas.FacturaDbScheme;
import com.asuswork.jamor.facturasapp.Database.Produto.Produto;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoBaseHelper;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoCursorWrapper;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoDbScheme;
import com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura.RelacaoFacturaProduto;
import com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura.RelacaoFacturaProdutoBaseHelper;
import com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura.RelacaoFacturaProdutoCursorWrapper;
import com.asuswork.jamor.facturasapp.Database.RelacaoProdutoFactura.RelacaoFacturaProdutosDbScheme;
import com.asuswork.jamor.facturasapp.R;

import java.util.ArrayList;
import java.util.Calendar;

public class EditarProdutoActivity extends AppCompatActivity {

    private String mUsername;

    private String producto_ID;

    private EditText comentario_field;
    private EditText valor_field;
    private EditText designacao_field;

    private Spinner categoria_field;
    private Button facturas_dialog_btn;

    private ArrayList<CheckBox> listaFacturasCheckboxes;

    private SQLiteDatabase mDatabase_produto;
    private SQLiteDatabase mDatabase_factura;
    private SQLiteDatabase mDatabase_relacaoFacturaProduto;

    private Dialog facturasDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_produto);
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

                                RelacaoFacturaProduto relacao = getRelacaoNovoProduto();
                                if(relacao!=null) {

                                    saveAllInfoToDatabase(relacao);

                                    Toast.makeText(getApplicationContext(), "Produto editado com sucesso !", Toast.LENGTH_SHORT).show();
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

        preencherCurrentValues();
    }

    private void saveAllInfoToDatabase(RelacaoFacturaProduto relacao){

        deleteRelacoesProdutoFactura();

        ContentValues values_produto = getContentValues_produto(relacao.getProduto());
        mDatabase_produto.update(ProdutoDbScheme.ProdutoTable.NAME, values_produto, ProdutoDbScheme.ProdutoTable.Cols.ID + " = ?", new String[]{producto_ID});

        if(!relacao.getListaFacturas().isEmpty()){
            for(String factura : relacao.getListaFacturas()){
                String factura_ID = factura.split(" - ")[0];
                ContentValues values_relacao = getContentValues_relacao(relacao.getProduto().getID(), factura_ID);
                mDatabase_relacaoFacturaProduto.insert(RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.NAME, null, values_relacao);
            }
        }

    }

    private void deleteRelacoesProdutoFactura(){
        mDatabase_relacaoFacturaProduto.delete(RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.NAME, RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_PRODUTO + " = ?", new String[]{producto_ID});
    }


    private ContentValues getContentValues_produto(Produto p){
        ContentValues values = new ContentValues();
        values.put(ProdutoDbScheme.ProdutoTable.Cols.ID, p.getID());
        values.put(ProdutoDbScheme.ProdutoTable.Cols.NOME, p.getNome());
        values.put(ProdutoDbScheme.ProdutoTable.Cols.CATEGORIA, p.getCategoria());
        values.put(ProdutoDbScheme.ProdutoTable.Cols.COMENTARIO, p.getComentario());
        values.put(ProdutoDbScheme.ProdutoTable.Cols.DATA, p.getData());
        values.put(ProdutoDbScheme.ProdutoTable.Cols.USER, p.getUser());
        values.put(ProdutoDbScheme.ProdutoTable.Cols.VALOR, p.getValor());
        return values;
    }

    private ContentValues getContentValues_relacao(String produto_ID, String factura_ID){
        ContentValues values = new ContentValues();
        values.put(RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_FACTURA, factura_ID);
        values.put(RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_PRODUTO, produto_ID);
        return values;
    }

    private RelacaoFacturaProduto getRelacaoNovoProduto(){
        String designacao = designacao_field.getText().toString();
        String comentario = comentario_field.getText().toString();
        int categoria = categoria_field.getSelectedItemPosition();
        String valor = valor_field.getText().toString();
        ArrayList<String> facturas = getFacturasArrayList();

        Produto p = new Produto(designacao, valor, categoria, getCurrentDataString(), comentario, mUsername);
        RelacaoFacturaProduto rel = new RelacaoFacturaProduto(p, facturas);

        if(designacao.isEmpty()  ||  categoria==0  ||  valor.isEmpty()){
            return null;
        }

        return rel;
    }

    private ArrayList<String> getFacturasArrayList(){
        ArrayList<String> facturas = new ArrayList<String>();

        for(CheckBox c : listaFacturasCheckboxes){
            if(c.isChecked()){
                facturas.add(c.getText().toString());
            }
        }

        return facturas;
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


    private void inicializarVariaveisGlobais(){

        mDatabase_produto = new ProdutoBaseHelper(getApplicationContext()).getWritableDatabase();
        mDatabase_factura = new FacturaBaseHelper(getApplicationContext()).getWritableDatabase();
        mDatabase_relacaoFacturaProduto = new RelacaoFacturaProdutoBaseHelper(getApplicationContext()).getWritableDatabase();

        mUsername = getIntent().getStringExtra("username");
        producto_ID = getIntent().getStringExtra("producto_ID");


        comentario_field = (EditText) findViewById(R.id.novoProduto_comentario);
        valor_field = (EditText) findViewById(R.id.novoProduto_valor);
        designacao_field = (EditText) findViewById(R.id.novoProduto_designacao);

        categoria_field = (Spinner) findViewById(R.id.novoProduto_categoria);
        String[] items =  mergeStringArrays(new String[]{"Escolha a categoria do novo produto..."}, Produto.CATEGORIAS_PRODUTO);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        categoria_field.setAdapter(adapter);


        inicializarFacturasDialog();


        listaFacturasCheckboxes = new ArrayList<CheckBox>();
        String[] items_facturas =  getFacturasStringArray();
        for(String str : items_facturas){
            CheckBox checkBox = new CheckBox(facturasDialog.getContext());
            checkBox.setChecked(false);
            checkBox.setText(str);
            ((LinearLayout)facturasDialog.findViewById(R.id.layout_facturas)).addView(checkBox);
            listaFacturasCheckboxes.add(checkBox);
        }


        facturas_dialog_btn = (Button) findViewById(R.id.novoProduto_facturas);
        facturas_dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facturasDialog.show();
            }
        });

    }

    private void preencherCurrentValues(){
        Produto p = getProdutoToEdit();
        RelacaoFacturaProduto relacao = getRelacaoFacturaProduto(p);
        for(String factura : relacao.getListaFacturas()){
            for(CheckBox box : listaFacturasCheckboxes){
                if(box.getText().toString().contains(factura)){
                    box.setChecked(true);
                }
            }
        }

        comentario_field.setText(p.getComentario());
        valor_field.setText(p.getValor());
        designacao_field.setText(p.getNome());

        categoria_field.setSelection(p.getCategoria());

    }

    private Produto getProdutoToEdit(){
        Cursor cc = mDatabase_produto.query(
                ProdutoDbScheme.ProdutoTable.NAME,
                null,
                ProdutoDbScheme.ProdutoTable.Cols.ID + " = ?",
                new String[]{producto_ID},
                null,
                null,
                null
        );

        ProdutoCursorWrapper c_produtos = new ProdutoCursorWrapper(cc);

        if(!c_produtos.moveToFirst()){
            return null;
        }


        Produto p = c_produtos.getProduto();


        return p;
    }

    private RelacaoFacturaProduto getRelacaoFacturaProduto(Produto p){
        //RelacaoFacturaProduto relacao;
        ArrayList<String> lista_facturas = new ArrayList<String>();

        Cursor c = mDatabase_relacaoFacturaProduto.query(
                RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.NAME,
                new String[]{RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_FACTURA},
                RelacaoFacturaProdutosDbScheme.RelacaoFacturaProdutosTable.Cols.ID_PRODUTO + " = ?",
                new String[]{producto_ID},
                null,
                null,
                "1 ASC"
        );

        RelacaoFacturaProdutoCursorWrapper c_relacao= new RelacaoFacturaProdutoCursorWrapper(c);

        if(c_relacao.moveToFirst()){
            while(!c_relacao.isAfterLast()){

                lista_facturas.add(c_relacao.getSingleFactura_ID());

                c_relacao.moveToNext();
            }
        }

        return new RelacaoFacturaProduto(p, lista_facturas);
    }

    private void inicializarFacturasDialog(){

        facturasDialog = new Dialog(EditarProdutoActivity.this);
        facturasDialog.setContentView(R.layout.facturas_dialog);
        facturasDialog.setTitle("Escolha as factuas para associar ao novo produto");

        ((LinearLayout)facturasDialog.findViewById(R.id.layout_facturas_save)).setOrientation(LinearLayout.HORIZONTAL);
        Button b = new Button(facturasDialog.getContext());
        b.setText("Voltar");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facturasDialog.dismiss();
            }
        });
        ((LinearLayout)facturasDialog.findViewById(R.id.layout_facturas_save)).addView(b);


        Button b3 = new Button(facturasDialog.getContext());
        b3.setText("Inverter selecção");
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<listaFacturasCheckboxes.size(); i++){
                    CheckBox c = listaFacturasCheckboxes.get(i);
                    if(c.isChecked()){
                        c.setChecked(false);
                    }else{
                        c.setChecked(true);
                    }
                }
            }
        });
        ((LinearLayout)facturasDialog.findViewById(R.id.layout_facturas_save)).addView(b3);

        facturasDialog.setCanceledOnTouchOutside(true);
        facturasDialog.setCancelable(true);
    }

    private String[] getFacturasStringArray(){
        String[] facturas_ID;

        Cursor c = mDatabase_factura.query(
                FacturaDbScheme.FacturaTable.NAME,
                null,
                FacturaDbScheme.FacturaTable.Cols.USER + " = ?",
                new String[]{mUsername},
                null,
                null,
                null
        );

        FacturaCursorWrapper cursor= new FacturaCursorWrapper(c);

        if(!cursor.moveToFirst()){
            return new String[]{};
        }

        facturas_ID = new String[cursor.getCount()];
        int count=0;

        while(!c.isAfterLast()){
            facturas_ID[count]=cursor.getFactura().toString();
            count++;
            c.moveToNext();
        }

        return facturas_ID;
    }


    private String[] mergeStringArrays(String[] a, String[] b){
        int length = a.length + b.length;
        String[] c = new String[length];
        int count=0;

        for(String str : a){
            c[count] = str;
            count++;
        }

        for(String str : b){
            c[count] = str;
            count++;
        }

        return c;
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", mUsername);
        startActivity(intent);
        this.finish();
    }
}
