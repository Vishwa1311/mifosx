package org.mifosplatform.integrationtests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.integrationtests.common.BatchApiHelper;
import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.CommonConstants;
import org.mifosplatform.integrationtests.common.OfficeHelper;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.accounting.Account;
import org.mifosplatform.integrationtests.common.accounting.AccountHelper;
import org.mifosplatform.integrationtests.common.charges.ChargesHelper;
import org.mifosplatform.integrationtests.common.savings.SavingsAccountHelper;
import org.mifosplatform.integrationtests.common.savings.SavingsStatusChecker;
import org.mifosplatform.integrationtests.common.system.CodeHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class BatchTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private SavingsAccountHelper savingsAccountHelper;
    private BatchApiHelper batchApiHelper;
    private AccountHelper accountHelper;

    public static final String COMMAND_DEPOSIT = "deposit";
    public static final String COMMAND_WITHDRAWAL = "withdrawal";

    @Before
    public void setup() {

        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }

    @SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
    @Test
    public void testSavingsTransactionWithoutMultipleCharges() {

        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);

        String codeValue = Utils.randomNameGenerator("CASH_", 5);
        final int codeValuePosition = 0;
        final String codeName = "PaymentType";

        // Retrieve all Codes
        final ArrayList<HashMap> retrieveAllCodes = (ArrayList) CodeHelper.getAllCodes(this.requestSpec, this.responseSpec);

        final Integer paymentTypeCodeId = CodeHelper.getCodeByName(retrieveAllCodes, codeName);
        Assert.assertNotNull("Code with name PaymentType not found", paymentTypeCodeId);

        Integer paymentTypeId = (Integer) CodeHelper.createCodeValue(this.requestSpec, this.responseSpec, paymentTypeCodeId, codeValue,
                codeValuePosition, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientID);

        final Integer savingsProductID = ClientSavingsIntegrationTest.createSavingsProduct(this.requestSpec, this.responseSpec,
                ClientSavingsIntegrationTest.MINIMUM_OPENING_BALANCE);
        Assert.assertNotNull(savingsProductID);

        final Integer savingsId = this.savingsAccountHelper.applyForSavingsApplication(clientID, savingsProductID,
                ClientSavingsIntegrationTest.ACCOUNT_TYPE_INDIVIDUAL);
        Assert.assertNotNull(savingsProductID);

        HashMap savingsStatusHashMap = SavingsStatusChecker.getStatusOfSavings(this.requestSpec, this.responseSpec, savingsId);
        SavingsStatusChecker.verifySavingsIsPending(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsAccountHelper.approveSavings(savingsId);
        SavingsStatusChecker.verifySavingsIsApproved(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsAccountHelper.activateSavings(savingsId);
        SavingsStatusChecker.verifySavingsIsActive(savingsStatusHashMap);

        final BatchRequest br = BatchApiHelper
                .buildSavingsTransactionRequestWithoutCharges(123L, savingsId, paymentTypeId, COMMAND_DEPOSIT);

        List<BatchRequest> batchRequests = new ArrayList<BatchRequest>();
        batchRequests.add(br);

        final List<BatchResponse> response = this.batchApiHelper.makeBatchRequest(this.requestSpec, this.responseSpec, batchRequests);
        Assert.assertEquals(response.size(), 1);
        Assert.assertEquals("Verifying RequestId 123", batchRequests.get(0).getRequestId(), response.get(0).getRequestId());
        Assert.assertEquals("Verifying Status code 200", (long) 200, (long) response.get(0).getStatusCode());

    }

    @SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
    @Test
    public void testSavingsTransactionWithMultipleCharges() {

        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);

        String codeValue = Utils.randomNameGenerator("CASH_", 5);
        final int codeValuePosition = 0;
        final String codeName = "PaymentType";

        // Retrieve all Codes
        final ArrayList<HashMap> retrieveAllCodes = (ArrayList) CodeHelper.getAllCodes(this.requestSpec, this.responseSpec);

        final Integer paymentTypeCodeId = CodeHelper.getCodeByName(retrieveAllCodes, codeName);
        Assert.assertNotNull("Code with name PaymentType not found", paymentTypeCodeId);

        Integer paymentTypeId = (Integer) CodeHelper.createCodeValue(this.requestSpec, this.responseSpec, paymentTypeCodeId, codeValue,
                codeValuePosition, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

        final Integer savingsChargeId1 = (Integer) ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getSavingsChargesWithAdvancedConfigDataAsJSON(paymentTypeId, ChargesHelper.CHARGE_DEPOSIT_FEE),
                CommonConstants.RESPONSE_RESOURCE_ID);
        Assert.assertNotNull(savingsChargeId1);

        final Integer savingsChargeId2 = (Integer) ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getSavingsChargesWithAdvancedConfigDataAsJSON(paymentTypeId, ChargesHelper.CHARGE_DEPOSIT_FEE),
                CommonConstants.RESPONSE_RESOURCE_ID);
        Assert.assertNotNull(savingsChargeId2);

        final Integer savingsChargeId3 = (Integer) ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getSavingsChargesWithAdvancedConfigDataAsJSON(paymentTypeId, ChargesHelper.CHARGE_DEPOSIT_FEE),
                CommonConstants.RESPONSE_RESOURCE_ID);
        Assert.assertNotNull(savingsChargeId3);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientID);

        final Integer savingsProductID = ClientSavingsIntegrationTest.createSavingsProduct(this.requestSpec, this.responseSpec,
                ClientSavingsIntegrationTest.MINIMUM_OPENING_BALANCE);
        Assert.assertNotNull(savingsProductID);

        final Integer savingsId = this.savingsAccountHelper.applyForSavingsApplication(clientID, savingsProductID,
                ClientSavingsIntegrationTest.ACCOUNT_TYPE_INDIVIDUAL);
        Assert.assertNotNull(savingsProductID);

        HashMap savingsStatusHashMap = SavingsStatusChecker.getStatusOfSavings(this.requestSpec, this.responseSpec, savingsId);
        SavingsStatusChecker.verifySavingsIsPending(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsAccountHelper.approveSavings(savingsId);
        SavingsStatusChecker.verifySavingsIsApproved(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsAccountHelper.activateSavings(savingsId);
        SavingsStatusChecker.verifySavingsIsActive(savingsStatusHashMap);

        final BatchRequest br = BatchApiHelper.buildSavingsTransactionRequestWithCharges(1234L, savingsId, paymentTypeId, savingsChargeId1,
                savingsChargeId2, savingsChargeId3, COMMAND_DEPOSIT);

        List<BatchRequest> batchRequests = new ArrayList<BatchRequest>();
        batchRequests.add(br);

        final List<BatchResponse> response = this.batchApiHelper.makeBatchRequest(this.requestSpec, this.responseSpec, batchRequests);
        Assert.assertEquals(response.size(), 1);
        Assert.assertEquals("Verifying RequestId 1234", batchRequests.get(0).getRequestId(), response.get(0).getRequestId());
        Assert.assertEquals("Verifying Status code 200", (long) 200, (long) response.get(0).getStatusCode());

    }

    @SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
    @Test
    public void testBatchSavingsTransactionsWithoutMultipleCharges() {

        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);

        String codeValue = Utils.randomNameGenerator("CASH_", 5);
        final int codeValuePosition = 0;
        final String codeName = "PaymentType";

        // Retrieve all Codes
        final ArrayList<HashMap> retrieveAllCodes = (ArrayList) CodeHelper.getAllCodes(this.requestSpec, this.responseSpec);

        final Integer paymentTypeCodeId = CodeHelper.getCodeByName(retrieveAllCodes, codeName);
        Assert.assertNotNull("Code with name PaymentType not found", paymentTypeCodeId);

        Integer paymentTypeId = (Integer) CodeHelper.createCodeValue(this.requestSpec, this.responseSpec, paymentTypeCodeId, codeValue,
                codeValuePosition, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientID);

        final Integer savingsProductID = ClientSavingsIntegrationTest.createSavingsProduct(this.requestSpec, this.responseSpec,
                ClientSavingsIntegrationTest.MINIMUM_OPENING_BALANCE);
        Assert.assertNotNull(savingsProductID);

        final Integer savingsId = this.savingsAccountHelper.applyForSavingsApplication(clientID, savingsProductID,
                ClientSavingsIntegrationTest.ACCOUNT_TYPE_INDIVIDUAL);
        Assert.assertNotNull(savingsProductID);

        HashMap savingsStatusHashMap = SavingsStatusChecker.getStatusOfSavings(this.requestSpec, this.responseSpec, savingsId);
        SavingsStatusChecker.verifySavingsIsPending(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsAccountHelper.approveSavings(savingsId);
        SavingsStatusChecker.verifySavingsIsApproved(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsAccountHelper.activateSavings(savingsId);
        SavingsStatusChecker.verifySavingsIsActive(savingsStatusHashMap);

        final BatchRequest br1 = BatchApiHelper.buildSavingsTransactionRequestWithoutCharges(123L, savingsId, paymentTypeId,
                COMMAND_DEPOSIT);
        final BatchRequest br2 = BatchApiHelper.buildSavingsTransactionRequestWithoutCharges(124L, savingsId, paymentTypeId,
                COMMAND_WITHDRAWAL);

        final List<BatchRequest> batchRequests = new ArrayList<BatchRequest>();
        batchRequests.add(br1);
        batchRequests.add(br2);

        final List<BatchResponse> response = this.batchApiHelper.makeBatchRequest(this.requestSpec, this.responseSpec, batchRequests);
        Assert.assertEquals(response.size(), 2);
        Assert.assertEquals("Verifying RequestId 123", batchRequests.get(0).getRequestId(), response.get(0).getRequestId());
        Assert.assertEquals("Verifying Status code 200", (long) 200, (long) response.get(0).getStatusCode());

        Assert.assertEquals("Verifying RequestId 124", batchRequests.get(1).getRequestId(), response.get(1).getRequestId());
        Assert.assertEquals("Verifying Status code 200", (long) 200, (long) response.get(1).getStatusCode());

    }

    @SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
    @Test
    public void testBatchSavingsTransactionsWithMultipleCharges() {

        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);

        String codeValue = Utils.randomNameGenerator("CASH_", 5);
        final int codeValuePosition = 0;
        final String codeName = "PaymentType";

        // Retrieve all Codes
        final ArrayList<HashMap> retrieveAllCodes = (ArrayList) CodeHelper.getAllCodes(this.requestSpec, this.responseSpec);

        final Integer paymentTypeCodeId = CodeHelper.getCodeByName(retrieveAllCodes, codeName);
        Assert.assertNotNull("Code with name PaymentType not found", paymentTypeCodeId);

        Integer paymentTypeId = (Integer) CodeHelper.createCodeValue(this.requestSpec, this.responseSpec, paymentTypeCodeId, codeValue,
                codeValuePosition, CodeHelper.SUBRESPONSE_ID_ATTRIBUTE_NAME);

        final Integer savingsChargeId1 = (Integer) ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getSavingsChargesWithAdvancedConfigDataAsJSON(paymentTypeId, ChargesHelper.CHARGE_DEPOSIT_FEE),
                CommonConstants.RESPONSE_RESOURCE_ID);
        Assert.assertNotNull(savingsChargeId1);

        final Integer savingsChargeId2 = (Integer) ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getSavingsChargesWithAdvancedConfigDataAsJSON(paymentTypeId, ChargesHelper.CHARGE_DEPOSIT_FEE),
                CommonConstants.RESPONSE_RESOURCE_ID);
        Assert.assertNotNull(savingsChargeId2);

        final Integer savingsChargeId3 = (Integer) ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getSavingsChargesWithAdvancedConfigDataAsJSON(paymentTypeId, ChargesHelper.CHARGE_DEPOSIT_FEE),
                CommonConstants.RESPONSE_RESOURCE_ID);
        Assert.assertNotNull(savingsChargeId3);

        final Integer savingsChargeId4 = (Integer) ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getSavingsChargesWithAdvancedConfigDataAsJSON(paymentTypeId, ChargesHelper.CHARGE_WITHDRAWAL_FEE),
                CommonConstants.RESPONSE_RESOURCE_ID);
        Assert.assertNotNull(savingsChargeId1);

        final Integer savingsChargeId5 = (Integer) ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getSavingsChargesWithAdvancedConfigDataAsJSON(paymentTypeId, ChargesHelper.CHARGE_WITHDRAWAL_FEE),
                CommonConstants.RESPONSE_RESOURCE_ID);
        Assert.assertNotNull(savingsChargeId2);

        final Integer savingsChargeId6 = (Integer) ChargesHelper.createCharges(this.requestSpec, this.responseSpec,
                ChargesHelper.getSavingsChargesWithAdvancedConfigDataAsJSON(paymentTypeId, ChargesHelper.CHARGE_WITHDRAWAL_FEE),
                CommonConstants.RESPONSE_RESOURCE_ID);
        Assert.assertNotNull(savingsChargeId3);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientID);

        final Integer savingsProductID = ClientSavingsIntegrationTest.createSavingsProduct(this.requestSpec, this.responseSpec,
                ClientSavingsIntegrationTest.MINIMUM_OPENING_BALANCE);
        Assert.assertNotNull(savingsProductID);

        final Integer savingsId = this.savingsAccountHelper.applyForSavingsApplication(clientID, savingsProductID,
                ClientSavingsIntegrationTest.ACCOUNT_TYPE_INDIVIDUAL);
        Assert.assertNotNull(savingsProductID);

        HashMap savingsStatusHashMap = SavingsStatusChecker.getStatusOfSavings(this.requestSpec, this.responseSpec, savingsId);
        SavingsStatusChecker.verifySavingsIsPending(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsAccountHelper.approveSavings(savingsId);
        SavingsStatusChecker.verifySavingsIsApproved(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsAccountHelper.activateSavings(savingsId);
        SavingsStatusChecker.verifySavingsIsActive(savingsStatusHashMap);

        final BatchRequest br1 = BatchApiHelper.buildSavingsTransactionRequestWithCharges(123L, savingsId, paymentTypeId, savingsChargeId1,
                savingsChargeId2, savingsChargeId3, COMMAND_DEPOSIT);
        final BatchRequest br2 = BatchApiHelper.buildSavingsTransactionRequestWithCharges(124L, savingsId, paymentTypeId, savingsChargeId4,
                savingsChargeId5, savingsChargeId6, COMMAND_WITHDRAWAL);
        final BatchRequest br3 = BatchApiHelper.buildSavingsTransactionRequestWithCharges(125L, savingsId, paymentTypeId, savingsChargeId1,
                savingsChargeId2, savingsChargeId3, COMMAND_DEPOSIT);
        final BatchRequest br4 = BatchApiHelper.buildSavingsTransactionRequestWithCharges(126L, savingsId, paymentTypeId, savingsChargeId4,
                savingsChargeId5, savingsChargeId6, COMMAND_WITHDRAWAL);

        final List<BatchRequest> batchRequests = new ArrayList<BatchRequest>();
        batchRequests.add(br1);
        batchRequests.add(br2);
        batchRequests.add(br3);
        batchRequests.add(br4);

        final List<BatchResponse> response = this.batchApiHelper.makeBatchRequest(this.requestSpec, this.responseSpec, batchRequests);
        Assert.assertEquals(response.size(), 4);
        Assert.assertEquals("Verifying RequestId 123", batchRequests.get(0).getRequestId(), response.get(0).getRequestId());
        Assert.assertEquals("Verifying Status code 200", (long) 200, (long) response.get(0).getStatusCode());

        Assert.assertEquals("Verifying RequestId 124", batchRequests.get(1).getRequestId(), response.get(1).getRequestId());
        Assert.assertEquals("Verifying Status code 200", (long) 200, (long) response.get(1).getStatusCode());

        Assert.assertEquals("Verifying RequestId 125", batchRequests.get(2).getRequestId(), response.get(2).getRequestId());
        Assert.assertEquals("Verifying Status code 200", (long) 200, (long) response.get(2).getStatusCode());

        Assert.assertEquals("Verifying RequestId 126", batchRequests.get(3).getRequestId(), response.get(3).getRequestId());
        Assert.assertEquals("Verifying Status code 200", (long) 200, (long) response.get(3).getStatusCode());

    }

    @SuppressWarnings({ "static-access" })
    @Test
    public void testPostingBatchJournalEntry() {

        this.accountHelper = new AccountHelper(this.requestSpec, this.responseSpec);

        final Account assetAccount = this.accountHelper.createAssetAccount();
        final Account liabilityAccount = this.accountHelper.createLiabilityAccount();

        final Integer assetAccountId = assetAccount.getAccountID();
        Assert.assertNotNull(assetAccountId);
        final Integer liabilityAccountId = liabilityAccount.getAccountID();
        Assert.assertNotNull(liabilityAccountId);

        final Integer accountingRuleId = this.accountHelper.createAccountingRules(assetAccountId, liabilityAccountId);

        final BatchRequest br1 = BatchApiHelper.buildBatchJournalEntryPostingRequest(123L, assetAccountId, liabilityAccountId,
                accountingRuleId);
        final BatchRequest br2 = BatchApiHelper.buildBatchJournalEntryPostingRequest(124L, assetAccountId, liabilityAccountId,
                accountingRuleId);
        final List<BatchRequest> batchRequests = new ArrayList<BatchRequest>();
        batchRequests.add(br1);
        batchRequests.add(br2);

        List<BatchResponse> batchResponse = this.batchApiHelper.makeBatchRequest(this.requestSpec, this.responseSpec, batchRequests);

        Assert.assertEquals(batchResponse.size(), 2);
        final String responseBody1 = batchResponse.get(0).getBody();
        final String responseBody2 = batchResponse.get(1).getBody();

        Assert.assertTrue(responseBody1.contains("transactionId"));
        Assert.assertEquals("Verifying RequestId 123", batchRequests.get(0).getRequestId(), batchResponse.get(0).getRequestId());
        Assert.assertEquals("Verifying Status code 200", (long) 200, (long) batchResponse.get(0).getStatusCode());

        Assert.assertTrue(responseBody2.contains("transactionId"));
        Assert.assertEquals("Verifying RequestId 124", batchRequests.get(1).getRequestId(), batchResponse.get(1).getRequestId());
        Assert.assertEquals("Verifying Status code 200", (long) 200, (long) batchResponse.get(1).getStatusCode());

    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void testBatchAccountTransfers() {

        this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);

        OfficeHelper officeHelper = new OfficeHelper(this.requestSpec, this.responseSpec);

        Integer officeId = officeHelper.createOffice("01 January 2011");
        Assert.assertNotNull(officeId);

        final Integer clientID = ClientHelper
                .createClient(this.requestSpec, this.responseSpec, "01 January 2011", String.valueOf(officeId));
        Assert.assertNotNull(clientID);

        final Integer toSavingsProductID = ClientSavingsIntegrationTest.createSavingsProduct(this.requestSpec, this.responseSpec,
                ClientSavingsIntegrationTest.MINIMUM_OPENING_BALANCE);
        Assert.assertNotNull(toSavingsProductID);

        final Integer toSavingsID = this.savingsAccountHelper.applyForSavingsApplication(clientID, toSavingsProductID,
                ClientSavingsIntegrationTest.ACCOUNT_TYPE_INDIVIDUAL);
        Assert.assertNotNull(toSavingsProductID);

        HashMap toSavingsStatusHashMap = SavingsStatusChecker.getStatusOfSavings(this.requestSpec, this.responseSpec, toSavingsID);
        SavingsStatusChecker.verifySavingsIsPending(toSavingsStatusHashMap);

        toSavingsStatusHashMap = this.savingsAccountHelper.approveSavings(toSavingsID);
        SavingsStatusChecker.verifySavingsIsApproved(toSavingsStatusHashMap);

        toSavingsStatusHashMap = this.savingsAccountHelper.activateSavings(toSavingsID);
        SavingsStatusChecker.verifySavingsIsActive(toSavingsStatusHashMap);

        final Integer fromSavingsProductID = ClientSavingsIntegrationTest.createSavingsProduct(this.requestSpec, this.responseSpec,
                ClientSavingsIntegrationTest.MINIMUM_OPENING_BALANCE);
        Assert.assertNotNull(fromSavingsProductID);

        final Integer fromSavingsID = this.savingsAccountHelper.applyForSavingsApplication(clientID, fromSavingsProductID,
                ClientSavingsIntegrationTest.ACCOUNT_TYPE_INDIVIDUAL);
        Assert.assertNotNull(fromSavingsProductID);

        HashMap fromSavingsStatusHashMap = SavingsStatusChecker.getStatusOfSavings(this.requestSpec, this.responseSpec, fromSavingsID);
        SavingsStatusChecker.verifySavingsIsPending(fromSavingsStatusHashMap);

        fromSavingsStatusHashMap = this.savingsAccountHelper.approveSavings(fromSavingsID);
        SavingsStatusChecker.verifySavingsIsApproved(fromSavingsStatusHashMap);

        fromSavingsStatusHashMap = this.savingsAccountHelper.activateSavings(fromSavingsID);
        SavingsStatusChecker.verifySavingsIsActive(fromSavingsStatusHashMap);

        final BatchRequest br1 = BatchApiHelper.buildBatchAccountTransferRequest(123L, officeId, officeId, clientID, clientID,
                fromSavingsID, toSavingsID, AccountTransferTest.FROM_SAVINGS_ACCOUNT_TYPE, AccountTransferTest.TO_SAVINGS_ACCOUNT_TYPE);

        final BatchRequest br2 = BatchApiHelper.buildBatchAccountTransferRequest(124L, officeId, officeId, clientID, clientID,
                fromSavingsID, toSavingsID, AccountTransferTest.FROM_SAVINGS_ACCOUNT_TYPE, AccountTransferTest.TO_SAVINGS_ACCOUNT_TYPE);

        final List<BatchRequest> batchRequests = new ArrayList<BatchRequest>();
        batchRequests.add(br1);
        batchRequests.add(br2);

        final List<BatchResponse> batchResponse = BatchApiHelper.makeBatchRequest(this.requestSpec, this.responseSpec, batchRequests);

        Assert.assertEquals(batchResponse.size(), 2);
        Assert.assertEquals("Verifying RequestId 123", batchRequests.get(0).getRequestId(), batchResponse.get(0).getRequestId());
        Assert.assertEquals("Verifying Status code 200", (long) 200, (long) batchResponse.get(0).getStatusCode());

        Assert.assertEquals("Verifying RequestId 124", batchRequests.get(1).getRequestId(), batchResponse.get(1).getRequestId());
        Assert.assertEquals("Verifying Status code 200", (long) 200, (long) batchResponse.get(1).getStatusCode());

    }

}