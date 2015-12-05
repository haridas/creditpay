package in.haridas.creditpay.activity;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

import in.haridas.creditpay.R;
import in.haridas.creditpay.store.LocalDbOpenHelper;

public class NewCardForm extends AppCompatActivity {

    LocalDbOpenHelper localDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_card_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        localDbOpenHelper = new LocalDbOpenHelper(this);

        // Add button listener.
        Button addNewCardButton = (Button)findViewById(R.id.add_new_card);
        addNewCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cardName = ((EditText)findViewById(R.id.card_name)).getText().toString();
                String cardNumber = ((EditText)findViewById(R.id.card_number)).getText().toString();
                String billDate = ((EditText)findViewById(R.id.billing_date)).getText().toString();

                String msg = cardName + " : " + cardNumber + " : " + billDate;

                // Add the record into database.

                if (cardNumber.length() == 16 && billDate.length() == 2) {
                    ContentValues values = new ContentValues();
                    values.put(LocalDbOpenHelper.CARD_NAME, cardName);
                    values.put(LocalDbOpenHelper.CARD_NUMBER, cardNumber);
                    values.put(LocalDbOpenHelper.BILL_DATE, billDate);

                    localDbOpenHelper.getWritableDatabase().insert(LocalDbOpenHelper.TABLE_NAME,
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
