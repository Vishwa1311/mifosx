package org.mifosplatform.integrationtests.common.accounting;

import java.util.ArrayList;
import java.util.HashMap;

import org.mifosplatform.integrationtests.common.CommonConstants;
import org.mifosplatform.integrationtests.common.Utils;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings("rawtypes")
public class AccountHelper {

    private final String CREATE_GL_ACCOUNT_URL = "/mifosng-provider/api/v1/glaccounts?tenantIdentifier=default";
    private final String GL_ACCOUNT_ID_RESPONSE = "resourceId";

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    public AccountHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    public Account createAssetAccount() {
        final String assetAccountJSON = new GLAccountBuilder().withAccountTypeAsAsset().build();
        final Integer accountID = Utils.performServerPost(this.requestSpec, this.responseSpec, this.CREATE_GL_ACCOUNT_URL,
                assetAccountJSON, this.GL_ACCOUNT_ID_RESPONSE);
        return new Account(accountID, Account.AccountType.ASSET);
    }

    public Account createIncomeAccount() {
        final String assetAccountJSON = new GLAccountBuilder().withAccountTypeAsIncome().build();
        final Integer accountID = Utils.performServerPost(this.requestSpec, this.responseSpec, this.CREATE_GL_ACCOUNT_URL,
                assetAccountJSON, this.GL_ACCOUNT_ID_RESPONSE);
        return new Account(accountID, Account.AccountType.INCOME);
    }

    public Account createExpenseAccount() {
        final String assetAccountJSON = new GLAccountBuilder().withAccountTypeAsExpense().build();
        final Integer accountID = Utils.performServerPost(this.requestSpec, this.responseSpec, this.CREATE_GL_ACCOUNT_URL,
                assetAccountJSON, this.GL_ACCOUNT_ID_RESPONSE);
        return new Account(accountID, Account.AccountType.EXPENSE);
    }

    public Account createLiabilityAccount() {
        final String assetAccountJSON = new GLAccountBuilder().withAccountTypeAsLiability().build();
        final Integer accountID = Utils.performServerPost(this.requestSpec, this.responseSpec, this.CREATE_GL_ACCOUNT_URL,
                assetAccountJSON, this.GL_ACCOUNT_ID_RESPONSE);
        return new Account(accountID, Account.AccountType.LIABILITY);
    }

    public ArrayList getAccountingWithRunningBalances() {
        final String GET_RUNNING_BALANCE_URL = "/mifosng-provider/api/v1/glaccounts?fetchRunningBalance=true";
        final ArrayList<HashMap> accountRunningBalance = Utils.performServerGet(this.requestSpec, this.responseSpec,
                GET_RUNNING_BALANCE_URL, "");
        return accountRunningBalance;
    }

    public HashMap getAccountingWithRunningBalanceById(final String accountId) {
        final String GET_RUNNING_BALANCE_URL = "/mifosng-provider/api/v1/glaccounts/" + accountId + "?fetchRunningBalance=true";
        final HashMap accountRunningBalance = Utils.performServerGet(this.requestSpec, this.responseSpec, GET_RUNNING_BALANCE_URL, "");
        return accountRunningBalance;
    }

    public Integer createAccountingRules(final Integer accountToDebitId, final Integer accountToCreditId) {
        final String GET_RUNNING_BALANCE_URL = "/mifosng-provider/api/v1/accountingrules?tenantIdentifier=default";
        final Integer response = Utils.performServerPost(this.requestSpec, this.responseSpec, GET_RUNNING_BALANCE_URL,
                getAccountingRuleAsJSON(accountToDebitId, accountToCreditId), CommonConstants.RESPONSE_RESOURCE_ID);
        return response;
    }

    private String getAccountingRuleAsJSON(final Integer accountToDebitId, final Integer accountToCreditId) {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", Utils.randomNameGenerator("HOME_RENT_", 5));
        map.put("officeId", 1);
        map.put("description", Utils.randomNameGenerator("DESC_", 5));
        map.put("accountToDebit", accountToDebitId);
        map.put("accountToCredit", accountToCreditId);
        String accountingRuleJSON = new Gson().toJson(map);
        System.out.println(accountingRuleJSON);
        return accountingRuleJSON;
    }

}
