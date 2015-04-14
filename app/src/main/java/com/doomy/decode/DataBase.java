/**
 * Copyright (C) 2015 Damien Chazoule
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.doomy.decode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ScanBD";
    private static final String TABLE_NAME = "ScansTable";
    private static final String KEY_ID = "id";
    private static final String KEY_FORMAT = "format";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_DATE = "date";
    private static final String[] COLONNES = { KEY_ID, KEY_FORMAT, KEY_CONTENT, KEY_DATE };

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("SQLite DB : ", "Constructeur");
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {

        String CREATE_TABLE = "CREATE TABLE ScansTable ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "format TEXT, "
                + "content TEXT, " + "date TEXT )";

        arg0.execSQL(CREATE_TABLE);
        Log.i("SQLite DB", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

        arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(arg0);
        Log.i("SQLite DB", "onUpgrade");
    }

    public void deleteAll() {
        SQLiteDatabase mDB = this.getWritableDatabase();
        mDB.delete(TABLE_NAME, null, null);
        mDB.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        Log.i("SQLite DB", "Delete All");
    }

    public void deleteOne(Scan mScan) {

        // 1. Get reference to writable DB
        SQLiteDatabase mDB = this.getWritableDatabase();
        mDB.delete(TABLE_NAME, // Table
                "id = ?", new String[] { String.valueOf(mScan.getID()) });
        mDB.close();
        Log.i("SQLite DB : Delete : ", mScan.toString());
    }

    public Scan showOne(int id) {
        SQLiteDatabase mDB = this.getReadableDatabase();
        Cursor mCursor = mDB.query(TABLE_NAME, // A. Table
                COLONNES, // B. Column names
                " id = ?", // C. Selections
                new String[] { String.valueOf(id) }, // D. Selections args
                null, // e. Group by
                null, // f. Having
                null, // g. Order by
                null); // h. Limit
        if (mCursor != null)
            mCursor.moveToFirst();
        Scan mScan = new Scan();
        mScan.setID(Integer.parseInt(mCursor.getString(0)));
        mScan.setFormat(mCursor.getString(1));
        mScan.setContent(mCursor.getString(2));
        mScan.setDate(mCursor.getString(3));
        // Log
        Log.i("SQLite DB : Show One  : ID =  " + id, mScan.toString());

        return mScan;
    }

    public List<Scan> showAll() {

        List<Scan> mScans = new LinkedList<>();
        String mQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase mDB = this.getWritableDatabase();
        Cursor mCursor = mDB.rawQuery(mQuery, null);
        Scan mScan = null;
        if (mCursor.moveToFirst()) {
            do {
                mScan = new Scan();
                mScan.setID(Integer.parseInt(mCursor.getString(0)));
                mScan.setFormat(mCursor.getString(1));
                mScan.setContent(mCursor.getString(2));
                mScan.setDate(mCursor.getString(3));
                mScans.add(mScan);
            } while (mCursor.moveToNext());
        }
        Log.i("SQLite DB : Show All : ", mScans.toString());
        return mScans;
    }

    public void addOne(Scan mScan) {

        SQLiteDatabase mDB = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FORMAT, mScan.getFormat());
        values.put(KEY_CONTENT, mScan.getContent());
        values.put(KEY_DATE, mScan.getDate());
        // Insertion
        mDB.insert(TABLE_NAME, // Table
                null, values);

        mDB.close();
        Log.i("SQLite DB : Add One  : ID =  " + mScan.getID(), mScan.toString());
    }

    public int updateOne(Scan mScan) {

        SQLiteDatabase mDB = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FORMAT, mScan.getFormat());
        values.put(KEY_CONTENT, mScan.getContent());
        values.put(KEY_DATE, mScan.getDate());

        int i = mDB.update(TABLE_NAME, // Table
                values, // Column/Value
                "id = ?", // Selections
                new String[] { String.valueOf(mScan.getID()) });

        mDB.close();
        Log.i("SQLite DB : Update One  : ID =  " + mScan.getID(), mScan.toString());

        return i;
    }

    public int getRowsCount() {
        String mQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase mDB = this.getReadableDatabase();
        Cursor mCursor = mDB.rawQuery(mQuery, null);
        int mCount = mCursor.getCount();
        mCursor.close();
        Log.i("SQLite DB : Row(s) : ", mCount + "");
        return mCount;
    }
}
