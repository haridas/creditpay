package in.haridas.creditpay;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import in.haridas.creditpay.activity.Card;
import in.haridas.creditpay.activity.CardSelector;
import static org.junit.Assert.*;

/**
 * Created by haridas on 8/4/16.
 */
public class CardSelectorTest {

    @Test
    public void test_date_diff_for_day() throws Exception {
        Calendar now = GregorianCalendar.getInstance();
        Calendar next = GregorianCalendar.getInstance();

        // On same month
        now.set(2016, 1, 23);
        next.set(2016, 1, 28);
        Assert.assertEquals(5, CardSelector.getDaydiff(now, next));

        now.set(2016, 0, 20);
        next.set(2016, 1, 29);
        Assert.assertEquals(40, CardSelector.getDaydiff(now, next));

        // With different time.
        now.set(2016, 0, 20, 23, 22, 33);
        next.set(2016, 1, 29, 23, 59, 59);
        Assert.assertEquals(40, CardSelector.getDaydiff(now, next));
    }

    @Test
    public void test_set_card_score() throws Exception {

        // Cards.
        Card cityCard = new Card("CITY", 21, 20);
        Card hdfcCard = new Card("HDFC", 5, 20);
        Card sbiCard = new Card("SBI", 7, 20);

        // Card Selector.
        CardSelector selector = new CardSelector();
        selector.addCard(hdfcCard);
        selector.addCard(cityCard);
        selector.addCard(sbiCard);

        // current date on Feb 4'th
        Calendar reference = new GregorianCalendar(2016, 1, 4);
        selector.setCardScores(reference.getTime());
        assertEquals(37, cityCard.getScore(), 0);
        assertEquals(21, hdfcCard.getScore(), 0);
        assertEquals(23, sbiCard.getScore(), 0);

        // current date on March 15'th
        reference = new GregorianCalendar(2016, 2, 15);
        selector.setCardScores(reference.getTime());
        assertEquals(26, cityCard.getScore(), 0);
        assertEquals(41, hdfcCard.getScore(), 0);
        assertEquals(43, sbiCard.getScore(), 0);

    }
}
