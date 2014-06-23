package org.mifosplatform.integrationtests.common;

import java.util.List;

import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class BatchApiHelper {

    private static final String BATCH_API_URL = "/mifosng-provider/api/v1/batches?tenantIdentifier=default";

    public static List<BatchResponse> fromJsonString(final String json) {
        return new Gson().fromJson(json, new TypeToken<List<BatchResponse>>() {}.getType());
    }

    public static BatchRequest buildSavingsTransactionRequestWithoutCharges(final Long requestId, final Integer savingsId,
            final Integer paymentTypeId, final String command) {

        final BatchRequest batchRequest = new BatchRequest();
        batchRequest.setRequestId(requestId);
        batchRequest.setRelativeUrl("savingsaccounts/" + savingsId + "/transactions?command=" + command);
        batchRequest.setMethod("POST");

        final String body = "{\"transactionDate\": \"01 June 2013\", \"transactionAmount\": \"100\", \"paymentTypeId\":" + paymentTypeId
                + ",\"accountNumber\": \"1\",\"checkNumber\": \"12\","
                + "\"routingCode\": \"123\",\"receiptNumber\": \"23\", \"bankNumber\": \"1234\","
                + "\"locale\": \"en\", \"dateFormat\": \"dd MMMM yyyy\"}";

        batchRequest.setBody(body);

        return batchRequest;
    }

    public static BatchRequest buildSavingsTransactionRequestWithCharges(final Long requestId, final Integer savingsId,
            final Integer paymentTypeId, final Integer chargeId1, final Integer chargeId2, final Integer chargeId3, final String command) {

        final BatchRequest batchRequest = new BatchRequest();
        batchRequest.setRequestId(requestId);
        batchRequest.setRelativeUrl("savingsaccounts/" + savingsId + "/transactions?command=" + command);
        batchRequest.setMethod("POST");

        final String body = "{\"transactionDate\": \"01 June 2013\", \"transactionAmount\": \"100\", \"paymentTypeId\":" + paymentTypeId
                + ",\"accountNumber\": \"1\",\"checkNumber\": \"12\","
                + "\"routingCode\": \"123\",\"receiptNumber\": \"23\", \"bankNumber\": \"1234\","
                + "\"locale\": \"en\", \"dateFormat\": \"dd MMMM yyyy\"," + "\"charges\": [{\"chargeId\"" + ":" + chargeId1
                + ",\"amount\": 10}, {\"chargeId\"" + ":" + chargeId2 + ", \"amount\": 15}, {\"chargeId\"" + ":" + chargeId3
                + ", \"amount\": 20}]}";

        batchRequest.setBody(body);

        return batchRequest;
    }

    public static BatchRequest buildBatchJournalEntryPostingRequest(final Long requestId, final Integer debitAccountId,
            final Integer creditAccountId, final Integer accountingRuleId) {

        final BatchRequest batchRequest = new BatchRequest();
        batchRequest.setRequestId(requestId);
        batchRequest.setRelativeUrl("journalentries");
        batchRequest.setMethod("POST");

        final String body = "{\"locale\": \"en\", \"dateFormat\": \"dd MMMM yyyy\","
                + "\"officeId\": 1, \"transactionDate\": \"01 January 2014\","
                + "\"referenceNumber\": \"2\", \"comments\": \"Paying House Rent\"," + "\"accountingRule\":" + accountingRuleId
                + ", \"currencyCode\": \"USD\"," + "\"credits\": [{\"glAccountId\":" + creditAccountId + ","
                + "\"amount\": \"1000\"}], \"debits\": [{\"glAccountId\":" + debitAccountId + "," + "\"amount\": \"1000\"}]}";

        batchRequest.setBody(body);

        return batchRequest;
    }

    public static BatchRequest buildBatchAccountTransferRequest(final Long requestId, final Integer fromOfficeId, final Integer toOfficeId,
            final Integer fromClientId, final Integer toClientId, final Integer fromAccountId, final Integer toAccountId,
            final String fromAccountType, final String toAccountType) {

        final BatchRequest batchRequest = new BatchRequest();
        batchRequest.setRequestId(requestId);
        batchRequest.setRelativeUrl("accounttransfers");
        batchRequest.setMethod("POST");

        final String body = "{\"fromAccountId\": \"" + fromAccountId + "\"," + "\"fromAccountType\":" + fromAccountType
                + ", \"toOfficeId\":" + toOfficeId + ",\"toClientId\":" + toClientId + ", \"toAccountType\":" + toAccountType
                + ",\"toAccountId\":" + toAccountId + ", \"transferAmount\": \"100\","
                + "\"transferDate\": \"01 June 2014\", \"transferDescription\": \"FT\","
                + "\"locale\": \"en\", \"dateFormat\": \"dd MMMM yyyy\"," + "\"fromClientId\":" + fromClientId + ",\"fromOfficeId\":"
                + fromOfficeId + "}";

        batchRequest.setBody(body);

        return batchRequest;
    }

    public static List<BatchResponse> makeBatchRequest(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            List<BatchRequest> batchRequests) {

        final String jsonifiedRequest = new Gson().toJson(batchRequests);
        final String response = Utils.performServerPost(requestSpec, responseSpec, BATCH_API_URL, jsonifiedRequest, null);
        return fromJsonString(response);

    }
}
