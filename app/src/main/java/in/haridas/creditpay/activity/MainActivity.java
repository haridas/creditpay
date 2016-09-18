package in.haridas.creditpay.activity;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import in.haridas.creditpay.R;
import in.haridas.creditpay.contentprovider.CardContentProvider;
import in.haridas.creditpay.database.CardTable;

/**
 * List the Cards based on its score.
 */
public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
//        setSupportActionBar(toolbar);
        setContentView(R.layout.activity_main);

        loadCards();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newCardForm = new Intent(MainActivity.this, NewCardForm.class);
                startActivity(newCardForm);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadCards() {
        String[] from = new String[]{CardTable.CARD_NAME, CardTable.BILLING_DAY,
                CardTable.GRACE_PERIOD, CardTable.CARD_SCORE};
        int[] to = new int[]{R.id.card_name, R.id.billing_day, R.id.grace_period, R.id.card_score};

        // Create adaptor with null cursor, we set the cursor from LoaderManager.
        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.list_layout, null, from, to, 0);

        setListAdapter(adapter);

        // Attach header to the card list view.
        View header = getLayoutInflater().inflate(R.layout.card_list_header, getListView(), false);
        getListView().addHeaderView(header, null, false);
        getListView().setHeaderDividersEnabled(false);
    }


    /**
     * Creates new loader after init.
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projections = CardTable.columns;
        CursorLoader cursorLoader = new CursorLoader(this,
                CardContentProvider.CONTENT_URI, projections, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // We got fully usable cursor.
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, CardView.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
