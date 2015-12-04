package in.haridas.creditpay;

import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewCardForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_card_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add button listener.
        Button addNewCardButton = (Button)findViewById(R.id.add_new_card);
        addNewCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText cardName = (EditText)findViewById(R.id.card_name);
                EditText cardNumber = (EditText)findViewById(R.id.card_number);
                EditText billDate = (EditText)findViewById(R.id.billing_date);

                String msg = cardName.getText() + " : " + cardNumber.getText() + " : " + billDate.getText();
                Snackbar.make(view, "Added new card ->" + msg, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
