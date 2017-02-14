package in.haridas.creditpay.card;

import com.google.firebase.database.Exclude;

import static android.R.attr.id;

/**
 * Created by haridas on 8/4/16.
 */
public class Card implements Comparable {

    /**
     * Id of the card, we won't consider the default value of the id.
     */
    private int index = -1;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Statement date between 1-28.
     */
    private int billingDay = 0;

    // Number of days after statement days
    private int gracePeriod = 0;
    private String name = null;
    private String email = null;

    /**
     * A score attached to this card in the runtime, this will be
     * used to sort the list of card via we pick a card which has
     * good card for today.
     */
    private float score = 0;

    public Card() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Card(String email, String name, int billingDay, int gracePeriod) {
        this.email = email;

        this.name = name;
        this.billingDay = billingDay;
        this.gracePeriod = gracePeriod;
    }

    public Card(int index, String name, int billingDay, int gracePeriod) {
        this.index = index;
        this.name = name;
        this.billingDay = billingDay;
        this.gracePeriod = gracePeriod;
    }

    public int getBillingDay() {
        return billingDay;
    }

    public void setBillingDay(int billingDay) {
        this.billingDay = billingDay;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    /**
     * Helper method for Cursor, to add new card into cursor.
     *
     * @return String[]
     *
     * NOTE: Moved to private scope as it's not serializable in ArrayList mode.
     */
    @Exclude
    private Object[] getDbRow() {
        return  new Object[] {index, name, billingDay, gracePeriod, getScore()};
    }

    @Override
    public int compareTo(Object another) {
        if(((Card)another).getScore() > this.getScore()) {
            return 1;
        } else {
            return -1;
        }
    }
}
