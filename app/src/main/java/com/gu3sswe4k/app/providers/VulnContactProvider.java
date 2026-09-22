package com.gu3sswe4k.app.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class VulnContactProvider extends ContentProvider {

    public static final String AUTHORITY = "com.gu3sswe4k.app.contacts";
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int CONTACT_ID = 1;
    private DbHelper dbHelper;

    static {
        MATCHER.addURI(AUTHORITY, "contacts/#", CONTACT_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                         String[] selectionArgs, String sortOrder) {
        long grantedId = Long.parseLong(uri.getLastPathSegment());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String grantClause = "_id=" + grantedId;
        String finalSelection = selection == null
                ? grantClause
                : grantClause + " AND (" + selection + ")";

        return db.rawQuery(
                "SELECT _id, name, phone, email, notes FROM contacts WHERE " + finalSelection,
                selectionArgs);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert("contacts", null, values);
        return Uri.parse(AUTHORITY + "/contacts/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.item/vnd.gu3sswe4k.contact";
    }

    private static class DbHelper extends SQLiteOpenHelper {
        DbHelper(Context ctx) {
            super(ctx, "vulncontacts.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE contacts (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT, phone TEXT, email TEXT, notes TEXT)");

            db.execSQL("INSERT INTO contacts (name, phone, email, notes) VALUES " +
                    "('Alice Victim','+1-555-SECRET-01','alice.victim@corp.example','no secrets here')");
            db.execSQL("INSERT INTO contacts (name, phone, email, notes) VALUES " +
                    "('Bob Manager','+1-555-777-0002','bob.manager@corp.example','FLAG{CP-01_grant_bypass}')");
            db.execSQL("INSERT INTO contacts (name, phone, email, notes) VALUES " +
                    "('Carol Doctor','+1-555-999-0003','carol.doctor@med.example','patient notes')");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }
}
