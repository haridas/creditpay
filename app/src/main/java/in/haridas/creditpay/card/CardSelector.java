package in.haridas.creditpay.card;

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
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        // This date is generated based on current time.
        Calendar cardBillDate = GregorianCalendar.getInstance();
        float score = 0;


        if (currentDay > card.getStatementDay()) {

            // Use bellow equation
            // score = (nextStatementDate - currentDate; in abs(days) ) + gracePeriod.
            // Get the next statementDate, from the card details and currentDate.
            Calendar nextStatementDate = GregorianCalendar.getInstance();
            nextStatementDate.setTimeInMillis(referenceDate.getTime());
            nextStatementDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH) + 1);
            nextStatementDate.set(Calendar.DATE, card.getStatementDay());
            score = getDiffByDay(nextStatementDate, currentDate) + card.getGracePeriod();
        } else if (currentDay < card.getStatementDay()){
            score = card.getStatementDay() - currentDay + card.getGracePeriod();
        } else {
            // Today is the billing date, so this card will get least score.
            score = 0;
        }

        // Set new score to the this card.
        card.setScore(score);
    }

    public static int getDiffByDay(Calendar date1, Calendar date2) {
        int mode1 = ((GregorianCalendar)date1).isLeapYear(date1.get(Calendar.YEAR)) ? 366 : 365;
        int mode2 = ((GregorianCalendar)date2).isLeapYear(date2.get(Calendar.YEAR)) ? 366 : 365;
        int diff = (date1.get(Calendar.DAY_OF_YEAR) % mode1) - (date2.get(Calendar.DAY_OF_YEAR) % mode2 );
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
