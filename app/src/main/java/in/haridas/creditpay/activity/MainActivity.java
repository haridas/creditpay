package in.haridas.creditpay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private Menu menu = null;

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
        cardListView.setEmptyView(findViewById(R.id.no_card_msg));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logoutFromFirebase();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        try {
            refreshMenuText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

            return true;
        } catch (NullPointerException ex) {
            return false;
        }
    }


    private void logoutFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Going to main activity automatically route to login page.
                        Intent main = new Intent(getApplicationContext(), LoginActivity.class);
                        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mAdaptor.cleanup();
                        startActivity(main);
                        finish();
                    }
                });
    }

    private void refreshMenuText(String username) {
        MenuItem menuItem = this.menu.findItem(R.id.username);
        menuItem.setTitle(username);

        MenuItem versionMenu = this.menu.findItem(R.id.app_version);
        versionMenu.setTitle("App Version: " + BuildConfig.VERSION_NAME);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Cleaning the Adaptor and firebase connections..");
        if (mAdaptor != null) {
            mAdaptor.cleanup();
        }
        super.onDestroy();
    }
}
