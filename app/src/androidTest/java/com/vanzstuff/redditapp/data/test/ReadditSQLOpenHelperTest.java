package com.vanzstuff.redditapp.data.test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.vanzstuff.readditapp.data.ReadditSQLOpenHelper;
import com.vanzstuff.redditapp.data.ReadditContract;

/**
 * Created by vanz on 16/11/14.
 */
public class ReadditSQLOpenHelperTest extends AndroidTestCase{

    private ReadditSQLOpenHelper mDBHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getContext().deleteDatabase("readdit.db");
        mDBHelper= new ReadditSQLOpenHelper(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mDBHelper.close();
        mDBHelper = null;
    }

    public void testOnCreate(){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM main.sqlite_master WHERE type=?;", new String[]{"table"});
        assertEquals(7, cursor.getCount());
        while (cursor.moveToNext()){
            if ( cursor.getString(1).equals(ReadditContract.Subscribe.TABLE_NAME)){
                assertEquals("CREATE TABLE subscribe ( _id INTEGER PRIMARY KEY, subreddit TEXT NOT NULL )", cursor.getString(4));
            }else if (cursor.getString(1).equals(ReadditContract.Comment.TABLE_NAME)){
                assertEquals("CREATE TABLE comment ( _id INTEGER PRIMARY KEY,parent INTEGER REFERENCES comment ( _id ),content TEXT NOT NULL,date TEXT NOT NULL, user TEXT NOT NULL, post INTEGER REFERENCES post ( _id ))", cursor.getString(4));
            }else if( cursor.getString(1).equals(ReadditContract.Post.TABLE_NAME)) {
                assertEquals("CREATE TABLE post ( _id INTERGER PRIMARY KEY, content TEXT NOT NULL, date TEXT NOT NULL, subredditINTEGER REFERENCES subreddit ( _id ),user TEXT NOT NULL,votes INTEGER DEFAULT 0)", cursor.getString(4));
            }else if(cursor.getString(1).equals(ReadditContract.Subreddit.TABLE_NAME)) {
                assertEquals("CREATE TABLE subreddit ( _id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE)", cursor.getString(4));
            }else if(cursor.getString(1).equals(ReadditContract.Tag.TABLE_NAME)) {
                assertEquals("CREATE TABLE tag ( _id INTEGER PRIMARY KEY, name TEXT NOT NULL )", cursor.getString(4));
            }else if(cursor.getString(1).equals(ReadditContract.TagXPost.TABLE_NAME)) {
                assertEquals("CREATE TABLE tag_x_post ( tag INTEGER REFERENCES tag( _id) , post INTEGER REFERENCES post( _id), PRIMARY KEY ( tag, post ))", cursor.getString(4));
            }else if (cursor.getString(1).equals("android_metadata")) {
                continue;
            }else{
                fail("Unexpected table: " + cursor.getString(0));
            }
        }
    }
}
