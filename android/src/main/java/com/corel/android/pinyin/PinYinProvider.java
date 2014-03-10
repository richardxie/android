/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.corel.android.pinyin;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.LiveFolders;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Provides access to a database of PinYin. Each note has a Chinese, the English
 * PinYin itself, a creation date and a modified data.
 */
public class PinYinProvider extends ContentProvider {

    private static final String TAG = "PinYinProvider";

    private static final String DATABASE_NAME = "pin_yin.db";
    private static final int DATABASE_VERSION = 2;
    private static final String PINYIN_TABLE_NAME = "pinyin";

    private static HashMap<String, String> sNotesProjectionMap;
    private static HashMap<String, String> sLiveFolderProjectionMap;

    private static final int PINYIN = 1;
    private static final int PINYIN_ID = 2;

    private static final UriMatcher sUriMatcher;

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + PINYIN_TABLE_NAME + " ("
                    + PinYin.PinYinColumns._ID + " INTEGER PRIMARY KEY,"
                    + PinYin.PinYinColumns.CHINESE + " TEXT,"
                    + PinYin.PinYinColumns.PINYIN + " TEXT,"
                    + PinYin.PinYinColumns.ENGLISH + " TEXT"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(PINYIN_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
        case PINYIN:
            qb.setProjectionMap(sNotesProjectionMap);
            break;

        case PINYIN_ID:
            qb.setProjectionMap(sNotesProjectionMap);
            qb.appendWhere(PinYin.PinYinColumns._ID + "=" + uri.getPathSegments().get(1));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = PinYin.PinYinColumns.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case PINYIN:
            return PinYin.PinYinColumns.CONTENT_TYPE;

        case PINYIN_ID:
            return PinYin.PinYinColumns.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != PINYIN) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        if (values.containsKey(PinYin.PinYinColumns.ENGLISH) == false) {
            Resources r = Resources.getSystem();
            values.put(PinYin.PinYinColumns.ENGLISH, r.getString(android.R.string.untitled));
        }

        if (values.containsKey(PinYin.PinYinColumns.PINYIN) == false) {
            values.put(PinYin.PinYinColumns.PINYIN, "");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(PINYIN_TABLE_NAME, PinYin.PinYinColumns.PINYIN, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(PinYin.PinYinColumns.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case PINYIN:
            count = db.delete(PINYIN_TABLE_NAME, where, whereArgs);
            break;

        case PINYIN_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.delete(PINYIN_TABLE_NAME, PinYin.PinYinColumns._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case PINYIN:
            count = db.update(PINYIN_TABLE_NAME, values, where, whereArgs);
            break;

        case PINYIN_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.update(PINYIN_TABLE_NAME, values, PinYin.PinYinColumns._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(PinYin.AUTHORITY, "pinyin", PINYIN);
        sUriMatcher.addURI(PinYin.AUTHORITY, "pinyin/#", PINYIN_ID);

        sNotesProjectionMap = new HashMap<String, String>();
        sNotesProjectionMap.put(PinYin.PinYinColumns._ID, PinYin.PinYinColumns._ID);
        sNotesProjectionMap.put(PinYin.PinYinColumns.CHINESE, PinYin.PinYinColumns.CHINESE);
        sNotesProjectionMap.put(PinYin.PinYinColumns.PINYIN, PinYin.PinYinColumns.PINYIN);
        sNotesProjectionMap.put(PinYin.PinYinColumns.ENGLISH, PinYin.PinYinColumns.ENGLISH);

        // Support for Live Folders.
        sLiveFolderProjectionMap = new HashMap<String, String>();
        sLiveFolderProjectionMap.put(LiveFolders._ID, PinYin.PinYinColumns._ID + " AS " +
                LiveFolders._ID);
        sLiveFolderProjectionMap.put(LiveFolders.NAME, PinYin.PinYinColumns.CHINESE + " AS " +
                LiveFolders.NAME);
        // Add more columns here for more robust Live Folders.
    }
}
