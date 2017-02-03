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

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;

import in.haridas.creditpay.R;
import in.haridas.creditpay.card.Card;
import in.haridas.creditpay.database.FirebaseDbUtil;

/**
 * List the Cards based on its score.
 */
public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private static final int RC_SIGN_IN = 123;
    private FirebaseListAdapter<Card> mAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseLogin();
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
                startActivity(intent);
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        super.onCreateOptionsMenu(menu);
//
////        MenuItem menuItem = menu.findItem(R.id.action_sync);
////        if (firebaseAuth.getCurrentUser() != null) {
////            menuItem.setIcon(R.drawable.ic_action_image_wb_cloudy);
////        } else {
////            menuItem.setIcon(R.drawable.ic_action_file_cloud_off);
////        }
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_sync) {
//            Log.d(TAG, "Selected cloud sync option...");
//            // Check the user is in logged in mode
//        }
//        return super.onOptionsItemSelected(item);
//    }


    private void firebaseLogin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) {
            Log.d(TAG, "User is already signed in");
        } else  {
            Log.d(TAG, "User not signed in.");

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .setLogo(R.mipmap.ic_launcher)
                            .setTheme(R.style.AppTheme)
                            .build(),
                    RC_SIGN_IN
            );
        }
    }

    private void firebaseLogout() {

    }

    private void loadFromFirebase() {
        ListView cardListView = (ListView) findViewById(R.id.card_list_view);
        DatabaseReference ref;
        try {
            ref = FirebaseDbUtil.getFirebaseDbReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        } catch (NullPointerException ex) {
            ref = null;
        }
        mAdaptor = new FirebaseListAdapter<Card>(this, Card.class, R.layout.list_layout, ref) {
            @Override
            protected void populateView(View view, Card cardBean, int position) {
                ((TextView)view.findViewById(R.id.card_name)).setText(cardBean.getName());
                ((TextView)view.findViewById(R.id.billing_day)).setText(String.valueOf(cardBean.getBillingDay()));
                ((TextView)view.findViewById(R.id.grace_period)).setText(String.valueOf(cardBean.getGracePeriod()));

                // Tag each view in the list with unique id, so that we can retrieve this object back from db.
                view.setTag(this.getRef(position).getKey());
            }
        };

        cardListView.setAdapter(mAdaptor);

        // Attach header to the card list view.
        View header = getLayoutInflater().inflate(R.layout.card_list_header, cardListView, false);
        cardListView.addHeaderView(header, null, false);
        cardListView.setHeaderDividersEnabled(false);
    }

//    private void loadCards() {
//        String[] from = new String[]{CardTable.CARD_NAME, CardTable.BILLING_DAY,
//                CardTable.GRACE_PERIOD, CardTable.CARD_SCORE};
//        int[] to = new int[]{R.id.card_name, R.id.billing_day, R.id.grace_period, R.id.card_score};
//
//        // Create adaptor with null cursor, we set the cursor from LoaderManager.
//        getLoaderManager().initLoader(0, null, this);
//        adapter = new SimpleCursorAdapter(this, R.layout.list_layout, null, from, to, 0);
//
//        listView.setAdapter(adapter);
//
//        // Attach header to the card list view.
//        View header = getLayoutInflater().inflate(R.layout.card_list_header, listView, false);
//        listView.addHeaderView(header, null, false);
//        listView.setHeaderDividersEnabled(false);
//    }

//    /**
//     * Creates new loader after init.
//     * @param id
//     * @param args
//     * @return
//     */
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        String[] projections = CardTable.columns;
//        CursorLoader cursorLoader = new CursorLoader(this,
//                CardContentProvider.CONTENT_URI, projections, null, null, null);
//        return cursorLoader;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        // We got fully usable cursor.
//        adapter.swapCursor(data);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        adapter.swapCursor(null);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdaptor.cleanup();
    }
}
