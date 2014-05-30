package org.mifosplatform.portfolio.charge.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChargePaymentTypeRepository extends JpaRepository<ChargePaymentType, Long>, JpaSpecificationExecutor<ChargePaymentType> {

}