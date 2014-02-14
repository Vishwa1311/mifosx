package org.mifosplatform.integrationtests.common.savings;

import java.util.HashMap;

import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.accounting.Account;

import com.google.gson.Gson;

@SuppressWarnings("unused")
public class SavingsApplicationTestBuilder {

    private static final String LOCALE = "en_GB";
    private static final String DIGITS_AFTER_DECIMAL = "2";
    private static final String IN_MULTIPLES_OF = "0";
    private static final String INR = "INR";
    private static final String DAYS = "0";
    private static final String WEEKS = "1";
    private static final String MONTHS = "2";
    private static final String YEARS = "3";
    private static final String DAILY = "1";
    private static final String MONTHLY = "4";
    private static final String QUARTERLY = "5";
    private static final String ANNUAL = "7";
    private static final String INTEREST_CALCULATION_USING_DAILY_BALANCE = "1";
    private static final String INTEREST_CALCULATION_USING_AVERAGE_DAILY_BALANCE = "2";
    private static final String DAYS_360 = "360";
    private static final String DAYS_365 = "365";
    private static final String NONE = "1";
    private static final String CASH_BASED = "2";

    private String nameOfSavingsProduct = ClientHelper.randomNameGenerator("SAVINGS_PRODUCT_", 6);
    private String shortName = ClientHelper.randomNameGenerator("", 4);
    private String description = ClientHelper.randomNameGenerator("", 20);
    private String interestCompoundingPeriodType = "1";
    private String interestPostingPeriodType = "4";
    private String interestCalculationType = INTEREST_CALCULATION_USING_DAILY_BALANCE;
    private String nominalAnnualInterestRate = "5.0";
    private String accountingRule = NONE;
    private String submittedOnDate = "";
    private final String currencyCode = INR;
    private final String interestCalculationDaysInYearType = DAYS_365;
    private Account[] accountList = null;

    public String build(final String ID, final String savingsProductId) {

        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("locale", "en_GB");
        map.put("clientId", ID);
        map.put("productId", savingsProductId);
        map.put("interestCalculationDaysInYearType", this.interestCalculationDaysInYearType);
        map.put("locale", LOCALE);
        // map.put("digitsAfterDecimal", DIGITS_AFTER_DECIMAL);
        // map.put("inMultiplesOf", IN_MULTIPLES_OF);
        map.put("interestCalculationType", INTEREST_CALCULATION_USING_AVERAGE_DAILY_BALANCE);
        map.put("nominalAnnualInterestRate", this.nominalAnnualInterestRate);
        map.put("interestCompoundingPeriodType", this.interestCompoundingPeriodType);
        map.put("interestPostingPeriodType", this.interestPostingPeriodType);
        map.put("submittedOnDate", this.submittedOnDate);
        String savingsApplicationJSON = new Gson().toJson(map);
        System.out.println(savingsApplicationJSON);
        return savingsApplicationJSON;
    }

    public SavingsApplicationTestBuilder withSubmittedOnDate(final String savingsApplicationSubmittedDate) {
        this.submittedOnDate = savingsApplicationSubmittedDate;
        return this;
    }
}