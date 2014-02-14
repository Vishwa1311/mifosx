package org.mifosplatform.integrationtests.common.savings;

import java.util.HashMap;

import org.mifosplatform.integrationtests.common.ClientHelper;
import org.mifosplatform.integrationtests.common.accounting.Account;

import com.google.gson.Gson;

@SuppressWarnings("unused")
public class SavingsApplicationTestBuilder {

    private static final String LOCALE = "en_GB";

    private String submittedOnDate = "";

    public String build(final String ID, final String savingsProductId) {

        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("dateFormat", "dd MMMM yyyy");
        map.put("locale", "en_GB");
        map.put("clientId", ID);
        map.put("productId", savingsProductId);
        map.put("locale", LOCALE);
        map.put("submittedOnDate", this.submittedOnDate);
        String savingsApplicationJSON = new Gson().toJson(map);
        System.out.println(savingsApplicationJSON);
        return savingsApplicationJSON;
    }

    public SavingsApplicationTestBuilder withSubmittedOnDate(final String savingsApplicationSubmittedDate) {
        this.submittedOnDate = savingsApplicationSubmittedDate;
        return this;
    }
}