package org.mifosplatform.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.Utils;
import org.mifosplatform.integrationtests.common.accounting.Account;
import org.mifosplatform.integrationtests.common.savings.SavingApplicationTestBuilder;
//import org.mifosplatform.integrationtests.common.savings.SavingApplicationTestBuilder;
import org.mifosplatform.integrationtests.common.savings.SavingProductTestBuilder;
import org.mifosplatform.integrationtests.common.savings.SavingTransactionHelper;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ClientSavingIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
	private SavingTransactionHelper savingTransactionHelper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }
    
    @Test
    public void checkClientSavingCreate() {
        this.savingTransactionHelper = new SavingTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);
        final Integer savingProductID = createSavingProduct();
        final Integer savingID = applyForSavingApplication(clientID, savingProductID);
    }
    
    private Integer createSavingProduct() {
        System.out.println("------------------------------CREATING NEW SAVING PRODUCT ---------------------------------------");
        //Account[] accounts = (Account[]) new ArrayList<Account>().toArray();
        final String savingProductJSON = new SavingProductTestBuilder() //
                .withInterestCompoundingPeriodTypeAsMonthly() //
                .withInterestPostingPeriodTypeAsQuarterly() //
                .withInterestCalculationPeriodTypeAsAverageDailyBalance() //
                .build();
        return this.savingTransactionHelper.getSavingProductId(savingProductJSON);
    }
    
    private Integer applyForSavingApplication(final Integer clientID, final Integer savingProductID) {
        System.out.println("--------------------------------APPLYING FOR SAVING APPLICATION--------------------------------");
        final String savingApplicationJSON = new SavingApplicationTestBuilder() //
                .withSubmittedOnDate("08 February 2014") //
                .build(clientID.toString(), savingProductID.toString());
        return this.savingTransactionHelper.getSavingId(savingApplicationJSON);
    }
}