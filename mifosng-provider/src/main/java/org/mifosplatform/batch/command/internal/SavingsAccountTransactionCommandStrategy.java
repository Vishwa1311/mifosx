package org.mifosplatform.batch.command.internal;

import org.mifosplatform.batch.command.CommandStrategy;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.batch.exception.ErrorHandler;
import org.mifosplatform.batch.exception.ErrorInfo;
import org.mifosplatform.portfolio.savings.api.SavingsAccountTransactionsApiResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

public class SavingsAccountTransactionCommandStrategy implements CommandStrategy {

    private final SavingsAccountTransactionsApiResource savingsAccountTransactionsApiResource;

    @Autowired
    public SavingsAccountTransactionCommandStrategy(final SavingsAccountTransactionsApiResource savingsAccountTransactionsApiResource) {
        this.savingsAccountTransactionsApiResource = savingsAccountTransactionsApiResource;
    }

    @Override
    public BatchResponse execute(final BatchRequest request) {

        final BatchResponse response = new BatchResponse();
        final String responseBody;

        try {

            final String url = request.getRelativeUrl();
            String[] pathParams = url.split("/");
            Long savingsId = new Long(pathParams[1]);
            String[] queryParams = url.split("=");
            String command = queryParams[1];

            responseBody = savingsAccountTransactionsApiResource.transaction(savingsId, command, request.getBody());

            response.setRequestId(request.getRequestId());
            response.setHeaders(request.getHeaders());
            response.setStatusCode(200);
            response.setBody(responseBody);

        } catch (RuntimeException e) {

            ErrorInfo ex = ErrorHandler.handler(e);

            response.setStatusCode(ex.getStatusCode());
            response.setBody(new Gson().toJson(ex));
        }

        return response;
    }

}
