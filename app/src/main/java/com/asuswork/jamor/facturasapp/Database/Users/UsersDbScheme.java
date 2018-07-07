package com.asuswork.jamor.facturasapp.Database.Users;

/**
 * Created by jamor on 28/02/2018.
 */

public class UsersDbScheme {

    public static final class UsersTable{
        public static final String NAME = "users";

        public static final class Cols{
            public static final String ID = "id";
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
            public static final String IS_PUBLIC = "is_public";
        }
    }

}
