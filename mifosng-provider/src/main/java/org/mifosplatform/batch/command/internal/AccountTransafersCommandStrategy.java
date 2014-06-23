package org.mifosplatform.batch.command.internal;

import org.mifosplatform.batch.command.CommandStrategy;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.batch.exception.ErrorHandler;
import org.mifosplatform.batch.exception.ErrorInfo;
import org.mifosplatform.portfolio.account.api.AccountTransfersApiResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

public class AccountTransafersCommandStrategy implements CommandStrategy {

    private final AccountTransfersApiResource accountTransfersApiResource;

    @Autowired
    public AccountTransafersCommandStrategy(final AccountTransfersApiResource accountTransfersApiResource) {
        this.accountTransfersApiResource = accountTransfersApiResource;
    }

    @Override
    public BatchResponse execute(final BatchRequest request) {

        final BatchResponse response = new BatchResponse();
        final String responseBody;

        try {

            responseBody = accountTransfersApiResource.create(request.getBody());
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
