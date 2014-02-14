package org.mifosplatform.integrationtests.common.savings;

import java.util.HashMap;

import org.mifosplatform.integrationtests.common.Utils;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings({ "rawtypes", "unused" })
public class SavingsTransactionHelper {

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    private static final String CREATE_SAVINGS_PRODUCT_URL = "/mifosng-provider/api/v1/savingsproducts?tenantIdentifier=default";
    private static final String APPLY_SAVINGS_URL = "/mifosng-provider/api/v1/savingsaccounts?tenantIdentifier=default";
    private static final String APPROVE_SAVINGS_COMMAND = "approve";
    private static final String UNDO_APPROVAL_SAVINGS_COMMAND = "undoApproval";
    private static final String ACTIVATE_SAVINGS_COMMAND = "activate";
    private static final String DEPOSIT_SAVINGS_COMMAND = "deposit";
    private static final String WITHDRAW_SAVINGS_COMMAND = "withdrawal";
    private static final String CALCULATE_INTEREST_SAVINGS_COMMAND = "calculateInterest";

    public SavingsTransactionHelper(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }

    public Integer getSavingsProductId(final String savingsProductJSON) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, CREATE_SAVINGS_PRODUCT_URL, savingsProductJSON, "resourceId");
    }

    public Integer getSavingsId(final String savingsApplicationJSON) {
        return Utils.performServerPost(this.requestSpec, this.responseSpec, APPLY_SAVINGS_URL, savingsApplicationJSON, "savingsId");
    }

    public HashMap approveSavings(final String approvalDate, final Integer savingsID) {
        System.out.println("--------------------------------- APPROVING SAVINGS APPLICATION ------------------------------------");
        return performSavingsTransaction(createSavingsOperationURL(APPROVE_SAVINGS_COMMAND, savingsID),
                getApproveSavingsAsJSON(approvalDate));
    }

    public HashMap undoApproval(final Integer savingsID) {
        System.out.println("--------------------------------- UNDO APPROVING SAVINGS APPLICATION -------------------------------");
        final String undoBodyJson = "{'note':'UNDO APPROVAL'}";
        return performSavingsTransaction(createSavingsOperationURL(UNDO_APPROVAL_SAVINGS_COMMAND, savingsID), undoBodyJson);
    }

    public HashMap activateSavings(final String activatedOnDate, final Integer savingsID) {
        System.out.println("---------------------------------- ACTIVATING SAVINGS APPLICATION ----------------------------------");
        return performSavingsTransaction(createSavingsOperationURL(ACTIVATE_SAVINGS_COMMAND, savingsID),
                getActivatedSavingsAsJSON(activatedOnDate));
    }

    public HashMap depositToSavingsAccountByClient(final String date, final Integer savingsID) {
        System.out.println("--------------------------------- SAVINGS TRANSACTION DEPOSIT --------------------------------");
        return performSavingsTransaction(createSavingsTransactionURL(DEPOSIT_SAVINGS_COMMAND, savingsID),
                getDepositSavingsApplicationAsJSON(date));
    }

    public HashMap withdrawalFromSavingsAccountByClient(final String date, final Integer savingsId) {
        System.out.println("\n--------------------------------- SAVINGS TRANSACTION WITHDRAWAL --------------------------------");
        return performSavingsTransaction(createSavingsTransactionURL(WITHDRAW_SAVINGS_COMMAND, savingsId),
                getWithdrawalSavingsApplicationAsJSON(date));
    }

    /*public HashMap calculateInterestForSavings(final Integer savingsId) {
        System.out.println("--------------------------------- CALCULATING INTEREST FOR SAVINGS --------------------------------");
        return performSavingsTransaction(createSavingsCalculateInterestURL(CALCULATE_INTEREST_SAVINGS_COMMAND, savingsId),
                getCalculatedInterestForSavingsApplicationAsJSON());
    }*/

    private String getApproveSavingsAsJSON(final String approvalDate) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("approvedOnDate", approvalDate);
        map.put("note", "Approval NOTE");
        String savingsAccountApproveJson = new Gson().toJson(map);
        System.out.println(savingsAccountApproveJson);
        return savingsAccountApproveJson;
    }

    private String getActivatedSavingsAsJSON(final String activationDate) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("activatedOnDate", activationDate);
        String savingsAccountActivateJson = new Gson().toJson(map);
        System.out.println(savingsAccountActivateJson);
        return savingsAccountActivateJson;
    }

    private String getDepositSavingsApplicationAsJSON(final String depositDate) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("transactionDate", depositDate);
        map.put("transactionAmount", "1000");
        String savingsAccountDepositJson = new Gson().toJson(map);
        System.out.println(savingsAccountDepositJson);
        return savingsAccountDepositJson;
    }

    private String getWithdrawalSavingsApplicationAsJSON(final String withdrawalDate) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("locale", "en");
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("transactionDate", withdrawalDate);
        map.put("transactionAmount", "1000");
        String savingsAccountWithdrawalJson = new Gson().toJson(map);
        System.out.println(savingsAccountWithdrawalJson);
        return savingsAccountWithdrawalJson;
    }

    private String getCalculatedInterestForSavingsApplicationAsJSON() {
        final HashMap<String, String> map = new HashMap<String, String>();
        String savingsAccountCalculatedInterestJson = new Gson().toJson(map);
        System.out.println(savingsAccountCalculatedInterestJson);
        return savingsAccountCalculatedInterestJson;
    }

    private String createSavingsOperationURL(final String command, final Integer savingsID) {
        return "/mifosng-provider/api/v1/savingsaccounts/" + savingsID + "?command=" + command + "&tenantIdentifier=default";
    }

    private String createSavingsTransactionURL(final String command, final Integer savingsID) {
        return "/mifosng-provider/api/v1/savingsaccounts/" + savingsID + "/transactions?command=" + command + "&tenantIdentifier=default";
    }

    private String createSavingsCalculateInterestURL(final String command, final Integer savingsID) {
        return "/mifosng-provider/api/v1/savingsaccounts/" + savingsID + "?command=" + command + "&tenantIdentifier=default";
    }

    private HashMap performSavingsTransaction(final String postURLForSavingsTransaction, final String jsonToBeSent) {

        final HashMap response = Utils.performServerPost(this.requestSpec, this.responseSpec, postURLForSavingsTransaction, jsonToBeSent,
                "changes");
        return (HashMap) response.get("status");
    }
}