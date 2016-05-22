package in.haridas.creditpay.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.StringBuilderPrinter;

import java.io.StringBufferInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import in.haridas.creditpay.database.CardDatabaseHelper;
import in.haridas.creditpay.database.CardTable;

/**
 * Created by haridasnss on 22/5/16.
 */
public class CardContentProvider extends ContentProvider {

    // DB
    private CardDatabaseHelper database;

    // For UriMatcher
    private static final int CARDS = 1;
    private static final int CARD_ID = 2;

    // Provider authority name.
    private static final String AUTHORITY = "in.haridas.creditpay.contentprovider";

    private static final String BASE_PATH = "cards";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/cards";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/card";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, CARDS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", CARD_ID);
    }


    @Override
    public boolean onCreate() {
        database = new CardDatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        validateColumns(projection);
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(CardTable.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CARDS:
                break;
            case CARD_ID:
                // Adding the id to the WHERE clause.
                queryBuilder.appendWhere(CardTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        // Notify potential event listeners.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        long id = 0;

        switch (uriType) {
            case CARDS:
                id = db.insert(CardTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case CARDS:
                rowsDeleted = db.delete(CardTable.TABLE_NAME, selection, selectionArgs);
                break;
            case CARD_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(CardTable.TABLE_NAME,
                            CardTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(CardTable.TABLE_NAME,
                            CardTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rawsUpdated = 0;
        switch (uriType) {
            case CARDS:
                rawsUpdated = db.update(CardTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CARD_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rawsUpdated = db.update(CardTable.TABLE_NAME, values,
                            CardTable.COLUMN_ID + "=" + id, null);
                } else {
                    rawsUpdated = db.update(CardTable.TABLE_NAME, values,
                            CardTable.COLUMN_ID + "=" + id + " and " + selection, null);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rawsUpdated;
    }

    private void validateColumns(String[] projection) {
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(CardTable.columns));
            if (!availableColumns.containsAll(requestedColumns)){
                throw new IllegalArgumentException("Unknown columns in projects: " + projection);
            }
        }
    }
}
