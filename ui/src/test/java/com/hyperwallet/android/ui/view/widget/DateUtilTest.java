package com.hyperwallet.android.ui.view.widget;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

@RunWith(RobolectricTestRunner.class)
public class DateUtilTest {
    private final DateUtil mDateUtil = new DateUtil();

    @Rule
    public final ExpectedException mThrown = ExpectedException.none();

    @Test
    public void testConvertDateFromServerToWidgetFormat() throws Exception {
        String serverDate = "2005-05-23";
        String widgetDate = "23 May 2005";
        assertThat(mDateUtil.convertDateFromServerToWidgetFormat(serverDate), is(widgetDate));
    }

    @Test
    public void testBuildParamsDateFromServerToWidget_whenIncorrectDate() throws Exception {
        mThrown.expect(ParseException.class);
        mDateUtil.convertDateFromServerToWidgetFormat("1990-01");
    }

    @Test
    public void testConvertDateFromServerToWidgetFormat_whenDateIsNullOrEmpty() throws Exception {
        assertThat(mDateUtil.convertDateFromServerToWidgetFormat(""), is(""));
        assertThat(mDateUtil.convertDateFromServerToWidgetFormat(null), is(""));
    }

    @Test
    public void testBuildParamsDateFromServerToCalendar_whenIncorrectDate() throws Exception {
        mThrown.expect(ParseException.class);
        mDateUtil.convertDateFromServerFormatToCalendar("123-32").getTime();
    }

    @Test
    public void testConvertDateFromServerFormatToCalendar_whenDateIsNullOrEmpty() throws ParseException {
        assertThat(mDateUtil.convertDateFromServerFormatToCalendar(null).getTime().toString(),
                is(Calendar.getInstance().getTime().toString()));
        assertThat(mDateUtil.convertDateFromServerFormatToCalendar("").getTime().toString(),
                is(Calendar.getInstance().getTime().toString()));
    }

    @Test
    public void testConvertDateFromServerFormatToCalendar() throws ParseException {
        String serverDate = "2005-05-23";
        final Calendar mayCalendar = Calendar.getInstance();
        mayCalendar.set(2005, 4, 23, 0, 0, 0);
        assertThat(mDateUtil.convertDateFromServerFormatToCalendar(serverDate).getTime().toString(),
                is(mayCalendar.getTime().toString()));
    }

    @Test
    public void testBuildDateFromDateDialogToServerFormat() {
        String widgetDate;
        int year;
        int month;
        int dayOfMonth;
        Collection<Object[]> inputParamList = buildParamsFromDialogToServer();
        for (Object[] item : inputParamList) {
            year = (int) item[0];
            month = (int) item[1];
            dayOfMonth = (int) item[2];
            widgetDate = (String) item[3];
            assertThat(mDateUtil.buildDateFromDateDialogToServerFormat(year, month, dayOfMonth), is(widgetDate));
        }
    }

    private Collection<Object[]> buildParamsFromDialogToServer() {
        return Arrays.asList(new Object[][]{
                {1900, 0, 1, "1900-01-01"},
                {2000, 11, 31, "2000-12-31"},
                {2001, 10, 13, "2001-11-13"}
        });
    }
}