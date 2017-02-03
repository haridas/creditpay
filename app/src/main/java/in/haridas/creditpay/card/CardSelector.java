package in.haridas.creditpay.card;

import android.database.Cursor;
import android.database.MatrixCursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import in.haridas.creditpay.database.CardTable;

/**
 * Created by haridas on 8/4/16.
 */
public class CardSelector {

    private List<Card> cards = null;

    public CardSelector() {
        cards = new ArrayList<>();
    }
    public CardSelector(List<Card> cardDates) {
        cards = cardDates;
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
    private void setScore(Card card, Date referenceDate) {
        Calendar currentDate = GregorianCalendar.getInstance();
        currentDate.setTimeInMillis(referenceDate.getTime());
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        // This date is generated based on current time.
        Calendar cardBillDate = GregorianCalendar.getInstance();
        float score = 0;


        if (currentDay > card.getBillingDay()) {

            // Use bellow equation
            // score = (nextStatementDate - currentDate; in abs(days) ) + gracePeriod.
            // Get the next statementDate, from the card details and currentDate.
            Calendar nextStatementDate = GregorianCalendar.getInstance();
            nextStatementDate.setTimeInMillis(referenceDate.getTime());
            nextStatementDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH) + 1);
            nextStatementDate.set(Calendar.DATE, card.getBillingDay());
            score = getDiffByDay(nextStatementDate, currentDate) + card.getGracePeriod();
        } else if (currentDay < card.getBillingDay()){
            score = card.getBillingDay() - currentDay + card.getGracePeriod();
        } else {
            // Today is the billing date, so this card will get least score.
            score = 0;
        }

        // Set new score to the this card.
        card.setScore(score);
    }

    /**
     * Find date difference in days if the date are in differ
     * @param date1
     * @param date2
     * @return
     */
    public static int getDiffByDay(Calendar date1, Calendar date2) {
        int mode1 = ((GregorianCalendar)date1).isLeapYear(date1.get(Calendar.YEAR)) ? 366 : 365;
        int mode2 = ((GregorianCalendar)date2).isLeapYear(date2.get(Calendar.YEAR)) ? 366 : 365;

        int year1 = date1.get(Calendar.YEAR);
        int year2 = date2.get(Calendar.YEAR);

        // Find which year is latest one.
        int latestYear = 0;
        int latestYearType = 0;

        int currentYearType = 0;
        int nextYearType = 0;
        Calendar currentYearObj = null;
        Calendar nextYearObj = null;
        if (year1 > year2) {
            nextYearType = mode1;
            nextYearObj = date1;
            currentYearType = mode2;
            currentYearObj = date2;
        } else if(year2 > year1) {
            nextYearObj = date2;
            nextYearType = mode2;
            currentYearObj = date1;
            currentYearType = mode1;
        } else {
            latestYear = year1;
            latestYearType = mode1;
            nextYearObj = date1;
            currentYearObj = date2;
        }

        int diff = 0;

        if (year1 - year2 == 0) {
            // Difference is done on the same year range.
            diff = (date1.get(Calendar.DAY_OF_YEAR) % mode1) - (date2.get(Calendar.DAY_OF_YEAR) % mode2 );
        } else {
            // The dates are in adjecent years.
            int currentYearRemaining = currentYearType - currentYearObj.get(Calendar.DAY_OF_YEAR);
            int nextYearDays = nextYearObj.get(Calendar.DAY_OF_YEAR);
            diff = currentYearRemaining + nextYearDays;
        }
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

    public List<Card> getSortedCards() {
        setCardScores(new Date());
        // Sort the card based on score, use selection sort.
        Collections.sort(cards);
        return cards;
    }

//    /**
//     * Get Processed result as MatrixCursor.
//     *
//     * Will sort the cards prepare a cursor for UI elements.
//     */
//    public MatrixCursor getSortedCursor() {
//
//        List<Card> sortedCards = getSortedCards();
//        int numCards = sortedCards.size();
//        String[] columns = new String[]{CardTable.COLUMN_ID, CardTable.CARD_NAME,
//                CardTable.BILLING_DAY, CardTable.GRACE_PERIOD, CardTable.CARD_SCORE };
//        MatrixCursor cursor = new MatrixCursor(columns, numCards);
//        for (int i=0; i < numCards; i++) {
//            Card card = sortedCards.get(i);
//            cursor.addRow(card.getDbRow());
//        }
//        return cursor;
//    }

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
