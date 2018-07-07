package com.asuswork.jamor.facturasapp.Database.Users;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by jamor on 28/02/2018.
 */

public class UsersCursorWrapper extends CursorWrapper {

    private Cursor cursor;

    public UsersCursorWrapper(Cursor cursor) {
        super(cursor);
        this.cursor=cursor;
    }

    public User getUser(boolean needUser, boolean needPass, boolean needIsPublic){
        User user = new User();

        if(needUser){
            user.setUsername(getString(getColumnIndex(UsersDbScheme.UsersTable.Cols.USERNAME)));
        }

        if(needPass){
            user.setPassword(getString(getColumnIndex(UsersDbScheme.UsersTable.Cols.PASSWORD)));
        }

        if(needIsPublic){
            user.setPublic(getString(getColumnIndex(UsersDbScheme.UsersTable.Cols.IS_PUBLIC)));
        }

        return user;
    }
}