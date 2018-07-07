package com.asuswork.jamor.facturasapp.Activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.asuswork.jamor.facturasapp.Database.Facturas.FacturaBaseHelper;
import com.asuswork.jamor.facturasapp.Database.Facturas.FacturaDbScheme;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoBaseHelper;
import com.asuswork.jamor.facturasapp.Database.Produto.ProdutoDbScheme;
import com.asuswork.jamor.facturasapp.Database.Users.User;
import com.asuswork.jamor.facturasapp.Database.Users.UsersBaseHelper;
import com.asuswork.jamor.facturasapp.Database.Users.UsersCursorWrapper;
import com.asuswork.jamor.facturasapp.Database.Users.UsersDbScheme;
import com.asuswork.jamor.facturasapp.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class UserSettingsActivity extends AppCompatActivity {

    private String username;
    private Button goBackBtn;
    private Button changeUsernameBtn;
    private Button changePasswordBtn;
    public Switch isVisble;

    private SQLiteDatabase mDatabase_users;
    private SQLiteDatabase mDatabase_produtos;
    private SQLiteDatabase mDatabase_facturas;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        inicializarGlobalVars();
        isVisble.setChecked(isUserVisible());

        inicializarBtnsOnClickListeners();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void inicializarGlobalVars() {

        username = getIntent().getStringExtra("username");

        goBackBtn = (Button) findViewById(R.id.goBack_settings_btn);
        changeUsernameBtn = (Button) findViewById(R.id.change_username_btn);
        changePasswordBtn = (Button) findViewById(R.id.change_password_btn);

        isVisble = (Switch) findViewById(R.id.user_public_switch);

        mDatabase_users = new UsersBaseHelper(getApplicationContext()).getWritableDatabase();
        mDatabase_produtos = new ProdutoBaseHelper(getApplicationContext()).getWritableDatabase();
        mDatabase_facturas = new FacturaBaseHelper(getApplicationContext()).getWritableDatabase();

    }


    private void inicializarBtnsOnClickListeners() {
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });


        changeUsernameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUsernameDialog();
            }
        });


        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordDialog();
            }
        });


        isVisble.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // "isChecked" é o que indica se o switch está 'on' ou 'off'

                Toast.makeText(getApplicationContext(), "[" + username + "] tornou o seu perfil " + (isChecked ? "visível" : "oculto"), Toast.LENGTH_SHORT).show();

                updateUserVisibility(isChecked);
            }
        });


    }

    private void updateUserUsername(String novo_username) {
        ContentValues values = new ContentValues();
        values.put(UsersDbScheme.UsersTable.Cols.USERNAME, novo_username);
        mDatabase_users.update(UsersDbScheme.UsersTable.NAME, values, UsersDbScheme.UsersTable.Cols.USERNAME + " = ?", new String[]{username});


        ContentValues values_produtos = new ContentValues();
        values_produtos.put(ProdutoDbScheme.ProdutoTable.Cols.USER, novo_username);
        mDatabase_produtos.update(ProdutoDbScheme.ProdutoTable.NAME, values_produtos, ProdutoDbScheme.ProdutoTable.Cols.USER + " = ?", new String[]{username});


        ContentValues values_facturas = new ContentValues();
        values_facturas.put(FacturaDbScheme.FacturaTable.Cols.USER, novo_username);
        mDatabase_facturas.update(FacturaDbScheme.FacturaTable.NAME, values_facturas, FacturaDbScheme.FacturaTable.Cols.USER + " = ?", new String[]{username});


        username = novo_username;
    }

    private boolean usernameAlreadyExists(String check_username){
        Cursor c = mDatabase_users.query(
                UsersDbScheme.UsersTable.NAME,
                new String[]{UsersDbScheme.UsersTable.Cols.USERNAME},
                UsersDbScheme.UsersTable.Cols.USERNAME + " = ?",
                new String[]{check_username},
                null,
                null,
                null
        );

        UsersCursorWrapper cursor = new UsersCursorWrapper(c);

        return cursor.moveToFirst();

    }

    private void changeUsernameDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserSettingsActivity.this);
        alertDialog.setTitle("[" + username + "] mudar o username");
        //alertDialog.setMessage("Insira o novo username");

        final EditText input = new EditText(UserSettingsActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        input.setText("");
        input.setHint("Insira o novo username");

        alertDialog.setView(input);

        alertDialog.setPositiveButton("Confirmar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String novo_username= input.getText().toString();
                        if (!novo_username.isEmpty()) {
                            if (username.equals(novo_username)) {
                                Toast.makeText(getApplicationContext(),
                                        "O novo username não pode ser igual ao username actual!", Toast.LENGTH_SHORT).show();

                                input.setText("");
                                input.setHint("Insira o novo username");

                            }else if(novo_username.length()<4){
                                Toast.makeText(getApplicationContext(),
                                        "O novo username tem que ter 4 ou mais caractéres!", Toast.LENGTH_SHORT).show();

                                input.setText("");
                                input.setHint("Insira o novo username");
                            } else if(!usernameAlreadyExists(novo_username)){

                                updateUserUsername(novo_username);

                                Toast.makeText(getApplicationContext(),
                                        "Username alterado com sucesso!", Toast.LENGTH_SHORT).show();
                            }else{

                                Toast.makeText(getApplicationContext(),
                                        "O novo username já está a ser utilizado por outro user!", Toast.LENGTH_SHORT).show();

                                input.setText("");
                                input.setHint("Insira o novo username");

                            }
                        }else {
                            Toast.makeText(getApplicationContext(),
                                    "O novo username não pode ser vazio!", Toast.LENGTH_SHORT).show();

                            input.setText("");
                            input.setHint("Insira o novo username");
                        }
                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void changePasswordDialog() {
        final String curr_password = getCurrUserPassword();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserSettingsActivity.this);
        alertDialog.setTitle("[" + username + "] mudar a password");
        //alertDialog.setMessage("Insira a nova password");

        final EditText input = new EditText(UserSettingsActivity.this);
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    String password = input.getText().toString();
                    if(!password.isEmpty()){
                        input.setHint("");
                    }
                }else{
                    String password = input.getText().toString();
                    if(password.isEmpty()){
                        input.setHint("Insira a nova password");
                    }
                }
            }

        });
        final EditText input_confirm = new EditText(UserSettingsActivity.this);
        input_confirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    String password = input_confirm.getText().toString();
                    if(!password.isEmpty()){
                        input_confirm.setHint("");
                    }
                }else{
                    String password = input_confirm.getText().toString();
                    if(password.isEmpty()){
                        input_confirm.setHint("Insira a nova password");
                    }
                }
            }

        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input_confirm.setLayoutParams(lp);

        input.setText("");
        input.setHint("Insira a nova password");
        input_confirm.setText("");
        input_confirm.setHint("Insira novamente a nova password");

        LinearLayout layout = new LinearLayout(alertDialog.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(input);
        layout.addView(input_confirm);

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("Confirmar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String password = input.getText().toString();
                        String password_confirm = input_confirm.getText().toString();
                        if (!password.isEmpty()) {
                            if(!password_confirm.isEmpty()){
                                if(password.equals(password_confirm)){
                                    if (curr_password.equals(password)) {
                                        Toast.makeText(getApplicationContext(),
                                                "A nova password tem que ser diferente da actual!", Toast.LENGTH_SHORT).show();
                                        input.setText("");
                                        input.setHint("Insira a nova password");
                                        input_confirm.setText("");
                                        input_confirm.setHint("Insira novamente a nova password");
                                    }else if(password.length()<4){
                                        Toast.makeText(getApplicationContext(),
                                                "A password tem que ter 4 ou mais caractéres!", Toast.LENGTH_SHORT).show();

                                        input.setText("");
                                        input.setHint("Insira a nova password");
                                        input_confirm.setText("");
                                        input_confirm.setHint("Insira novamente a nova password");
                                    }  else {

                                        updateUserPassword(password);

                                        Toast.makeText(getApplicationContext(),
                                                "Password alterada com sucesso!", Toast.LENGTH_SHORT).show();

                                        dialog.cancel();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(),
                                            "As 2 passwords não são iguais!", Toast.LENGTH_SHORT).show();
                                    input.setText("");
                                    input.setHint("Insira a nova password");

                                    input_confirm.setText("");
                                    input_confirm.setHint("Insira novamente a nova password");
                                }
                            }else{
                                input.setText("");
                                input.setHint("Insira a nova password");
                                input_confirm.setText("");
                                input_confirm.setHint("Insira novamente a nova password");

                                Toast.makeText(getApplicationContext(),
                                        "Tem que confirmar a sua nova password!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "A nova password não pode ser vazia!", Toast.LENGTH_SHORT).show();
                            input.setText("");
                            input.setHint("Insira a nova password");
                            input_confirm.setText("");
                            input_confirm.setHint("Insira novamente a nova password");
                        }

                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    private String getCurrUserPassword() {
        Cursor c = mDatabase_users.query(
                UsersDbScheme.UsersTable.NAME,
                new String[]{UsersDbScheme.UsersTable.Cols.PASSWORD},
                UsersDbScheme.UsersTable.Cols.USERNAME + " = ?",
                new String[]{username},
                null,
                null,
                null
        );

        UsersCursorWrapper cursor = new UsersCursorWrapper(c);

        if (!cursor.moveToFirst()) {
            return "";
        }

        User user = cursor.getUser(false, true, false);

        return user.getPassword();
    }

    private void updateUserPassword(String nova_password) {
        ContentValues values = new ContentValues();
        values.put(UsersDbScheme.UsersTable.Cols.PASSWORD, nova_password);
        mDatabase_users.update(UsersDbScheme.UsersTable.NAME, values, UsersDbScheme.UsersTable.Cols.USERNAME + " = ?", new String[]{username});
    }

    private void updateUserVisibility(boolean isVisible) {
        ContentValues values = new ContentValues();
        values.put(UsersDbScheme.UsersTable.Cols.IS_PUBLIC, isVisible ? "y":"n");
        mDatabase_users.update(UsersDbScheme.UsersTable.NAME, values, UsersDbScheme.UsersTable.Cols.USERNAME + " = ?", new String[]{username});
    }

    private boolean isUserVisible() {
        Cursor c = mDatabase_users.query(
                UsersDbScheme.UsersTable.NAME,
                new String[]{UsersDbScheme.UsersTable.Cols.IS_PUBLIC},
                UsersDbScheme.UsersTable.Cols.USERNAME + " = ?",
                new String[]{username},
                null,
                null,
                null
        );

        UsersCursorWrapper cursor = new UsersCursorWrapper(c);

        if (!cursor.moveToFirst()) {
            return false;
        }

        User user = cursor.getUser(false, false, true);

        return user.isPublic();

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        this.finish();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("UserSettings Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
