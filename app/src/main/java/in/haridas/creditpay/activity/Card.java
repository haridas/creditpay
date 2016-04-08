package in.haridas.creditpay.activity;

/**
 * Created by haridas on 8/4/16.
 */
public class Card {
    /**
     * Billing date between 1-28.
     */
    int billingDate = 0;
    int gracePeriod = 0;
    String name = null;

    /**
     * A score attached to this card in the runtime, this will be
     * used to sort the list of card via we pick a card which has
     * good card for today.
     */
    float score = 0;

    public Card() {
    }

    public Card(String name, int billingDate, int gracePeriod) {
        this.name = name;
        this.billingDate = billingDate;
        this.gracePeriod = gracePeriod;
    }

    public int getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(int billingDate) {
        this.billingDate = billingDate;
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
}
