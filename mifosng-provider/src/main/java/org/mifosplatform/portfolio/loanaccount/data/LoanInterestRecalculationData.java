/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.loanaccount.data;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.calendar.data.CalendarData;

public class LoanInterestRecalculationData {

    private final Long id;
    private final Long loanId;
    private final EnumOptionData interestRecalculationCompoundingType;
    private final EnumOptionData rescheduleStrategyType;
    @SuppressWarnings("unused")
    private final CalendarData calendarData;
    private final EnumOptionData recalculationRestFrequencyType;
    private final Integer recalculationRestFrequencyInterval;
    /*private final LocalDate recalculationRestFrequencyDate;*/
    private final EnumOptionData recalculationRestFrequencyNthDay;
    private final EnumOptionData recalculationRestFrequencyWeekday;
    private final Integer recalculationRestFrequencyOnDay;
    private final EnumOptionData recalculationCompoundingFrequencyType;
    private final Integer recalculationCompoundingFrequencyInterval;
    /*private final LocalDate recalculationCompoundingFrequencyDate;*/
    private final EnumOptionData recalculationCompoundingFrequencyNthDay;
    private final EnumOptionData recalculationCompoundingFrequencyWeekday;
    private final Integer recalculationCompoundingFrequencyOnDay;
    @SuppressWarnings("unused")
    private final CalendarData compoundingCalendarData;

    public LoanInterestRecalculationData(final Long id, final Long loanId, final EnumOptionData interestRecalculationCompoundingType,
            final EnumOptionData rescheduleStrategyType, final CalendarData calendarData,
            final EnumOptionData recalculationRestFrequencyType, final Integer recalculationRestFrequencyInterval,
            final EnumOptionData recalculationRestFrequencyNthDay,
            final EnumOptionData recalculationRestFrequencyWeekday, final Integer recalculationRestFrequencyOnDay, 
            final CalendarData compoundingCalendarData, final EnumOptionData recalculationCompoundingFrequencyType, 
            final Integer recalculationCompoundingFrequencyInterval,
            final EnumOptionData recalculationCompoundingFrequencyNthDay, final EnumOptionData recalculationCompoundingFrequencyWeekday, 
            final Integer recalculationCompoundingFrequencyOnDay) {
        this.id = id;
        this.loanId = loanId;
        this.interestRecalculationCompoundingType = interestRecalculationCompoundingType;
        this.rescheduleStrategyType = rescheduleStrategyType;
        this.calendarData = calendarData;
        this.recalculationRestFrequencyType = recalculationRestFrequencyType;
        this.recalculationRestFrequencyInterval = recalculationRestFrequencyInterval;
        /*this.recalculationRestFrequencyDate = recalculationRestFrequencyDate;*/
        this.recalculationRestFrequencyNthDay = recalculationRestFrequencyNthDay;
        this.recalculationRestFrequencyWeekday = recalculationRestFrequencyWeekday;
        this.recalculationRestFrequencyOnDay = recalculationRestFrequencyOnDay;
        this.recalculationCompoundingFrequencyType = recalculationCompoundingFrequencyType;
        this.recalculationCompoundingFrequencyInterval = recalculationCompoundingFrequencyInterval;
        /*this.recalculationCompoundingFrequencyDate = recalculationCompoundingFrequencyDate;*/
        this.recalculationCompoundingFrequencyNthDay = recalculationCompoundingFrequencyNthDay;
        this.recalculationCompoundingFrequencyWeekday = recalculationCompoundingFrequencyWeekday;
        this.recalculationCompoundingFrequencyOnDay = recalculationCompoundingFrequencyOnDay;
        this.compoundingCalendarData = compoundingCalendarData;
    }

    public static LoanInterestRecalculationData withCalendarData(final LoanInterestRecalculationData recalculationData,
            final CalendarData calendarData, CalendarData compoundingCalendarData) {
        return new LoanInterestRecalculationData(recalculationData.id, recalculationData.loanId,
                recalculationData.interestRecalculationCompoundingType, recalculationData.rescheduleStrategyType, calendarData,
                recalculationData.recalculationRestFrequencyType, recalculationData.recalculationRestFrequencyInterval,
                recalculationData.recalculationRestFrequencyNthDay, 
                recalculationData.recalculationRestFrequencyWeekday, recalculationData.recalculationRestFrequencyOnDay, compoundingCalendarData,
                recalculationData.recalculationCompoundingFrequencyType, recalculationData.recalculationCompoundingFrequencyInterval,
                recalculationData.recalculationCompoundingFrequencyNthDay,
                recalculationData.recalculationCompoundingFrequencyWeekday, recalculationData.recalculationCompoundingFrequencyOnDay);
    }

    public Long getId() {
        return this.id;
    }

}
