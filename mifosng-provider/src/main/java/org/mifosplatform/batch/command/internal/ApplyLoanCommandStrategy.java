package org.mifosplatform.batch.command.internal;

import org.mifosplatform.batch.command.CommandStrategy;
import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.batch.exception.ErrorHandler;
import org.mifosplatform.batch.exception.ErrorInfo;
import org.mifosplatform.portfolio.loanaccount.api.LoansApiResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

public class ApplyLoanCommandStrategy implements CommandStrategy{

	private final LoansApiResource loansApiResource;
	
	@Autowired
	public ApplyLoanCommandStrategy(final LoansApiResource loansApiResource) {
		this.loansApiResource = loansApiResource;
	}
	
	@Override
	public BatchResponse execute(BatchRequest request) {
		
		final BatchResponse response = new BatchResponse();	
		final String responseBody;		

		response.setRequestId(request.getRequestId());
		response.setHeaders(request.getHeaders());
		
		//Try-catch blocks to map exceptions to appropriate status codes
		try {
			
			//Calls 'SubmitLoanFunction' function from 'LoansApiResource' to Apply Loan to an existing client
			responseBody = loansApiResource.calculateLoanScheduleOrSubmitLoanApplication(null, null, request.getBody());
			
			response.setStatusCode(200);
			//Sets the body of the response after loan is successfully applied
			response.setBody(responseBody);
			
		}
		catch (RuntimeException e) {
			
			//Gets an object of type ErrorInfo, containing information about raised exception
			ErrorInfo ex = ErrorHandler.handler(e);
			
			response.setStatusCode(ex.getStatusCode());
			response.setBody(new Gson().toJson(ex));
		}
		
		return response;		
	}
}
