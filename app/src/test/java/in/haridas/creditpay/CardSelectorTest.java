package in.haridas.creditpay;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import in.haridas.creditpay.activity.CardSelector;

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
    }
}
