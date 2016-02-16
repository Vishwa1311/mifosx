package org.mifosplatform.organisation.workingdays.domain;

import java.math.BigDecimal;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.mifosplatform.organisation.workingdays.domain.WorkingDays;
import org.mifosplatform.portfolio.calendar.domain.Calendar;
import org.mifosplatform.portfolio.calendar.domain.CalendarFrequencyType;
import org.mifosplatform.portfolio.calendar.domain.CalendarType;
import org.mifosplatform.portfolio.calendar.service.CalendarUtils;
import net.fortuna.ical4j.model.Recur;


public class Test {
	
	public static void main(String[] args) {
		
		/*BigDecimal numberOfPeriods = BigDecimal.ZERO;
		LocalDate startDateAfterConsideringMonths = null;
        LocalDate endDateAfterConsideringMonths = null;
        WorkingDays workingDays = new WorkingDays("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR,SA,SU", 2, false);
        
        
		LocalDate startDate = new LocalDate(2015, 3, 31);
		LocalDate endDate = new LocalDate(2015, 4, 30);
		Integer repaymentEvery = 1;
		Calendar loanCalendar = Calendar.createRepeatingCalendar("", startDate, CalendarType.COLLECTION.getValue(), CalendarFrequencyType.MONTHLY, -1, null, -1);
		
		
		int numberOfMonths = Months.monthsBetween(startDate, endDate).getMonths();
		
		startDateAfterConsideringMonths = CalendarUtils.getNewRepaymentMeetingDate(loanCalendar.getRecurrence(), startDate.plusDays(1), 
				startDate.plusDays(1), repaymentEvery, 
				Recur.MONTHLY, workingDays);
		System.out.println(startDateAfterConsideringMonths);
		endDateAfterConsideringMonths = CalendarUtils.getNewRepaymentMeetingDate(loanCalendar.getRecurrence(), loanCalendar.getStartDateLocalDate(), 
				startDate.plusMonths(numberOfMonths), repaymentEvery, 
				Recur.MONTHLY, workingDays);
		int daysLeftAfterMonths = Days.daysBetween(startDate, endDateAfterConsideringMonths).getDays();
        int daysInPeriodAfterMonths = Days.daysBetween(startDateAfterConsideringMonths, endDateAfterConsideringMonths).getDays();
        numberOfPeriods = numberOfPeriods.add(BigDecimal.valueOf(numberOfMonths)).add(
                BigDecimal.valueOf((double) daysLeftAfterMonths / daysInPeriodAfterMonths));*/

	}

}
