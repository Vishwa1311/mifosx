
ALTER TABLE `m_loan` DROP `repayment_frequency_nth_day_enum`, DROP `repayment_frequency_day_of_week_enum`;

ALTER TABLE `m_product_loan_recalculation_details` DROP `rest_freqency_date`, DROP `compounding_freqency_date`;
ALTER TABLE `m_product_loan_recalculation_details` ADD `rest_frequency_nth_day_enum` INT(5), ADD `rest_frequency_on_day` INT(5), ADD `rest_frequency_weekday_enum` INT(5),
ADD `compounding_frequency_nth_day_enum` INT(5), ADD `compounding_frequency_on_day` INT(5), ADD `compounding_frequency_weekday_enum` INT(5);

ALTER TABLE `m_loan_recalculation_details` DROP `rest_freqency_date`, DROP `compounding_freqency_date`;
ALTER TABLE `m_loan_recalculation_details` ADD `rest_frequency_nth_day_enum` INT(5), ADD `rest_frequency_on_day` INT(5), ADD `rest_frequency_weekday_enum` INT(5),
ADD `compounding_frequency_nth_day_enum` INT(5), ADD `compounding_frequency_on_day` INT(5), ADD `compounding_frequency_weekday_enum` INT(5);