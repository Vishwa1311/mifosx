package org.mifosplatform.integrationtests.common.savings;

import java.util.HashMap;
import java.util.Map;

import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.accounting.Account;
import org.mifosplatform.integrationtests.common.accounting.Account.AccountType;

import com.google.gson.Gson;

@SuppressWarnings("unused")
public class SavingsProductTestBuilder {

    private static final String LOCALE = "en_GB";
    private static final String DIGITS_AFTER_DECIMAL = "4";
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
    private String nominalAnnualInterestRate = "10.0";
    private String accountingRule = NONE;
    private String savingsReferenceAccountId = "1";
    private String transfersInSuspenseAccountId = "4";
    private String savingsControlAccountId = "4";
    private String interestOnSavingsAccountId = "3";
    private String incomeFromFeeAccountId = "2";
    private String incomeFromPenaltyAccountId = "2";
    private String minRequiredOpeningBalance = "1000.0";
    private String lockinPeriodFrequency = "0";
    private String withdrawalFeeForTransfers = "true";
    private String lockinPeriodFrequencyType = DAYS;
    private final String currencyCode = INR;
    private final String interestCalculationDaysInYearType = DAYS_365;
    private Account[] accountList = null;

    public String build() {
        final HashMap<String, String> map = new HashMap<String, String>();

        map.put("name", this.nameOfSavingsProduct);
        map.put("shortName", this.shortName);
        map.put("description", this.description);
        map.put("currencyCode", this.currencyCode);
        map.put("interestCalculationDaysInYearType", this.interestCalculationDaysInYearType);
        map.put("locale", LOCALE);
        map.put("digitsAfterDecimal", DIGITS_AFTER_DECIMAL);
        map.put("inMultiplesOf", IN_MULTIPLES_OF);
        map.put("interestCalculationType", this.interestCalculationType);
        map.put("nominalAnnualInterestRate", this.nominalAnnualInterestRate);
        map.put("interestCompoundingPeriodType", this.interestCompoundingPeriodType);
        map.put("interestPostingPeriodType", this.interestPostingPeriodType);
        map.put("accountingRule", this.accountingRule);
        map.put("savingsReferenceAccountId", this.savingsReferenceAccountId);
        map.put("transfersInSuspenseAccountId", this.transfersInSuspenseAccountId);
        map.put("savingsControlAccountId", this.savingsControlAccountId);
        map.put("interestOnSavingsAccountId", this.interestOnSavingsAccountId);
        map.put("incomeFromFeeAccountId", this.incomeFromFeeAccountId);
        map.put("incomeFromPenaltyAccountId", this.incomeFromPenaltyAccountId);
        map.put("minRequiredOpeningBalance", this.minRequiredOpeningBalance);
        map.put("lockinPeriodFrequency", this.lockinPeriodFrequency);
        map.put("lockinPeriodFrequencyType", this.lockinPeriodFrequencyType);
        map.put("withdrawalFeeForTransfers", this.withdrawalFeeForTransfers);

        if (this.accountingRule.equals(CASH_BASED)) {
            Account[] account_list = { new Account(1, AccountType.ASSET) };
            withAccountingRuleAsCashBased(account_list);
            map.putAll(getAccountMappingForCashBased(account_list));
        }
        String savingsProductCreateJson = new Gson().toJson(map);
        System.out.println(savingsProductCreateJson);
        return savingsProductCreateJson;
    }

    public SavingsProductTestBuilder withSavingsName(final String savingsName) {
        this.nameOfSavingsProduct = savingsName;
        return this;
    }

    public SavingsProductTestBuilder withInterestCompoundingPeriodTypeAsDaily() {
        this.interestCompoundingPeriodType = DAILY;
        return this;
    }

    public SavingsProductTestBuilder withInterestCompoundingPeriodTypeAsMonthly() {
        this.interestCompoundingPeriodType = MONTHLY;
        return this;
    }

    public SavingsProductTestBuilder withInterestPostingPeriodTypeAsMonthly() {
        this.interestPostingPeriodType = MONTHLY;
        return this;
    }

    public SavingsProductTestBuilder withInterestPostingPeriodTypeAsQuarterly() {
        this.interestPostingPeriodType = QUARTERLY;
        return this;
    }

    public SavingsProductTestBuilder withInterestPostingPeriodTypeAsAnnual() {
        this.interestPostingPeriodType = ANNUAL;
        return this;
    }

    public SavingsProductTestBuilder withInterestCalculationPeriodTypeAsDailyBalance() {
        this.interestCalculationType = INTEREST_CALCULATION_USING_DAILY_BALANCE;
        return this;
    }

    public SavingsProductTestBuilder withInterestCalculationPeriodTypeAsAverageDailyBalance() {
        this.interestCalculationType = INTEREST_CALCULATION_USING_AVERAGE_DAILY_BALANCE;
        return this;
    }

    public SavingsProductTestBuilder withAccountingRuleAsNone() {
        this.accountingRule = NONE;
        return this;
    }

    public SavingsProductTestBuilder withAccountingRuleAsCashBased(final Account[] account_list) {
        this.accountingRule = CASH_BASED;
        this.accountList = account_list;
        return this;
    }

    private Map<String, String> getAccountMappingForCashBased(final Account[] accountList) {
        final Map<String, String> map = new HashMap<String, String>();
        if (accountList != null) {
            for (int i = 0; i < this.accountList.length; i++) {
                if (this.accountList[i].getAccountType().equals(Account.AccountType.ASSET)) {
                    final String ID = this.accountList[i].getAccountID().toString();
                    map.put("savingsReferenceAccountId", ID);
                }
                if (this.accountList[i].getAccountType().equals(Account.AccountType.LIABILITY)) {
                    final String ID = this.accountList[i].getAccountID().toString();
                    map.put("savingsControlAccountId", ID);
                    map.put("transfersInSuspenseAccountId", ID);
                }
                if (this.accountList[i].getAccountType().equals(Account.AccountType.EXPENSE)) {
                    final String ID = this.accountList[i].getAccountID().toString();
                    map.put("interestOnSavingsAccountId", ID);
                }
                if (this.accountList[i].getAccountType().equals(Account.AccountType.INCOME)) {
                    final String ID = this.accountList[i].getAccountID().toString();
                    map.put("incomeFromFeeAccountId", ID);
                    map.put("incomeFromPenaltyAccountId", ID);
                }
            }
        }
        return map;
    }
}