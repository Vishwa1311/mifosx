package org.mifosplatform.batch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.mifosplatform.batch.domain.BatchRequest;
import org.mifosplatform.batch.domain.BatchResponse;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Provides methods to create dependency map among the various batchRequests.
 * It also provides method that takes care of dependency resolution among 
 * related requests.
 * 
 * @author Rishabh Shukla
 * @see BatchApiServiceImpl
 */
@Component
public class ResolutionHelper {
	
	private FromJsonHelper fromJsonHelper;
	
	@Autowired
	public ResolutionHelper(final FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper = fromJsonHelper;
	}
	
	/**
	 * Returns a map containing requests that are divided in accordance of dependency relations 
	 * among them. Each different list is identified with a "Key" which is the "requestId" of 
	 * the request at topmost level in dependency hierarchy of that particular list.
	 * 
	 * @param requestList
	 * @return ConcurrentHashMap<Long, List<BatchRequest>>
	 */
	public ConcurrentHashMap<Long, ArrayList<BatchRequest>> getDependencyMap(final List<BatchRequest> requestList) {
		
		//Create a map with a '-1L' which will contain a list of independent requests
		ConcurrentHashMap<Long, ArrayList<BatchRequest>> dependencyMap = new ConcurrentHashMap<Long, ArrayList<BatchRequest>>();
		
		dependencyMap.put(-1L, new ArrayList<BatchRequest>());	
		
		//For every request in the requestList 
		for(final BatchRequest br : requestList) {
			
			final Long reference = br.getReference();
			
			//If current request is dependent on some other request
			if(reference != null) {
				
				//Then find the request on which br is dependent on
				for(final BatchRequest parentRequest : requestList) {
					
					//If request on which br is dependent is found
					if(parentRequest.getRequestId() == reference) {
						
						//Then start the process of adding it to one of the key in the dependencyMap						
						
						//If referenced request is in the list with key "-1L"
						if(dependencyMap.get(-1L).contains(parentRequest)) {
							
							//Then create a new list with key = reference
							ArrayList<BatchRequest> newList = new ArrayList<BatchRequest>();
							newList.add(parentRequest);
							newList.add(br);
							
							//And add this list to the map
							dependencyMap.put(reference, newList);
							
							//Also remove parentRequest from the list at '-1L'
							ArrayList<BatchRequest> listCopy = dependencyMap.get(-1L);
							
							listCopy.remove(parentRequest);
							
							dependencyMap.replace(-1L, listCopy);
						}
						//If current request is not in key '-1L'
						else {
							//Find the list in which it is present
							for(ConcurrentHashMap.Entry<Long, ArrayList<BatchRequest>> entry: dependencyMap.entrySet()) {
								if(entry.getValue().contains(parentRequest)) {
									ArrayList<BatchRequest> listCopy = entry.getValue();
									listCopy.add(br);
									
									//Add this request to this list
									dependencyMap.replace(entry.getKey(), listCopy);															
								}
							}
						}		
					}				
					
				}
			}
			//If request is independent then add it to '-1L'
			else {
				ArrayList<BatchRequest> listCopy = dependencyMap.get(-1L);
				listCopy.add(br);
				
				dependencyMap.replace(-1L, listCopy);
			}
			
		}
		
		return dependencyMap;
	}

	/**
	 * Returns a BatchRequest after dependency resolution. It takes a request and the
	 * response of the request it is dependent upon as its arguments and change the body
	 * or relativeUrl of the request according to parent Request.
	 * 
	 * @param request
	 * @param lastResponse
	 * @return BatchRequest
	 */
	public BatchRequest resoluteRequest(final BatchRequest request,final BatchResponse parentResponse) {
		
		//create a duplicate request
		BatchRequest br = request;
		
		//Gets the body from parent Response as a JsonObject
		final JsonObject jsonResponseBody = this.fromJsonHelper.parse(parentResponse.getBody()).getAsJsonObject();
		
		//Gets the body from current Request as a JsonObject
		final JsonObject jsonRequestBody = this.fromJsonHelper.parse(request.getBody()).getAsJsonObject();
		
		JsonObject jsonResultBody = new JsonObject();
		
		//Iterate through each element in the requestBody to find dependent parameter
		for(Entry<String, JsonElement> element: jsonRequestBody.entrySet()) {
			
			String paramVal = element.getValue().getAsString();
			
			//If a dependent parameter is found
			if(paramVal.contains("{$data.")) {
				
				//Get the parameter name to be replaced 
				final String parameter = paramVal.substring("{$data.".length(), paramVal.length()-1);
				
				//Get the value of the parameter from parent response
				final String resParamValue = jsonResponseBody.get(parameter).getAsString();
				
				//add the value after dependency resolution				
				jsonResultBody.add(parameter, this.fromJsonHelper.parse(resParamValue));
			}
			else {
				jsonResultBody.add(element.getKey(), element.getValue());
			}
		}		

		//Set the body after dependency resolution
		br.setBody(jsonResultBody.toString());		
		
		//Also check the relativeUrl for any dependency resolution
		String relativeUrl = request.getRelativeUrl(); 
		
		if(relativeUrl.contains("{$data.")) {
			final String parameter = relativeUrl.substring(relativeUrl.indexOf('/') + "\"{$data.\"".length(), relativeUrl.length()-2);			
			
			//Get the value of the parameter from last response
			final String resParamValue = jsonResponseBody.get(parameter).getAsString();

			relativeUrl = relativeUrl.replace("{$data." + parameter + "}", resParamValue);
						
			br.setRelativeUrl(relativeUrl);			
		}
		
		return br;
	}
	
}
