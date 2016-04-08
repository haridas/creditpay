package in.haridas.creditpay;

import android.provider.CalendarContract;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void check_timeDelta() throws Exception {

        Calendar calendar = GregorianCalendar.getInstance();
        //System.out.println("Current time: " + calendar.getTime());
        calendar.add(GregorianCalendar.DATE, 20);
        //System.out.println("Print time +20 day from now: " + calendar.get(Calendar.DAY_OF_MONTH));

        // Updating the Billing date objects to new one.
        calendar.set(Calendar.DAY_OF_MONTH, 4);

        //System.out.println("Changed day of the month: " + calendar.getTime());
;    }

    @Test
    public void test_add_delta_to_current_date() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2016, 1, 28);
        assertEquals("28/02/2016", dateFormat.format(calendar.getTime()));
    }

    @Test
    public void test_credit_card_billing_date() throws Exception {
        String[] billingDates = {"05", "07", "21", "25"};
        Calendar currentDate = GregorianCalendar.getInstance();
        currentDate.set(2016, 1, 28);
    }
}