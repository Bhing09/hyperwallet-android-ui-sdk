package com.hyperwallet.android.ui.common.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class UtilsTest {
    private Map<String, String> currenciesMap = new HashMap<String, String>() {
        {
            put("ALL", "ALL1,000,000.00");         // Albania Currency
            put("ARS", "ARS1,000,000.00");      // Argentina Currency
            put("AMD", "AMD1,000,000.00");       // Armenia Currency
            put("AUD", "A$1,000,000.00");      // Australia Currency
            put("BDT", "BDT1,000,000.00");      // Bangladesh Currency
            put("BRL", "R$1,000,000.00");      // Brazil Currency
            put("BGN", "BGN1,000,000.00");      // Bulgaria Currency
            put("KHR", "KHR1,000,000.00");      // Cambodia Currency
            put("CAD", "CA$1,000,000.00");      // Canada Currency
            put("CLP", "CLP1,000,000.00");         // Chile Currency
            put("CNY", "CN¥1,000,000.00");      // China Currency
            put("COP", "COP1,000,000.00");         // Colombia Currency
            put("HRK", "HRK1,000,000.00");      // Croatia Currency
            put("CZK", "CZK1,000,000.00");      // Czech Republic Currency
            put("DKK", "DKK1,000,000.00");      // Denmark Currency
            put("EGP", "EGP1,000,000.00");      // Egypt Currency
            put("EUR", "€1,000,000.00");      // Austria Currency
            put("HKD", "HK$1,000,000.00");      // Hong Kong Currency
            put("HUF", "HUF1,000,000.00");      // Hungary Currency
            put("INR", "₹1,000,000.00");       // India Currency
            put("IDR", "IDR1,000,000.00");         // Indonesia Currency
            put("JMD", "JMD1,000,000.00");      // Jamaica Currency
            put("JPY", "¥1,000,000.00");         // Japan Currency
            put("JOD", "JOD1,000,000.00");     // Jordan Currency
            put("KZT", "KZT1,000,000.00");      // Kazakhstan Currency
            put("KES", "KES1,000,000.00");      // Kenya Currency
            put("LAK", "LAK1,000,000.00");         // Laos Currency
            put("MYR", "MYR1,000,000.00");      // Malaysia Currency
            put("MXN", "MXN1,000,000.00");      // Mexico Currency
            put("MAD", "MAD1,000,000.00");      // Morocco Currency
            put("ILS", "₪1,000,000.00");      // Israel Currency
            put("TWD", "NT$1,000,000.00");      // Taiwan Currency
            put("TRY", "TRY1,000,000.00");      // Turkey Currency
            put("NZD", "NZ$1,000,000.00");      // New Zealand Currency
            put("NGN", "NGN1,000,000.00");      // Nigeria Currency
            put("NOK", "NOK1,000,000.00");      // Norway Currency
            put("PKR", "PKR1,000,000.00");         // Pakistan Currency
            put("PEN", "PEN1,000,000.00");      // Peru Currency
            put("PHP", "PHP1,000,000.00");      // Philippines Currency
            put("PLN", "PLN1,000,000.00");      // Poland Currency
            put("GBP", "£1,000,000.00");      // Isle of Man
            put("RON", "RON1,000,000.00");      // Romania Currency
            put("RUB", "RUB1,000,000.00");      // Russia Currency
            put("RSD", "RSD1,000,000.00");         // Serbia Currency
            put("SGD", "SGD1,000,000.00");      // Singapore Currency
            put("ZAR", "ZAR1,000,000.00");      // South Africa Currency
            put("KRW", "₩1,000,000.00");         // South Korea Currency
            put("LKR", "LKR1,000,000.00");      // Sri Lanka Currency
            put("SEK", "SEK1,000,000.00");      // Sweden Currency
            put("CHF", "CHF1,000,000.00");      // Switzerland Currency
            put("THB", "THB1,000,000.00");      // Thailand Currency
            put("TND", "TND1,000,000.00");     // Tunisia Currency
            put("AED", "AED1,000,000.00");      // United Arab Emirates Currency
            put("UGX", "UGX1,000,000.00");         // Uganda Currency
            put("USD", "$1,000,000.00");      // United States Currency
            put("VND", "VND1,000,000.00");         // Vietnam Currency
        }
    };

    @Test
    public void testAllCurrencyFormats() {
        String amount = "1000000";

        for (Map.Entry<String, String> currencyMap : currenciesMap.entrySet()) {
            String currency = Utils.formatCurrency(currencyMap.getKey(), amount);
            assertThat(currency, is(currencyMap.getValue()));
        }
    }
}