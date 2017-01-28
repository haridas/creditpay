package in.haridas.creditpay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import in.haridas.creditpay.R;
import in.haridas.creditpay.card.CardBean;

/**
 * List the Cards based on its score.
 */
public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private static final int RC_SIGN_IN = 123;
    private FirebaseListAdapter<CardBean> mAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);

        // For rendering improvements.
        getWindow().setBackgroundDrawable(null);
//        getWindow().setBackgroundDrawableResource(R.mipmap.ic_launcher);

        setContentView(R.layout.activity_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
//        setSupportActionBar(toolbar);
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

        ListView listView = (ListView)findViewById(R.id.card_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), CardView.class);
                intent.putExtra("id", id);
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
        FirebaseDatabase fDb = FirebaseDatabase.getInstance();
        fDb.setPersistenceEnabled(true);
        DatabaseReference ref = fDb.getReference();
        mAdaptor = new FirebaseListAdapter<CardBean>(this, CardBean.class, R.layout.list_layout, ref) {
            @Override
            protected void populateView(View view, CardBean cardBean, int position) {
                ((TextView)view.findViewById(R.id.card_name)).setText(cardBean.getCardName());
                ((TextView)view.findViewById(R.id.billing_day)).setText(String.valueOf(cardBean.getBillingDate()));
                ((TextView)view.findViewById(R.id.grace_period)).setText(String.valueOf(cardBean.getGracePeriod()));
                ((TextView)view.findViewById(R.id.card_score)).setText(String.valueOf(cardBean.getId()));
            }
        };

        cardListView.setAdapter(mAdaptor);
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
