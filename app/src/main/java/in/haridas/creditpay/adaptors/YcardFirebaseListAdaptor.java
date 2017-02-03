package in.haridas.creditpay.adaptors;

import android.app.Activity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import in.haridas.creditpay.card.Card;

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
public abstract class YcardFirebaseListAdaptor<T> extends FirebaseListAdapter<T> {


    // This holds the sorted list values.
    private ArrayList<Card> cardBeanList = null;
    private List<Card> cardList = null;

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
        super(activity, modelClass, modelLayout, ref);
    }

    public void sortCards() {

    }



}
