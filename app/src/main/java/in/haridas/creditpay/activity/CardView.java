package in.haridas.creditpay.activity;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import in.haridas.creditpay.R;
import in.haridas.creditpay.card.CardBean;
import in.haridas.creditpay.contentprovider.CardContentProvider;
import in.haridas.creditpay.database.CardTable;

public class CardView extends AppCompatActivity {

    private static final String TAG = "CardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_card);
        setSupportActionBar(toolbar);

        EditText cardName = (EditText)findViewById(R.id.card_name);
        EditText billingDay = (EditText)findViewById(R.id.billing_day);
        EditText gracePeriod = (EditText)findViewById(R.id.grace_period);

        final long contentId = getIntent().getExtras().getLong("id");
        String[] data = this.getDataFromProvider(contentId);
        if (data != null && data.length == 3) {
            cardName.setText(data[0]);
            billingDay.setText(data[1]);
            gracePeriod.setText(data[2]);
        } else {
            Log.e(TAG, "Card data is not correctly retrieved from database");
        }
    }


    /**
     * Get the card details from the cloud storage for editing.
     *
     * @param cardId
     * @return
     */
    private CardBean getCardfromCloud(long cardId) {
        return null;
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
            mCursor.close();

        } else {
            mCursor.moveToFirst();
            data = new String[]{mCursor.getString(1), mCursor.getString(2), mCursor.getString(3)};
            mCursor.close();
        }
        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.card_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        long contentId;
        Uri contentUri;
        switch (id) {
            case R.id.action_delete_card:
                contentId = getIntent().getExtras().getLong("id");
                contentUri = ContentUris.withAppendedId(CardContentProvider.CONTENT_URI, contentId);
                Log.i(TAG, String.valueOf(contentId));
                getContentResolver().delete(contentUri, null, null);
                Toast.makeText(this, "Removed the Card.", Toast.LENGTH_SHORT).show();

                // Go to the main activity after removing current card.
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                break;
            case R.id.action_update_card:
                contentId = getIntent().getExtras().getLong("id");
                contentUri = ContentUris.withAppendedId(CardContentProvider.CONTENT_URI, contentId);
                ContentValues values = CardView.getFormDataAndValidate(this);
                getContentResolver().update(contentUri, values, null, null);
                Log.i(TAG, String.valueOf(contentId));
                Toast.makeText(this, "Card updated.", Toast.LENGTH_SHORT).show();

                // Go to the main activity after removing current card.
                mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static ContentValues getFormDataAndValidate(CardView context) {
        String cardName = ((EditText)context.findViewById(R.id.card_name)).getText().toString();
        String billingDay = ((EditText)context.findViewById(R.id.billing_day)).getText().toString();
        String gracePeriod = ((EditText)context.findViewById(R.id.grace_period)).getText().toString();

        String msg = cardName + " : " + billingDay + " : " + gracePeriod;

        // Add the record into database.

        ContentValues values = new ContentValues();
        if (cardName.length() > 0 && billingDay.length() > 0) {
            values.put(CardTable.CARD_NAME, cardName);
            values.put(CardTable.BILLING_DAY, billingDay);
            values.put(CardTable.GRACE_PERIOD, gracePeriod);

            Log.i(NewCardForm.class.getName(), "New card added..." + msg);
        } else {
            Log.e(NewCardForm.class.getName(), "Failed to add new card, data is not correct: " + msg);
        }

        return values;
    }
}
