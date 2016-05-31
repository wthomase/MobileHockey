package com.project.tcss450.wthomase.mobilehockey.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.project.tcss450.wthomase.mobilehockey.R;

public class UserInfoDB {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Course.db";

    private UserInfoDBHelper mUserInfoDBHelper;
    private SQLiteDatabase mSQLiteDatabase;


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

    public void closeDB() {
        mSQLiteDatabase.close();
    }

    class UserInfoDBHelper extends SQLiteOpenHelper {

        private final String CREATE_COURSE_SQL;

        private final String DROP_COURSE_SQL;

        public UserInfoDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_COURSE_SQL = context.getString(R.string.CREATE_COURSE_SQL);
            DROP_COURSE_SQL = context.getString(R.string.DROP_COURSE_SQL);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_COURSE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_COURSE_SQL);
            onCreate(sqLiteDatabase);
        }
    }


}

