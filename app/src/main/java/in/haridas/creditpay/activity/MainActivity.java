package in.haridas.creditpay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import in.haridas.creditpay.R;
import in.haridas.creditpay.adaptors.YcardFirebaseListAdaptor;
import in.haridas.creditpay.card.Card;
import in.haridas.creditpay.database.FirebaseDbUtil;

/**
 * List the Cards based on its score.
 */
public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private YcardFirebaseListAdaptor<Card> mAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFromFirebase();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newCardForm = new Intent(MainActivity.this, NewCardForm.class);
                startActivity(newCardForm);
            }
        });

        final ListView listView = (ListView)findViewById(R.id.card_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object obj = listView.getAdapter().getItem(position);
                Intent intent = new Intent(getApplicationContext(), CardView.class);
                intent.putExtra("key", view.getTag().toString());
                intent.putExtra("email", ((Card) obj).getEmail());
                intent.putExtra("cardName", ((Card) obj).getName());
                intent.putExtra("billingDate", ((Card) obj).getBillingDay());
                intent.putExtra("gracePeriod", ((Card) obj).getGracePeriod());

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadFromFirebase() {
        ListView cardListView = (ListView) findViewById(R.id.card_list_view);
        DatabaseReference ref;
        try {
            ref = FirebaseDbUtil.getFirebaseDbReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } catch (NullPointerException ex) {
            Log.e(TAG, "Error in firebase initialization,  " + String.valueOf(ex));
            return; // No point continue further.
        }
        mAdaptor = new YcardFirebaseListAdaptor<Card>(this, Card.class, R.layout.list_layout, ref) {
            @Override
            protected void populateView(View view, Card card, int position) {
                Log.i(TAG, "Data: " + card.getName() + " " + card.getBillingDay() + " " + card.getGracePeriod());
                ((TextView)view.findViewById(R.id.card_name)).setText(card.getName());
                ((TextView)view.findViewById(R.id.billing_day)).setText(String.valueOf(card.getBillingDay()));
                ((TextView)view.findViewById(R.id.grace_period)).setText(String.valueOf(card.getGracePeriod()));
                ((TextView)view.findViewById(R.id.card_score)).setText(String.valueOf(card.getScore()));

                // Tag each view in the list with unique id, so that we can retrieve this object back from db.
                view.setTag(this.getRef(position).getKey());
            }
        };
        cardListView.setAdapter(mAdaptor);
    }

    @Override
    protected void onDestroy() {
        if (mAdaptor != null) {
            mAdaptor.cleanup();
        }
        super.onDestroy();
    }
}
