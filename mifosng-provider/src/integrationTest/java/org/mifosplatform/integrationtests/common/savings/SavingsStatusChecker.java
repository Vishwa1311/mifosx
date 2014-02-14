package org.mifosplatform.integrationtests.common.savings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.mifosplatform.integrationtests.common.Utils;

import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings("rawtypes")
public class SavingsStatusChecker {

    public static void verifySavingsProductCreatedOnServer(final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec, final Integer generatedSavingsProductID) {
        System.out.println("\n------------------------------ CHECK SAVINGS PRODUCT DETAILS ------------------------------------");
        final String SAVINGS_URL = "/mifosng-provider/api/v1/savingsproducts/" + generatedSavingsProductID + "?tenantIdentifier=default";
        final Integer responseSavingsProductID = Utils.performServerGet(requestSpec, responseSpec, SAVINGS_URL, "id");
        assertEquals("ERROR IN CREATING THE SAVINGS PRODUCT", generatedSavingsProductID, responseSavingsProductID);
        System.out.println("Generated Savings Product Id:" + generatedSavingsProductID + " = Expected Savings Product Id:"
                + responseSavingsProductID + "\n");
    }

    public static void verifySavingsApplicationSubmittedOnServer(final RequestSpecification requestSpec,
            final ResponseSpecification responseSpec, final Integer generatedSavingsID) {
        System.out.println("\n------------------------------ CHECK SAVINGS APPLIACTION DETAILS ------------------------------------");
        final String SAVINGS_URL = "/mifosng-provider/api/v1/savingsproducts/" + generatedSavingsID + "?tenantIdentifier=default";
        final Integer responseSavingsID = Utils.performServerGet(requestSpec, responseSpec, SAVINGS_URL, "id");
        assertEquals("ERROR IN CREATING THE SAVINGS PRODUCT", generatedSavingsID, responseSavingsID);
        System.out.println("Generated Savings Id:" + generatedSavingsID + " = Expected Savings Id:" + responseSavingsID);
    }

    public static void verifySavingsIsApproved(final HashMap savingsStatusHashMap) {
        System.out.println("\n-------------------------------------- VERIFYING SAVINGS APPLICATION IS APPROVED ------------------------------------");
        assertTrue("ERROR IN APPROVING SAVINGS APPLICATION", getStatus(savingsStatusHashMap, "approved"));
        System.out.println("Savings Application Status:" + savingsStatusHashMap + "\n");
    }

    public static void verifySavingsIsPending(final HashMap savingsStatusHashMap) {
        System.out.println("\n-------------------------------------- VERIFYING SAVINGS APPLICATION IS PENDING ------------------------------------");
        assertTrue("SAVINGS ACCOUNT IS NOT IN PENDING STATE", getStatus(savingsStatusHashMap, "submittedAndPendingApproval"));
        System.out.println("Savings Application Status:" + savingsStatusHashMap + "\n");
    }

    public static void verifySavingsIsActive(final HashMap savingsStatusHashMap) {
        System.out.println("\n-------------------------------------- VERIFYING SAVINGS APPLICATION IS ACTIVE ------------------------------------");
        assertTrue("ERROR IN ACTIVATING THE SAVINGS APPLICATION", getStatus(savingsStatusHashMap, "active"));
        System.out.println("Savings Application Status:" + savingsStatusHashMap + "\n");
    }

    public static void verifySavingsAccountIsClosed(final HashMap savingsStatusHashMap) {
        System.out.println("\n-------------------------------------- VERIFYING SAVINGS APPLICATION IS CLOSED ------------------------------------");
        assertTrue("ERROR IN CLOSING THE SAVINGS APPLICATION", getStatus(savingsStatusHashMap, "closed"));
        System.out.println("Savings Application Status:" + savingsStatusHashMap + "\n");
    }

    public static void verifySavingsAccountIsNotActive(final HashMap savingsStatusHashMap) {
        System.out.println("\n-------------------------------------- VERIFYING SAVINGS APPLICATION IS INACTIVE ------------------------------------");
        assertTrue(getStatus(savingsStatusHashMap, "active"));
        System.out.println("Savings Application Status:" + savingsStatusHashMap + "\n");
    }

    public static HashMap getStatusOfSavings(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final Integer savingsID) {
        final String url = "/mifosng-provider/api/v1/savingsaccounts/" + savingsID + "?tenantIdentifier=default";
        return Utils.performServerGet(requestSpec, responseSpec, url, "status");
    }

    private static boolean getStatus(final HashMap savingsStatusMap, final String nameOfSavingsStatusString) {
        return (Boolean) savingsStatusMap.get(nameOfSavingsStatusString);
    }
}