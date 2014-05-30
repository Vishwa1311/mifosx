package org.mifosplatform.portfolio.charge.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentTypeChargeRepository extends JpaRepository<PaymentTypeCharge, Long>, JpaSpecificationExecutor<PaymentTypeCharge> {

}