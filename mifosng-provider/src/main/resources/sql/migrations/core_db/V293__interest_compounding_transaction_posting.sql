ALTER TABLE `m_product_loan_recalculation_details`
	ADD COLUMN `is_compounding_to_be_posted_as_transaction` TINYINT(1) NOT NULL DEFAULT '0';
ALTER TABLE `m_loan_recalculation_details`
	ADD COLUMN `is_compounding_to_be_posted_as_transaction` TINYINT(1) NOT NULL DEFAULT '0';	