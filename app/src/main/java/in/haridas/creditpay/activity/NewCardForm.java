package in.haridas.creditpay.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.haridas.creditpay.Constants;
import in.haridas.creditpay.R;
import in.haridas.creditpay.card.Card;
import in.haridas.creditpay.card.CardBean;
import in.haridas.creditpay.contentprovider.CardContentProvider;
import in.haridas.creditpay.database.CardTable;
import in.haridas.creditpay.database.FirebaseDbUtil;

public class NewCardForm extends AppCompatActivity {

    private EditText cardNameBox;
    private EditText billingDayBox;
    private EditText gracePeriodBox;

    private CardTable cardTable;
    private Uri cardUri;

    private static final String TAG = NewCardForm.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_new_card_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_card_form);
        setSupportActionBar(toolbar);

        // Check if it's from saved instance.
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            cardUri = (bundle == null) ? null : (Uri) bundle.getParcelable(
                    CardContentProvider.CONTENT_ITEM_TYPE);
            fillData(cardUri);
        }

        // Add button listener.
        Button addNewCardButton = (Button)findViewById(R.id.add_new_card);

        addNewCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                setResult(RESULT_OK);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.saveUIState();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        saveUIState();
        bundle.putParcelable(CardContentProvider.CONTENT_ITEM_TYPE, cardUri);
    }

    private void fillData(Uri cardUri) {

        String[] projection = {CardTable.CARD_NAME, CardTable.BILLING_DAY, CardTable.GRACE_PERIOD};
        Cursor cursor = getContentResolver().query(cardUri, projection, null, null, null);

        if(cursor != null) {
            cursor.moveToFirst();
            String cardName = cursor.getString(cursor.getColumnIndexOrThrow(CardTable.CARD_NAME));
            String billingDay = cursor.getString(cursor.getColumnIndexOrThrow(CardTable.BILLING_DAY));
            String gracePeriod = cursor.getString(cursor.getColumnIndexOrThrow(CardTable.GRACE_PERIOD));

            cardNameBox.setText(cardName);
            billingDayBox.setText(billingDay);
            gracePeriodBox.setText(gracePeriod);

            // Close the cursor.
            cursor.close();
        }
    }

    private void saveUIState() {
        String cardName = ((EditText)findViewById(R.id.card_name)).getText().toString();
        String billingDay = ((EditText)findViewById(R.id.billing_day)).getText().toString();
        String gracePeriod = ((EditText)findViewById(R.id.grace_period)).getText().toString();

        String msg = cardName + " : " + billingDay + " : " + gracePeriod;

        // providing default Graceperiod.
        if (gracePeriod.trim().length() == 0) {
            gracePeriod = String.valueOf(Constants.DEFAULT_GRACE_PERIOD);
        }
        // Add the record into database.
        if (cardName.length() > 0 && billingDay.length() > 0) {
            saveToFirebaseDb(cardName, billingDay, gracePeriod);
            Log.i(NewCardForm.class.getName(), "New card added..." + msg);
            startActivity(new Intent(NewCardForm.this, MainActivity.class));
        } else {
            Log.e(NewCardForm.class.getName(), "Failed to add new card, data is not correct: " + msg);
        }
    }

    private void saveToLocalDB(String cardName, String billingDay, String gracePeriod) {
        ContentValues values = new ContentValues();
        values.put(CardTable.CARD_NAME, cardName);
        values.put(CardTable.BILLING_DAY, billingDay);
        values.put(CardTable.GRACE_PERIOD, gracePeriod);
        getContentResolver().insert(CardContentProvider.CONTENT_URI, values);
    }

    private void saveToFirebaseDb(String cardName, String billingDay, String gracePeriod) {
        DatabaseReference ref = FirebaseDbUtil.getFirebaseDbReference();

        try {
            int bd = Integer.parseInt(billingDay);
            int gp = Integer.parseInt(gracePeriod);
            CardBean cardBean = new CardBean(cardName, bd, gp);
            ref.push().setValue(cardBean);
        } catch (NumberFormatException ex) {
            Log.e(TAG, "Error while saving the card, please provide correct data");
            Toast.makeText(this, "Failed to add new card, please check input.", Toast.LENGTH_SHORT).show();
        }

    }
}
