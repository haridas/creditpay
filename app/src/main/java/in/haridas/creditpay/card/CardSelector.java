package in.haridas.creditpay.card;

import android.database.Cursor;
import android.database.MatrixCursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import in.haridas.creditpay.database.CardTable;

/**
 * Created by haridas on 8/4/16.
 */
public class CardSelector {

    ArrayList<Card> cards = null;

    public CardSelector() {
        cards = new ArrayList<>();
    }
    public CardSelector(List<Card> cardDates) {
        this.cards = cards;
    }

    /**
     * Initialize the CardSelector from cursor.
     * @param cursor
     */
    public CardSelector(Cursor cursor) {
        cards = new ArrayList<>();
        populateCardListFromCursor(cursor);
    }

    private void populateCardListFromCursor(Cursor cursor) {
        int cardLen = cursor.getCount();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            int _id = cursor.getInt(0);
            String name = cursor.getString(1);
            int billDay = cursor.getInt(2);
            int gracePeriod = cursor.getInt(3);

            Card card = new Card(_id, name, billDay, gracePeriod);
            cards.add(card);

            cursor.moveToNext();
        }
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

    public ArrayList<Card> getSortedCards() {
        setCardScores(new Date());

        // Sort the card based on score, use selection sort.

        return cards;
    }

    /**
     * Get Processed result as MatrixCursor.
     *
     * Will sort the cards prepare a cursor for UI elements.
     */
    public MatrixCursor getSortedCursor() {

        ArrayList<Card> sortedCards = getSortedCards();
        int numCards = sortedCards.size();
        MatrixCursor cursor = new MatrixCursor(CardTable.columns, numCards);
        for (int i=0; i < numCards; i++) {
            Card card = sortedCards.get(i);
            cursor.addRow(card.getDbRow());
        }
        return cursor;
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
