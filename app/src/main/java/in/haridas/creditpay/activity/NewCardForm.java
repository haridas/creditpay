package in.haridas.creditpay.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import in.haridas.creditpay.R;
import in.haridas.creditpay.database.CardTable;

public class NewCardForm extends AppCompatActivity {

    CardTable CardTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_card_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CardTable = new CardTable(this);

        // Add button listener.
        Button addNewCardButton = (Button)findViewById(R.id.add_new_card);


        addNewCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cardName = ((EditText)findViewById(R.id.card_name)).getText().toString();
                String billingDay = ((EditText)findViewById(R.id.billing_day)).getText().toString();
                String gracePeriod = ((EditText)findViewById(R.id.grace_period)).getText().toString();

                String msg = cardName + " : " + billingDay + " : " + gracePeriod;

                // Add the record into database.

                if (cardName.length() > 0 && billingDay.length() > 0) {
                    ContentValues values = new ContentValues();
                    values.put(CardTable.CARD_NAME, cardName);
                    values.put(CardTable.BILLING_DAY, billingDay);
                    values.put(CardTable.GRACE_PERIOD, gracePeriod);

                    CardTable.getWritableDatabase().insert(CardTable.TABLE_NAME,
                            null, values);

                    Snackbar.make(view, "Added new card ->" + msg, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    startActivity(new Intent(NewCardForm.this, MainActivity.class));
                } else {
                    Snackbar.make(view, "Wrong input, please check it", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    /**
     * Ensure the db table exists or create it.
     */
    private void ensureDbExists() {

    }



}
