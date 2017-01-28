package in.haridas.creditpay.database;

/**
 * Created by haridas on 28/1/17.
 */

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * An singleton pattern to properly initialize the database and its properties.
 * before using the db client.
 */
public class FirebaseDbUtil {
    private static DatabaseReference dbRef = null;

    public static DatabaseReference getFirebaseDbReference() {
        if (dbRef == null) {
            FirebaseDatabase fdb = FirebaseDatabase.getInstance();
            fdb.setPersistenceEnabled(true);
            dbRef = fdb.getReference();
        }
        return dbRef;
    }
}
