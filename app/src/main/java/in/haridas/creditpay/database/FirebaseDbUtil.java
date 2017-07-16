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
    private static FirebaseDatabase dbRef = null;

    private static FirebaseDatabase getDbRef(String email) {

        if (dbRef == null) {
            FirebaseDatabase fdb = FirebaseDatabase.getInstance();
            fdb.setPersistenceEnabled(true);
            fdb.getReference(email).keepSynced(true);
            dbRef = fdb;
        }
        return dbRef;
    }

    public static DatabaseReference getFirebaseDbReference(String userEmail) {
        return getDbRef(userEmail).getReference(userEmail);
    }

    public static void goOffline() {
        FirebaseDatabase.getInstance().goOffline();
    }
}
