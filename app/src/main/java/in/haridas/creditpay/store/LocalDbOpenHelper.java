package in.haridas.creditpay.store;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by haridas on 4/12/15.
 */
public class LocalDbOpenHelper extends SQLiteOpenHelper {

    final public static String TABLE_NAME = "cards";
    final public static String CARD_NAME = "card_name";
    final public static String CARD_NUMBER = "card_number";
    final public static String BILL_DATE = "bill_date";
    final static String _ID = "_id";

    final public static String[] columns = {_ID, CARD_NAME, CARD_NUMBER, BILL_DATE};

    final private static String CREATE_CMD =
    "CREATE TABLE cards ("
            + _ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + CARD_NAME + " varchar(20) NOT NULL, "
            + CARD_NUMBER + " varchar(16) NOT NULL, "
            + BILL_DATE + " smallint unsigned NOT NULL"
            + ")";

    final private static String NAME = TABLE_NAME + "_db";
    final private static Integer VERSION = 1;
    final private Context context;

    public LocalDbOpenHelper(Context context) {
        super(context, NAME, null, VERSION );
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }

    void deleteDb() {
        context.deleteDatabase(NAME);
    }
}
