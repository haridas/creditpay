package in.haridas.creditpay.card;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;

import in.haridas.creditpay.R;
import in.haridas.creditpay.activity.CardView;
import in.haridas.creditpay.activity.NewCardForm;
import in.haridas.creditpay.contentprovider.CardContentProvider;
import in.haridas.creditpay.database.CardTable;

/**
 * Created by haridas on 28/1/17.
 */

public class CardUtil {

    private static final String TAG = CardUtil.class.getName();

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
