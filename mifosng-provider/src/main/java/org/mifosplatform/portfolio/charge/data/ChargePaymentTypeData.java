package org.mifosplatform.portfolio.charge.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class ChargePaymentTypeData {

    // private final Long id;
    private final BigDecimal amount;
    private final List<EnumOptionData> chargeCalculationTypeOptions;
    private final EnumOptionData chargeCalculationType;
    private final Collection<CodeValueData> paymentTypeOptions;
    private final CodeValueData paymentType;

    public static ChargePaymentTypeData template(final List<EnumOptionData> chargeCalculationTypeOptions,
            final Collection<CodeValueData> paymentTypeOptions) {

        return new ChargePaymentTypeData(null, null, chargeCalculationTypeOptions, null, paymentTypeOptions);
    }

    public static ChargePaymentTypeData withTemplate(final ChargePaymentTypeData chargePaymentType, final ChargePaymentTypeData template) {
        return new ChargePaymentTypeData(chargePaymentType.amount, chargePaymentType.chargeCalculationType,
                template.chargeCalculationTypeOptions, chargePaymentType.paymentType, template.paymentTypeOptions);
    }

    public static ChargePaymentTypeData instance(final BigDecimal amount, final EnumOptionData chargeCalculationType,
            final CodeValueData paymentType) {

        final List<EnumOptionData> chargeCalculationTypeOptions = null;
        final Collection<CodeValueData> paymentTypeOptions = null;

        return new ChargePaymentTypeData(amount, chargeCalculationType, chargeCalculationTypeOptions, paymentType, paymentTypeOptions);
    }

    private ChargePaymentTypeData(final BigDecimal amount, final EnumOptionData chargeCalculationType,
            final List<EnumOptionData> chargeCalculationTypeOptions, final CodeValueData paymentType,
            final Collection<CodeValueData> paymentTypeOptions) {
        this.amount = amount;
        this.chargeCalculationType = chargeCalculationType;
        this.chargeCalculationTypeOptions = chargeCalculationTypeOptions;
        this.paymentType = paymentType;
        this.paymentTypeOptions = paymentTypeOptions;
    }
}