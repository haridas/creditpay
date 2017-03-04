package in.haridas.creditpay.database;

/**
 * Created by haridas on 28/1/17.
 */

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.R.attr.key;

/**
 * An singleton pattern to properly initialize the database and its properties.
 * before using the db client.
 */
public class FirebaseDbUtil {

    private static final String FIREBASE_REF_NAME = "cards";
    private static DatabaseReference dbRef = null;

    private static FirebaseDatabase getDbRef() {
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        return fdb;
    }

    public static DatabaseReference getFirebaseDbReference(String userEmail) {
        return FirebaseDatabase.getInstance().getReference(userEmail);
    }

    public static void goOffline() {
        FirebaseDatabase.getInstance().goOffline();
    }
}
