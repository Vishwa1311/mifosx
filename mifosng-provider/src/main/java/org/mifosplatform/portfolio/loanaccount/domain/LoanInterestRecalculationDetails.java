/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mifosplatform.portfolio.loanproduct.domain.InterestRecalculationCompoundingMethod;
import org.mifosplatform.portfolio.loanproduct.domain.LoanProductInterestRecalculationDetails;
import org.mifosplatform.portfolio.loanproduct.domain.LoanRescheduleStrategyMethod;
import org.mifosplatform.portfolio.loanproduct.domain.RecalculationFrequencyType;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Entity for holding interest recalculation setting, details will be copied
 * from product directly
 * 
 * @author conflux
 */

@Entity
@Table(name = "m_loan_recalculation_details")
public class LoanInterestRecalculationDetails extends AbstractPersistable<Long> {

    @OneToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    /**
     * {@link InterestRecalculationCompoundingMethod}
     */
    @Column(name = "compound_type_enum", nullable = false)
    private Integer interestRecalculationCompoundingMethod;

    /**
     * {@link LoanRescheduleStrategyMethod}
     */
    @Column(name = "reschedule_strategy_enum", nullable = false)
    private Integer rescheduleStrategyMethod;

    @Column(name = "rest_frequency_type_enum", nullable = false)
    private Integer restFrequencyType;

    @Column(name = "rest_frequency_interval", nullable = false)
    private Integer restInterval;

    /*
     * @Temporal(TemporalType.DATE)
     * 
     * @Column(name = "rest_freqency_date") private Date restFrequencyDate;
     */

    @Column(name = "rest_frequency_nth_day_enum", nullable = true)
    private Integer restFrequencyNthDay;

    @Column(name = "rest_frequency_weekday_enum", nullable = true)
    private Integer restFrequencyWeekday;

    @Column(name = "rest_frequency_on_day", nullable = true)
    private Integer restFrequencyOnDay;

    @Column(name = "compounding_frequency_type_enum", nullable = true)
    private Integer compoundingFrequencyType;

    @Column(name = "compounding_frequency_interval", nullable = true)
    private Integer compoundingInterval;

    /*
     * @Temporal(TemporalType.DATE)
     * 
     * @Column(name = "compounding_freqency_date") private Date
     * compoundingFrequencyDate;
     */

    @Column(name = "compounding_frequency_nth_day_enum", nullable = true)
    private Integer compoundingFrequencyNthDay;

    @Column(name = "compounding_frequency_weekday_enum", nullable = true)
    private Integer compoundingFrequencyWeekday;

    @Column(name = "compounding_frequency_on_day", nullable = true)
    private Integer compoundingFrequencyOnDay;

    @Column(name = "is_compounding_to_be_posted_as_transaction")
    private Boolean isCompoundingToBePostedAsTransaction;

    protected LoanInterestRecalculationDetails() {
        // Default constructor for jpa repository
    }

    private LoanInterestRecalculationDetails(final Integer interestRecalculationCompoundingMethod, final Integer rescheduleStrategyMethod,
            final Integer restFrequencyType, final Integer restInterval, final Integer restFrequencyNthDay, Integer restFrequencyWeekday,
            Integer restFrequencyOnDay, Integer compoundingFrequencyType, Integer compoundingInterval, Integer compoundingFrequencyNthDay,
            Integer compoundingFrequencyWeekday, Integer compoundingFrequencyOnDay, final boolean isCompoundingToBePostedAsTransaction) {
        this.interestRecalculationCompoundingMethod = interestRecalculationCompoundingMethod;
        this.rescheduleStrategyMethod = rescheduleStrategyMethod;
        this.restFrequencyNthDay = restFrequencyNthDay;
        this.restFrequencyWeekday = restFrequencyWeekday;
        this.restFrequencyOnDay = restFrequencyOnDay;
        this.restFrequencyType = restFrequencyType;
        this.restInterval = restInterval;
        this.compoundingFrequencyNthDay = compoundingFrequencyNthDay;
        this.compoundingFrequencyWeekday = compoundingFrequencyWeekday;
        this.compoundingFrequencyOnDay = compoundingFrequencyOnDay;
        this.compoundingFrequencyType = compoundingFrequencyType;
        this.compoundingInterval = compoundingInterval;
        this.isCompoundingToBePostedAsTransaction = isCompoundingToBePostedAsTransaction;
    }

    public static LoanInterestRecalculationDetails createFrom(
            final LoanProductInterestRecalculationDetails loanProductInterestRecalculationDetails) {
        return new LoanInterestRecalculationDetails(loanProductInterestRecalculationDetails.getInterestRecalculationCompoundingMethod(),
                loanProductInterestRecalculationDetails.getRescheduleStrategyMethod(), loanProductInterestRecalculationDetails
                        .getRestFrequencyType().getValue(), loanProductInterestRecalculationDetails.getRestInterval(),
                loanProductInterestRecalculationDetails.getRestFrequencyNthDay(),
                loanProductInterestRecalculationDetails.getRestFrequencyWeekday(),
                loanProductInterestRecalculationDetails.getRestFrequencyOnDay(), loanProductInterestRecalculationDetails
                        .getCompoundingFrequencyType().getValue(), loanProductInterestRecalculationDetails.getCompoundingInterval(),
                loanProductInterestRecalculationDetails.getCompoundingFrequencyNthDay(),
                loanProductInterestRecalculationDetails.getCompoundingFrequencyWeekday(),
                loanProductInterestRecalculationDetails.getCompoundingFrequencyOnDay(),
                loanProductInterestRecalculationDetails.getIsCompoundingToBePostedAsTransaction());
    }

    public void updateLoan(final Loan loan) {
        this.loan = loan;
    }

    public InterestRecalculationCompoundingMethod getInterestRecalculationCompoundingMethod() {
        return InterestRecalculationCompoundingMethod.fromInt(this.interestRecalculationCompoundingMethod);
    }

    public LoanRescheduleStrategyMethod getRescheduleStrategyMethod() {
        return LoanRescheduleStrategyMethod.fromInt(this.rescheduleStrategyMethod);
    }

    public RecalculationFrequencyType getRestFrequencyType() {
        return RecalculationFrequencyType.fromInt(this.restFrequencyType);
    }

    public Integer getRestInterval() {
        return this.restInterval;
    }

    public RecalculationFrequencyType getCompoundingFrequencyType() {
        return RecalculationFrequencyType.fromInt(this.compoundingFrequencyType);
    }

    public Integer getCompoundingInterval() {
        return this.compoundingInterval;
    }

    public Integer getRestFrequencyNthDay() {
        return this.restFrequencyNthDay;
    }

    public Integer getRestFrequencyWeekday() {
        return this.restFrequencyWeekday;
    }

    public Integer getRestFrequencyOnDay() {
        return this.restFrequencyOnDay;
    }

    public Integer getCompoundingFrequencyNthDay() {
        return this.compoundingFrequencyNthDay;
    }

    public Integer getCompoundingFrequencyWeekday() {
        return this.compoundingFrequencyWeekday;
    }

    public Integer getCompoundingFrequencyOnDay() {
        return this.compoundingFrequencyOnDay;
    }
    
    public boolean isCompoundingToBePostedAsTransaction(){
    	return null == this.isCompoundingToBePostedAsTransaction? false 
    			: this.isCompoundingToBePostedAsTransaction;
    }
}
