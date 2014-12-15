package com.vanzstuff.readdit.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReadditSQLOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "readdit.db";

    public ReadditSQLOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TAG = "CREATE TABLE " + ReadditContract.Tag.TABLE_NAME + " ( " +
                ReadditContract.Tag._ID + " INTEGER PRIMARY KEY, " +
                ReadditContract.Tag.COLUMN_NAME + " TEXT UNIQUE NOT NULL );";
        final String CREATE_POST = "CREATE TABLE " + ReadditContract.Post.TABLE_NAME + " ( " +
                ReadditContract.Post._ID + " INTEGER PRIMARY KEY, " +
                ReadditContract.Post.COLUMN_TITLE + " TEXT NOT NULL, " +
                ReadditContract.Post.COLUMN_CONTENT + " TEXT NOT NULL, " +
                ReadditContract.Post.COLUMN_DATE + " INTEGER NOT NULL, " +
                ReadditContract.Post.COLUMN_SUBREDDIT + " TEXT NOT NULL, " +
                ReadditContract.Post.COLUMN_USER +  " TEXT NOT NULL, " +
                ReadditContract.Post.COLUMN_VOTES + " INTEGER DEFAULT 0, " +
                ReadditContract.Post.COLUMN_THREADS + " INTEGER DEFAULT 0);";
        final String CREATE_TAG_X_POST = "CREATE TABLE " + ReadditContract.TagXPost.TABLE_NAME + " ( " +
                ReadditContract.TagXPost.COLUMN_TAG + " INTEGER REFERENCES " + ReadditContract.Tag.TABLE_NAME + "( " + ReadditContract.Tag._ID + ") , " +
                ReadditContract.TagXPost.COLUMN_POST + " INTEGER REFERENCES " + ReadditContract.Post.TABLE_NAME + "( " + ReadditContract.Post._ID + "), " +
                "PRIMARY KEY ( " + ReadditContract.TagXPost.COLUMN_TAG + ", " + ReadditContract.TagXPost.COLUMN_POST + " ));";
        final String CREATE_COMMENT = "CREATE TABLE " + ReadditContract.Comment.TABLE_NAME + " ( " +
                ReadditContract.Comment._ID + " INTEGER PRIMARY KEY," +
                ReadditContract.Comment.COLUMN_PARENT + " INTEGER REFERENCES " + ReadditContract.Comment.TABLE_NAME + " ( " + ReadditContract.Comment._ID + " )," +
                ReadditContract.Comment.COLUMN_CONTENT + " TEXT NOT NULL," +
                ReadditContract.Comment.COLUMN_DATE + " INTEGER NOT NULL, " +
                ReadditContract.Comment.COLUMN_USER + " TEXT NOT NULL, " +
                ReadditContract.Comment.COLUMN_POST + " INTEGER REFERENCES " + ReadditContract.Post.TABLE_NAME + " ( " + ReadditContract.Post._ID + " ));";
        final String CREATE_SUBSCRIBE = "CREATE TABLE " + ReadditContract.Subreddit.TABLE_NAME + " ( " +
                ReadditContract.Subreddit._ID + " INTEGER PRIMARY KEY, " +
                ReadditContract.Subreddit.COLUMN_NAME + " TEXT NOT NULL );";

        db.execSQL(CREATE_TAG);
        db.execSQL(CREATE_POST);
        db.execSQL(CREATE_TAG_X_POST);
        db.execSQL(CREATE_COMMENT);
        db.execSQL(CREATE_SUBSCRIBE);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //is not necessary, for while
    }
}