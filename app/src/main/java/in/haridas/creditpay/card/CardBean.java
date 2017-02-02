package in.haridas.creditpay.card;

/**
 * Created by haridas on 16/1/17.
 */

public class CardBean {
    private int billingDate;
    private int gracePeriod;
    private String cardName;
    private String userEmail;

    public CardBean() {

    }

    public CardBean(String email, String name, int bDate, int gPeriod) {
        userEmail = email;
        cardName = name;
        billingDate = bDate;
        gracePeriod = gPeriod;
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

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getEmail() {
        return userEmail;
    }

    public void setEmail(String email) {
        this.userEmail = email;
    }
}