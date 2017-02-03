package in.haridas.creditpay.card;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import in.haridas.creditpay.Constants;
import in.haridas.creditpay.R;
import in.haridas.creditpay.activity.CardView;
import in.haridas.creditpay.activity.NewCardForm;
import in.haridas.creditpay.database.CardTable;
import in.haridas.creditpay.database.FirebaseDbUtil;

/**
 * Created by haridas on 28/1/17.
 */

public class CardUtil {

    private static final String TAG = CardUtil.class.getName();

    //TODO: Use generics, so per activity wise one util class can be defined.
    public static ContentValues getFormDataAndValidate(NewCardForm context) {
        String cardName = ((EditText)context.findViewById(R.id.card_name)).getText().toString();
        String billingDay = ((EditText)context.findViewById(R.id.billing_day)).getText().toString();
        String gracePeriod = ((EditText)context.findViewById(R.id.grace_period)).getText().toString();
        return validateInput(cardName, billingDay, gracePeriod);
    }

    public static ContentValues getFormDataAndValidate(CardView context) {

        String cardName = ((EditText)context.findViewById(R.id.card_name)).getText().toString();
        String billingDay = ((EditText)context.findViewById(R.id.billing_day)).getText().toString();
        String gracePeriod = ((EditText)context.findViewById(R.id.grace_period)).getText().toString();
        return validateInput(cardName, billingDay, gracePeriod);
    }

    private static ContentValues validateInput(String cardName, String billingDay, String gracePeriod) {

        String msg = cardName + " : " + billingDay + " : " + gracePeriod;

        if (gracePeriod.trim().length() == 0) {
            gracePeriod = String.valueOf(Constants.DEFAULT_GRACE_PERIOD);
        }

        ContentValues values = new ContentValues();
        if (cardName.length() > 0 && billingDay.length() > 0) {
            values.put(Constants.CARD_NAME, cardName);
            values.put(Constants.BILLING_DAY, Integer.parseInt(billingDay));
            values.put(Constants.GRACE_PERIOD, gracePeriod);

            Log.i(NewCardForm.class.getName(), "Card holds a valid set of values." + msg);
        } else {
            Log.e(NewCardForm.class.getName(), "Failed to add new card, data is not correct: " + msg);
        }

        return values;
    }

    /**
     * Save / update the card info to DB, take care the user auth related stuff here.
     *
     * This method will be called only after login to the app, so null ptr error won't come.
     *
     * @param cardName Name of the card
     * @param billingDay Statement day.
     * @param gracePeriod Grace period
     * @return boolean
     */
    public static boolean saveToFirebaseDb(String cardName, String billingDay, String gracePeriod) {

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDbUtil.getFirebaseDbReference(uid);

        if (gracePeriod.trim().length() == 0) {
            gracePeriod = String.valueOf(Constants.DEFAULT_GRACE_PERIOD);
        }

        try {
            int bd = Integer.parseInt(billingDay);
            int gp = Integer.parseInt(gracePeriod);
            CardBean cardBean = new CardBean(email, cardName, bd, gp);
            ref.push().setValue(cardBean);
        } catch (NumberFormatException ex) {
            Log.e(TAG, "Error while saving the card, please provide correct data");
            return false;
        }
        return true;
    }
}
