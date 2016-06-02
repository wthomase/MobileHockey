package com.project.tcss450.wthomase.mobilehockey.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.project.tcss450.wthomase.mobilehockey.R;

/**
 * Local DB class used to store username and password per project specifications.
 */

public class UserInfoDB {

    /** Stores the version of this DB */
    public static final int DB_VERSION = 1;

    /** Stores the name of the DB */
    public static final String DB_NAME = "Users.db";

    /** Stores the instance of the inner helper class */
    private UserInfoDBHelper mUserInfoDBHelper;
    /** Stores the actual SQLite DB. */
    private SQLiteDatabase mSQLiteDatabase;

    /** Constructs a new UserInfoDB class. */
    public UserInfoDB(Context context) {
        mUserInfoDBHelper = new UserInfoDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mUserInfoDBHelper.getWritableDatabase();
    }

    /**
     * Inserts the user info into the local sqlite table. Returns true if successful, false otherwise.
     * @param userid
     * @param password
     * @return true or false
     */
    public boolean insertUser(String userid, String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", userid);
        contentValues.put("password", password);

        long rowId = mSQLiteDatabase.insert("UserInfo", null, contentValues);
        return rowId != -1;
    }

    /**
     * Helper method to close the SQLite DB.
     */
    public void closeDB() {
        mSQLiteDatabase.close();
    }


    /**
     * Inner helper class used to create and drop the tables inside of the local DB.
     */
    class UserInfoDBHelper extends SQLiteOpenHelper {

        /** String used to store the syntax required to create a new table in the DB */
        private final String CREATE_USERS_SQL;

        /** String used to store the syntax required to drop a new table in the DB. */
        private final String DROP_USERS_SQL;

        /**
         * Constructs a new UserInfoDBHelper.
         *
         * @param context
         * @param name
         * @param factory
         * @param version
         */
        public UserInfoDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_USERS_SQL = context.getString(R.string.CREATE_USERS_TABLE_SQL);
            DROP_USERS_SQL = context.getString(R.string.DROP_USERS_TABLE_SQL);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_USERS_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_USERS_SQL);
            onCreate(sqLiteDatabase);
        }
    }


}

