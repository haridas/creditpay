package in.haridas.creditpay.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by haridas on 8/4/16.
 */
public class CardSelector {

    List<Card> cards = null;
    public CardSelector(List<Card> cardDates) {
        this.cards = cards;
    }

    private void setScore(Card card) {
        Date now = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(now.getTime());
        int dayOfMonth = calendar.get(calendar.MONTH);

        // This date is generated based on current time.
        Calendar cardBillDate = GregorianCalendar.getInstance();
        float score = 0;


        if (dayOfMonth > card.getBillingDate()) {
            // This means, next billing cycle will be on next month.
            cardBillDate.set(calendar.YEAR, calendar.get(calendar.YEAR));
            cardBillDate.set(calendar.MONTH, calendar.get(calendar.MONTH) + 1);
            cardBillDate.set(calendar.DATE, card.getBillingDate() + card.getGracePeriod());
            score = getDaydiff(calendar, cardBillDate);
        } else if (dayOfMonth < card.getBillingDate()){
            score = card.getBillingDate() - dayOfMonth;
        } else {
            // Today is the billing date, so this card will get least score.
            score = 0;
        }

        // Set new score to the this card.
        card.setScore(score);
    }

    public static int getDaydiff(Calendar now, Calendar cardBillDate) {
        int diff = now.get(Calendar.DAY_OF_YEAR) - cardBillDate.get(Calendar.DAY_OF_YEAR);
        return Math.abs(diff);
    }

    /**
     * Set the card score.
     *
     * @param cards
     */
    private void setCardScores(List<Card> cards) {
        for(Card card : cards) {
            setScore(card);
        }
    }

    /**
     * Pick the best card that can be used today based on the
     * score.
     *
     * @return Card
     */
    public Card getCard() {
        return null;
    }
}
