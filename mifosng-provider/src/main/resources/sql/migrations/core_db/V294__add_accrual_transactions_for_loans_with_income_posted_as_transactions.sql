ALTER TABLE `job`
	CHANGE COLUMN `name` `name` VARCHAR(100) NOT NULL AFTER `id`,
	CHANGE COLUMN `display_name` `display_name` VARCHAR(100) NOT NULL AFTER `name`;

INSERT INTO `job` (`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `job_key`, `initializing_errorlog`, `is_active`, `currently_running`, `updates_allowed`, `scheduler_group`, `is_misfired`) VALUES ('Add Accrual Transactions For Loans With Income Posted As Transactions', 'Add Accrual Transactions For Loans With Income Posted As Transactions', '0 1 0 1/1 * ? *', now(), 5, NULL, NULL, NULL, NULL, NULL, 1, 0, 1, 3, 0);

UPDATE `job` SET `task_priority`=6 WHERE  `name`='Update Non Performing Assets';
