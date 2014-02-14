package org.mifosplatform.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.conn.HttpHostConnectException;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.SavingsProductHelper;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.savings.SavingsApplicationTestBuilder;
import org.mifosplatform.integrationtests.common.savings.SavingsStatusChecker;
import org.mifosplatform.integrationtests.common.savings.SavingsTransactionHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

/**
 * Client Savings Integration Test for checking Savings Application.
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class ClientSavingsIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private SavingsTransactionHelper savingsTransactionHelper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void checkClientSavingsCreate() {
        this.savingsTransactionHelper = new SavingsTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);

        final Integer savingsProductID = createSavingsProduct();
        final Integer savingsID = applyForSavingsApplication(clientID, savingsProductID);

        HashMap savingsStatusHashMap = SavingsStatusChecker.getStatusOfSavings(this.requestSpec, this.responseSpec, savingsID);
        SavingsStatusChecker.verifySavingsIsPending(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsTransactionHelper.approveSavings(savingsID);
        SavingsStatusChecker.verifySavingsIsApproved(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsTransactionHelper.activateSavings(savingsID);
        SavingsStatusChecker.verifySavingsIsActive(savingsStatusHashMap);
        
        HashMap savingsTransactionHashMap = this.savingsTransactionHelper.depositToSavingsAccountByClient(savingsID);

        savingsTransactionHashMap = this.savingsTransactionHelper.withdrawalFromSavingsAccountByClient(savingsID);

        final ArrayList<HashMap> savingsTransactions = this.savingsTransactionHelper.getSavingsTransactions(this.requestSpec, this.responseSpec, savingsID, "transactions");
        verifySavingsTransaction(savingsTransactions);
        
        final Object savingsInterest = this.savingsTransactionHelper.getSavingsInterest(this.requestSpec, this.responseSpec, savingsID);
        verifySavingsInterest(savingsInterest);

    }

    private Integer createSavingsProduct() {
        System.out.println("------------------------------CREATING NEW SAVINGS PRODUCT ---------------------------------------");
        final String savingsProductJSON = new SavingsProductHelper() //
                .withInterestCompoundingPeriodTypeAsDaily() //
                .withInterestPostingPeriodTypeAsMonthly() //
                .withInterestCalculationPeriodTypeAsDailyBalance() //
                .build();
        return this.savingsTransactionHelper.getSavingsProductId(savingsProductJSON);
    }

    private Integer applyForSavingsApplication(final Integer clientID, final Integer savingsProductID) {
        System.out.println("--------------------------------APPLYING FOR SAVINGS APPLICATION--------------------------------");
        final String savingsApplicationJSON = new SavingsApplicationTestBuilder() //
                .withSubmittedOnDate("08 January 2013") //
                .build(clientID.toString(), savingsProductID.toString());
        return this.savingsTransactionHelper.getSavingsId(savingsApplicationJSON);
    }
    
    private void verifySavingsTransaction(final ArrayList<HashMap> savingsTransactions) {
        System.out.println("--------------------VERIFYING THE BALANCE, INTEREST --------------------------");

        assertEquals("Verifying Minimum opening Balance", new Float("1000.0"), savingsTransactions.get(2).get("runningBalance"));
        assertEquals("Verifying Balance after Deposit", new Float("3000.0"), savingsTransactions.get(1).get("runningBalance"));
        assertEquals("Verifying Balance after Withdrawal", new Float("2000.0"), savingsTransactions.get(0).get("runningBalance"));
    }
    
    private void verifySavingsInterest(final Object savingsInterest) {
        System.out.println("--------------------VERIFYING THE BALANCE, INTEREST --------------------------");

        assertEquals("Verifying Interest Calculation", new Float("238.3399"), savingsInterest);
    }
}