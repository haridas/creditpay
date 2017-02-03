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

import in.haridas.creditpay.Constants;
import in.haridas.creditpay.R;
import in.haridas.creditpay.card.CardUtil;
import in.haridas.creditpay.contentprovider.CardContentProvider;
import in.haridas.creditpay.database.CardTable;

public class NewCardForm extends AppCompatActivity {

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
        ContentValues values = CardUtil.getFormDataAndValidate(this);
        String cardName = values.get(Constants.CARD_NAME).toString();
        String billingDay = values.get(Constants.BILLING_DAY).toString();
        String gracePeriod = values.get(Constants.GRACE_PERIOD).toString();

        String msg = cardName + " : " + billingDay + " : " + gracePeriod;

        boolean status = CardUtil.saveToFirebaseDb(cardName, billingDay, gracePeriod);
        if (status) {
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
}
