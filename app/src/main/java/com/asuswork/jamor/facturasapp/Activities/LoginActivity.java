package com.asuswork.jamor.facturasapp.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asuswork.jamor.facturasapp.Database.Users.User;
import com.asuswork.jamor.facturasapp.Database.Users.UsersBaseHelper;
import com.asuswork.jamor.facturasapp.Database.Users.UsersCursorWrapper;
import com.asuswork.jamor.facturasapp.Database.Users.UsersDbScheme;
import com.asuswork.jamor.facturasapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private UserRegistrationTask mRegTask = null;

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private static final int ACTION_LOGIN = 0;
    private static final int ACTION_REGISTO = 1;

    private static final int ERROR_UNKNOWN_USER = 0;
    private static final int ERROR_WRONG_PASSWORD = 1;
    //private static final int ERROR_ALREADY_USED_USERNAME = 2;

    private static final String PREFS_NAME = "FacturasAPP_PrefsFile";
    private static final String PREFS_LAST_LOGIN = "FacturasAPP_PrefsFile_LastLogin";
    private static final String PREFS_LAST_USER = "FacturasAPP_PrefsFile_User";
    private static final String PREFS_LAST_USAGE = "FacturasAPP_PrefsFile_LastUsage";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!getIntent().getBooleanExtra("from_main_screen", false)){
            String lastUser = checkAppLoginTimeValidation();
            if(!lastUser.isEmpty()){
                Toast.makeText(getApplicationContext(), "Login na conta do user:  " + lastUser, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("username", lastUser);
                startActivity(intent);
                finish();
                //return;
            }
        }

        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptAction(ACTION_LOGIN);
                    return true;
                }
                return false;
            }
        });

        Button mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAction(ACTION_LOGIN);
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.registar_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAction(ACTION_REGISTO);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    private String checkAppLoginTimeValidation(){
        /*
            return true if previous login permitted and false otherwise
        */
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

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        String lastLogin = settings.getString(PREFS_LAST_LOGIN, "");
        String lastUser = settings.getString(PREFS_LAST_USER, "");

        if (!lastLogin.isEmpty()) {
            settings.edit().remove(PREFS_LAST_LOGIN).apply();
            settings.edit().remove(PREFS_LAST_USER).apply();
            if(Math.abs(getMinutesFromTimestamp(lastLogin)-minute)<=5){
                settings.edit().putString(PREFS_LAST_LOGIN, time).apply();
                settings.edit().putString(PREFS_LAST_USER, lastUser).apply();
                return lastUser;
            }
        }

        return "";

    }

    private void saveLastUsageTime(){
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

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        settings.edit().putString(PREFS_LAST_USAGE, time).apply();
    }

    @Override
    public void onBackPressed(){
        saveLastUsageTime();
        finish();
    }

    private int getMinutesFromTimestamp(String timestamp){
        String minutes_str = timestamp.substring(timestamp.length()-2);
        return Integer.parseInt(minutes_str);
    }



    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUsernameView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptAction(int action) {
        if(action == ACTION_LOGIN){    // login
            if (mAuthTask != null) {
                return;
            }
        }else if(action == ACTION_REGISTO){    // registo
            if (mRegTask != null) {
                return;
            }
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if(password.isEmpty()){
            mPasswordView.setError(getString(R.string.error_empty_password));
            focusView = mPasswordView;
            cancel = true;
        }else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_empty_username));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_short_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            if(action == ACTION_LOGIN) {    // login
                mAuthTask = new UserLoginTask(username, password);
                mAuthTask.execute((Void) null);
            }else if(action == ACTION_REGISTO){  // registo
                mRegTask = new UserRegistrationTask(username, password);
                mRegTask.execute((Void) null);
            }
        }
    }

    private boolean isUsernameValid(String user) {
        //T.O.D.O: Replace this with your own logic
        return user.length() >= 4;
    }

    private boolean isPasswordValid(String password) {
        //T.O.D.O: Replace this with your own logic
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUsernameView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        private SQLiteDatabase mDatabase_users;

        private int ERROR_TYPE;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
            mDatabase_users = new UsersBaseHelper(getApplicationContext()).getReadableDatabase();

            ERROR_TYPE = 0;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Cursor c = mDatabase_users.query(
                    UsersDbScheme.UsersTable.NAME,
                    new String[]{UsersDbScheme.UsersTable.Cols.USERNAME, UsersDbScheme.UsersTable.Cols.PASSWORD},
                    UsersDbScheme.UsersTable.Cols.USERNAME + " = ?",
                    new String[]{mUsername},
                    null,
                    null,
                    null
            );

            UsersCursorWrapper cursor = new UsersCursorWrapper(c);

            if(!cursor.moveToFirst()){
                ERROR_TYPE = ERROR_UNKNOWN_USER;     // username não existe na base de dados
                return false;
            }else{
                User user = cursor.getUser(true, true, false);

                if(!mPassword.equals(user.getPassword())){
                    ERROR_TYPE = ERROR_WRONG_PASSWORD;     // password errada
                    return false;
                }

                return true;
            }

            /*try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mUsername)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/



            //return true;
        }

        private void writeUsernameToPrefs(String username){

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


            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

            if (!settings.getString(PREFS_LAST_LOGIN, "").isEmpty() || !settings.getString(PREFS_LAST_USER, "").isEmpty()) {
                settings.edit().remove(PREFS_LAST_LOGIN).apply();
                settings.edit().remove(PREFS_LAST_USER).apply();
            }

            settings.edit().putString(PREFS_LAST_LOGIN, time).apply();
            settings.edit().putString(PREFS_LAST_USER, username).apply();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(getApplicationContext(), "Login efectuado com sucesso!\n[USER:  " + mUsername + "]", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("username", mUsername);
                writeUsernameToPrefs(mUsername);
                startActivity(intent);
                finish();

            } else {
                if(ERROR_TYPE == ERROR_WRONG_PASSWORD) {
                    Toast.makeText(getApplicationContext(), "Password incorrecta!", Toast.LENGTH_SHORT).show();
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }else if(ERROR_TYPE == ERROR_UNKNOWN_USER) {
                    Toast.makeText(getApplicationContext(), "Username não existe!", Toast.LENGTH_SHORT).show();
                    mUsernameView.setError(getString(R.string.error_non_existant_user));
                    mUsernameView.requestFocus();
                }
                ERROR_TYPE = 0;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        private SQLiteDatabase mDatabase_users;


        UserRegistrationTask(String username, String password) {
            mUsername = username;
            mPassword = password;
            mDatabase_users = new UsersBaseHelper(getApplicationContext()).getWritableDatabase();

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Cursor c = mDatabase_users.query(
                    UsersDbScheme.UsersTable.NAME,
                    new String[]{UsersDbScheme.UsersTable.Cols.USERNAME},
                    UsersDbScheme.UsersTable.Cols.USERNAME + " = ?",
                    new String[]{mUsername},
                    null,
                    null,
                    null
            );

            UsersCursorWrapper cursor = new UsersCursorWrapper(c);

            if(cursor.moveToFirst()){
                return false;
            }else{

                User user = new User(mUsername, mPassword, "y");

                ContentValues values = getContentValues(user);
                mDatabase_users.insert(UsersDbScheme.UsersTable.NAME, null, values);

                return true;
            }

            /*try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mUsername)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/



            //return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(getApplicationContext(), "Registo efectuado com sucesso!  [USER: " + mUsername + "]", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Username já em utilização!", Toast.LENGTH_SHORT).show();
                mUsernameView.setError(getString(R.string.error_alreadyUsed_user));
                mUsernameView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mRegTask = null;
            showProgress(false);
        }

        private ContentValues getContentValues(User u){
            ContentValues values = new ContentValues();
            values.put(UsersDbScheme.UsersTable.Cols.USERNAME, u.getUsername());
            values.put(UsersDbScheme.UsersTable.Cols.PASSWORD, u.getPassword());
            values.put(UsersDbScheme.UsersTable.Cols.IS_PUBLIC, u.isPublic() ? "y":"n");
            return values;
        }
    }
}

