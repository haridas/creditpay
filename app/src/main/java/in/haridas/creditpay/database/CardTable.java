package in.haridas.creditpay.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by haridas on 4/12/15.
 */

/**
 * Create Table definition for each models specifically for easy separation.
 */
public class CardTable {

    public static final String TABLE_NAME = "cards";
    public static final String COLUMN_ID = "_id";
    public static final String CARD_NAME = "card_name";
    public static final String BILLING_DAY = "billing_day";
    public static final String GRACE_PERIOD = "grace_period";

    public static final String[] columns = {COLUMN_ID, CARD_NAME, BILLING_DAY, GRACE_PERIOD};

    // Table creation SQL statement.
    private static final String CREATE_CMD = "CREATE TABLE cards ("
            + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + CARD_NAME + " varchar(20) NOT NULL, "
            + BILLING_DAY + " smallint unsigned NOT NULL, "
            + GRACE_PERIOD + " smallint unsigned NOT NULL"
            + ")";

    public static void onCreate(SQLiteDatabase db) {
        Log.i(CardTable.class.getName(), "Creating Cards table in DB");
        db.execSQL(CREATE_CMD);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CardTable.class.getName(), " Upgrading database from version: " +
                oldVersion + " to: " + newVersion +
                ", cleaning all previous data..");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
