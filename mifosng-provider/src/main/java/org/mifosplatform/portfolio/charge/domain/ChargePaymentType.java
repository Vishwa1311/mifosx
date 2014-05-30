package org.mifosplatform.portfolio.charge.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_charge_payment_type")
public class ChargePaymentType extends AbstractPersistable<Long> {
    
    @ManyToOne
    @JoinColumn(name = "charge_id", nullable = false)
    private Charge charge;

    @ManyToOne
    @JoinColumn(name = "payment_type_id", nullable = false)
    private CodeValue paymentType;
    
    @Column(name = "charge_calculation_type_enum", nullable = false)
    private ChargeCalculationType chargeCalculationType;
    
    @Column(name = "amount", scale = 6, precision = 19, nullable = false)
    private BigDecimal amount;
    
    public ChargePaymentType(final Charge charge, final CodeValue paymentType, final ChargeCalculationType chargeCalculationType, final BigDecimal amount) {
        this.charge = charge;
        this.paymentType = paymentType;
        this.chargeCalculationType = chargeCalculationType;
        this.amount = amount;
    }
    
    public Charge getCharge() {
        return this.charge;
    }
    
    public void setCharge(final Charge charge) {
        this.charge = charge;
    }
    
    public CodeValue getPaymentType() {
        return this.getPaymentType();
    }
    
    public void setPaymentType(final CodeValue paymentType) {
        this.paymentType = paymentType;
    }
    
    public ChargeCalculationType getChargeCalculationType() {
        return this.chargeCalculationType;
    }
    
    public void setChargeCalculationType(final ChargeCalculationType chargeCalculationType) {
        this.chargeCalculationType = chargeCalculationType;
    }
    
    public BigDecimal getAmount() {
        return this.amount;
    }
    
    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }
}