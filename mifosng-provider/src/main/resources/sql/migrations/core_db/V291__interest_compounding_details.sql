CREATE TABLE `m_loan_interest_recalculation_additional_details` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`loan_repayment_schedule_id` BIGINT NOT NULL,
	`effective_date` DATE NOT NULL,
	`amount` DECIMAL(19,6) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_additional_details_repayment_schedule_id` FOREIGN KEY (`loan_repayment_schedule_id`) REFERENCES `m_loan_repayment_schedule` (`id`)
);