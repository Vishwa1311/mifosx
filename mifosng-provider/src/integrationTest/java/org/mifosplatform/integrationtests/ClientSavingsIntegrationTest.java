package org.mifosplatform.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.accounting.Account;
import org.mifosplatform.integrationtests.common.accounting.Account.AccountType;
import org.mifosplatform.integrationtests.common.savings.SavingsApplicationTestBuilder;
import org.mifosplatform.integrationtests.common.savings.SavingsProductTestBuilder;
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

    @Test
    public void checkClientSavingsCreate() {
        this.savingsTransactionHelper = new SavingsTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);
        
        final Integer savingsProductID = createSavingsProduct();
        SavingsStatusChecker.verifySavingsProductCreatedOnServer(this.requestSpec, this.responseSpec, savingsProductID);
        
        final Integer savingsID = applyForSavingsApplication(clientID, savingsProductID);
        SavingsStatusChecker.verifySavingsApplicationSubmittedOnServer(this.requestSpec, this.responseSpec, savingsID);

        HashMap savingsStatusHashMap = SavingsStatusChecker.getStatusOfSavings(this.requestSpec, this.responseSpec, savingsID);
        SavingsStatusChecker.verifySavingsIsPending(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsTransactionHelper.approveSavings("09 January 2013", savingsID);
        SavingsStatusChecker.verifySavingsIsApproved(savingsStatusHashMap);
        
        savingsStatusHashMap = this.savingsTransactionHelper.undoApproval(savingsID);
        SavingsStatusChecker.verifySavingsIsPending(savingsStatusHashMap);
        
        savingsStatusHashMap = this.savingsTransactionHelper.approveSavings("09 January 2013", savingsID);
        SavingsStatusChecker.verifySavingsIsApproved(savingsStatusHashMap);

        savingsStatusHashMap = this.savingsTransactionHelper.activateSavings("10 January 2013", savingsID);
        SavingsStatusChecker.verifySavingsIsActive(savingsStatusHashMap);

        HashMap savingsTransactionHashMap = this.savingsTransactionHelper.depositToSavingsAccountByClient("10 January 2013", savingsID);

        savingsTransactionHashMap = this.savingsTransactionHelper.withdrawalFromSavingsAccountByClient("10 January 2013", savingsID);

        //savingsTransactionHashMap = this.savingsTransactionHelper.calculateInterestForSavings(savingsID);
    }

    private Integer createSavingsProduct() {
        System.out.println("------------------------------CREATING NEW SAVINGS PRODUCT ---------------------------------------");
        final String savingsProductJSON = new SavingsProductTestBuilder() //
                .withInterestCompoundingPeriodTypeAsMonthly() //
                .withInterestPostingPeriodTypeAsQuarterly() //
                .withInterestCalculationPeriodTypeAsAverageDailyBalance() //
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
}