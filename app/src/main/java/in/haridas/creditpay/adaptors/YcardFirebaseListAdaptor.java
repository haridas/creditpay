package in.haridas.creditpay.adaptors;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import in.haridas.creditpay.R;
import in.haridas.creditpay.card.Card;
import in.haridas.creditpay.card.CardSelector;

/**
 * Created by haridas on 29/1/17.
 */


/**
 * Custom adaptor for this application.
 *
 * As our usecase is dynamic in nature, we need to reorder the list post fetching it.
 * The list elements need to be sorted based on the current time, so we can't statically keep the
 * order info in db, have to compute on demand.
 *
 * We need to fetch all content, and then sort and display with the list view. Our case the number
 * of elements are very less, so this won't cause problem. If this is not the case then the logic
 * need to be moved to backend server side.
 *
 * @param <T> ModelClass
 */
public abstract class YcardFirebaseListAdaptor<T> extends BaseAdapter {

    private final String TAG = YcardFirebaseListAdaptor.class.getName();
    // This holds the sorted list values.
    protected List<Card> cardList = null;


    private FirebaseArray mSnapshots;
    private final Class<T> mModelClass;
    protected Activity mActivity;
    protected int mLayout;

    YcardFirebaseListAdaptor(Activity activity,
                        Class<T> modelClass,
                        @LayoutRes int modelLayout,
                        FirebaseArray snapshots) {
        mActivity = activity;
        mModelClass = modelClass;
        mLayout = modelLayout;
        mSnapshots = snapshots;

        mSnapshots.setOnChangedListener(new FirebaseArray.OnChangedListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {
                Log.i(TAG, ">>> EVENT: " + type.toString() + " " + " Index: " + index + " Old Index: " + oldIndex);
                YcardFirebaseListAdaptor.this.cardList.clear();
                YcardFirebaseListAdaptor.this.sortCards();
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                YcardFirebaseListAdaptor.this.onCancelled(databaseError);
            }

            @Override
            public void onDataChanged() {
                YcardFirebaseListAdaptor.this.onDataChanged();
            }
        });
    }

    /**
     * @param activity    The activity containing the ListView
     * @param modelClass  Firebase will marshall the data at a location into
     *                    an instance of a class that you provide
     * @param modelLayout This is the layout used to represent a single list item.
     *                    You will be responsible for populating an instance of the corresponding
     *                    view with the data from an instance of modelClass.
     * @param ref         The Firebase location to watch for data changes. Can also be a slice of a location,
     *                    using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public YcardFirebaseListAdaptor(Activity activity, Class<T> modelClass, int modelLayout, Query ref) {
        this(activity, modelClass, modelLayout, new FirebaseArray(ref));
        cardList = new ArrayList<>();
    }

    private void sortCards() {
        if (getCount() > 0) {
            for (int i = 1; i <= getCount(); i++) {
                Card card = (Card)getItem(i - 1);
                card.setIndex(i - 1);
                cardList.add(card);
            }
            CardSelector selector = new CardSelector(cardList);
            selector.setCardScores(GregorianCalendar.getInstance().getTime());
            cardList = selector.getSortedCards();

            Log.i(TAG, "Cards resorted...");

            // Updated the snapshot list.
            List<DataSnapshot> snapshots = new ArrayList<>();
            List<DataSnapshot> currSnapshot = mSnapshots.getmSnapshots();
            for (Card c: cardList) {
                // Keeping the original index info after sorting, reconstructing the mSnapshots list.
                snapshots.add(currSnapshot.get(c.getIndex()));
            }
            // Override the sorted snapshots back.
            mSnapshots.setmSnapshots(snapshots);
        }
    }



    public void cleanup() {
        mSnapshots.cleanup();
    }

    @Override
    public int getCount() {
        return mSnapshots.getCount();
    }

    @Override
    public T getItem(int position) {
        return parseSnapshot(mSnapshots.getItem(position));
    }

    /**
     * This method parses the DataSnapshot into the requested type. You can override it in subclasses
     * to do custom parsing.
     *
     * @param snapshot the DataSnapshot to extract the model from
     * @return the model extracted from the DataSnapshot
     */
    protected T parseSnapshot(DataSnapshot snapshot) {
        return snapshot.getValue(mModelClass);
    }

    public DatabaseReference getRef(int position) {
        return mSnapshots.getItem(position).getRef();
    }

    @Override
    public long getItemId(int i) {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return mSnapshots.getItem(i).getKey().hashCode();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Log.i(TAG, ">> getView called with: " + position);
        if (view == null) {
            view = mActivity.getLayoutInflater().inflate(mLayout, viewGroup, false);
        }

//        T model = getItem(position);
        Card model = cardList.get(position);

        // Call out to subclass to marshall this model into the provided view
        populateView(view, model, position);
        return view;
    }

    /**
     * This method will be triggered each time updates from the database have been completely processed.
     * So the first time this method is called, the initial data has been loaded - including the case
     * when no data at all is available. Each next time the method is called, a complete update (potentially
     * consisting of updates to multiple child items) has been completed.
     * <p>
     * You would typically override this method to hide a loading indicator (after the initial load) or
     * to complete a batch update to a UI element.
     */
    protected void onDataChanged() {
        Log.i(TAG, ">> onDatachanged");
    }

    /**
     * This method will be triggered in the event that this listener either failed at the server,
     * or is removed as a result of the security and Firebase Database rules.
     *
     * @param error A description of the error that occurred
     */
    protected void onCancelled(DatabaseError error) {
        Log.w(TAG, error.toException());
    }

    /**
     * Each time the data at the given Firebase location changes,
     * this method will be called for each item that needs to be displayed.
     * The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     * <p>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param v        The view to populate
     * @param model    The object containing the data used to populate the view
     * @param position The position in the list of the view being populated
     */
    protected abstract void populateView(View v, Card model, int position);
}
