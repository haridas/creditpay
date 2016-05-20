package in.haridas.creditpay.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

import in.haridas.creditpay.R;
import in.haridas.creditpay.store.LocalDbOpenHelper;

public class MainActivity extends ListActivity {

    private LocalDbOpenHelper localDbOpenHelper;
    private SimpleCursorAdapter dbCursorAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        localDbOpenHelper = new LocalDbOpenHelper(this);


        Cursor cursor = readCards();
        dbCursorAdaptor = new SimpleCursorAdapter(this, R.layout.list_layout,
                cursor, LocalDbOpenHelper.columns,
                new int[] {R.id._id, R.id.card_name, R.id.billing_day, R.id.grace_period});

        setListAdapter(dbCursorAdaptor);

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

    private Cursor readCards() {
        return localDbOpenHelper.getReadableDatabase().query(LocalDbOpenHelper.TABLE_NAME,
                LocalDbOpenHelper.columns, null, new String[]{}, null, null, null);
    }
}
