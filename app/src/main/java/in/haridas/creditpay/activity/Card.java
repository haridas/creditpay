package in.haridas.creditpay.activity;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import in.haridas.creditpay.R;
import in.haridas.creditpay.contentprovider.CardContentProvider;
import in.haridas.creditpay.database.CardTable;

public class Card extends AppCompatActivity {

    private static final String TAG = "CardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        EditText cardName = (EditText)findViewById(R.id.card_name);
        EditText billingDay = (EditText)findViewById(R.id.billing_day);
        EditText gracePeriod = (EditText)findViewById(R.id.grace_period);
        View button = findViewById(R.id.update_card_button);

        long contentId = getIntent().getExtras().getLong("id");
        String[] data = this.getDataFromProvider(contentId);
        if (data != null && data.length == 3) {
            cardName.setText(data[0]);
            billingDay.setText(data[1]);
            gracePeriod.setText(data[2]);
        } else {
            Log.e(TAG, "Card data is not correctly retrieved from database");
        }

    }

    private String[] getDataFromProvider(long contentId) {
        String[] data = {};
        Uri cardUri = ContentUris.withAppendedId(CardContentProvider.CONTENT_URI, contentId);

        String[] columns = {
                CardTable.COLUMN_ID,
                CardTable.CARD_NAME,
                CardTable.BILLING_DAY,
                CardTable.GRACE_PERIOD
        };

        Cursor mCursor = getContentResolver().query(cardUri, columns, null, null, null);
        if(mCursor == null) {
            Log.e(TAG, "Got null cursor from database. Uri: " + cardUri);
        } else if (mCursor.getCount() < 1) {
            Log.e(TAG, "The give card is not present in database");

        } else {
            mCursor.moveToFirst();
            data = new String[]{mCursor.getString(1), mCursor.getString(2), mCursor.getString(3)};
        }
        return data;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
