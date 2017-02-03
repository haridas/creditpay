package in.haridas.creditpay.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import in.haridas.creditpay.Constants;
import in.haridas.creditpay.R;
import in.haridas.creditpay.card.Card;
import in.haridas.creditpay.card.CardUtil;
import in.haridas.creditpay.contentprovider.CardContentProvider;
import in.haridas.creditpay.database.CardTable;
import in.haridas.creditpay.database.FirebaseDbUtil;

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

        String rowKey = getIntent().getExtras().getString("key");
        String rCardName = getIntent().getExtras().getString("cardName");
        int rBillingDate = getIntent().getExtras().getInt("billingDate");
        int rGracePeriod = getIntent().getExtras().getInt("gracePeriod");
        String rEmail = getIntent().getExtras().getString("email");

        cardName.setText(rCardName);
        billingDay.setText(String.valueOf(rBillingDate));
        gracePeriod.setText(String.valueOf(rGracePeriod));
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
        String userUid;
        DatabaseReference ref;
        String rowKey;

        try {
            rowKey = getIntent().getExtras().getString("key");
            userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ref = FirebaseDbUtil.getFirebaseDbReference(userUid);
        } catch (NullPointerException ex) {
            rowKey = "";
            userUid = "";
            ref = null;
        }

        switch (id) {
            case R.id.action_delete_card:
                ref.getRef().child(rowKey).removeValue();
                Toast.makeText(this, "Removed the Card.", Toast.LENGTH_SHORT).show();

                // Go to the main activity after removing current card.
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                break;
            case R.id.action_update_card:

                ContentValues values = CardUtil.getFormDataAndValidate(this);
                String cardName = values.get(Constants.CARD_NAME).toString();
                int billingDay = values.getAsInteger(Constants.BILLING_DAY);
                int gracePeriod = values.getAsInteger(Constants.GRACE_PERIOD);
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                Card cardBean = new Card(
                        email,
                        cardName,
                        billingDay,
                        gracePeriod);

                ref.getRef().child(rowKey).setValue(cardBean);
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

}
