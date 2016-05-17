package in.haridas.creditpay.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by haridas on 8/4/16.
 */
public class CardSelector {

    List<Card> cards = null;

    public CardSelector() {
        cards = new ArrayList<>();
    }
    public CardSelector(List<Card> cardDates) {
        this.cards = cards;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Set the card score based on the given referenceDate.
     *
     *
     * @param card Instance of Card.
     * @param referenceDate Reference date, eg; TODAY.
     */
    public void setScore(Card card, Date referenceDate) {
        Calendar currentDate = GregorianCalendar.getInstance();
        currentDate.setTimeInMillis(referenceDate.getTime());
        int currentDay = currentDate.get(currentDate.DAY_OF_MONTH);

        // This date is generated based on current time.
        Calendar cardBillDate = GregorianCalendar.getInstance();
        float score = 0;


        if (currentDay > card.getStatementDay()) {

            // Use bellow equation
            // score = (nextStatementDate - currentDate; in abs(days) ) + gracePeriod.
            // Get the next statementDate, from the card details and currentDate.
            Calendar nextStatementDate = GregorianCalendar.getInstance();
            nextStatementDate.set(currentDate.YEAR, currentDate.get(currentDate.YEAR));
            nextStatementDate.set(currentDate.MONTH, currentDate.get(currentDate.MONTH) + 1);
            nextStatementDate.set(currentDate.DATE, card.getStatementDay());
            score = getDaydiff(nextStatementDate, currentDate) + card.getGracePeriod();
        } else if (currentDay < card.getStatementDay()){
            score = card.getStatementDay() - currentDay + card.getGracePeriod();
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
     * Set the score for all cards, based on the given reference date.
     *
     */
    public void setCardScores(Date referenceDate) {
        for(Card card : cards) {
            setScore(card, referenceDate);
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
