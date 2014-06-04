/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.charge.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.joda.time.MonthDay;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.monetary.service.CurrencyReadPlatformService;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.charge.data.PaymentTypeChargeData;
import org.mifosplatform.portfolio.charge.domain.ChargeAppliesTo;
import org.mifosplatform.portfolio.charge.exception.ChargeNotFoundException;
import org.mifosplatform.portfolio.common.service.CommonEnumerations;
import org.mifosplatform.portfolio.common.service.DropdownReadPlatformService;
import org.mifosplatform.portfolio.paymentdetail.PaymentDetailConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ChargeReadPlatformServiceImpl implements ChargeReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final CurrencyReadPlatformService currencyReadPlatformService;
    private final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService;
    private final DropdownReadPlatformService dropdownReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;

    @Autowired
    public ChargeReadPlatformServiceImpl(final PlatformSecurityContext context,
            final CurrencyReadPlatformService currencyReadPlatformService,
            final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService, final RoutingDataSource dataSource,
            final DropdownReadPlatformService dropdownReadPlatformService, final CodeValueReadPlatformService codeValueReadPlatformService) {
        this.context = context;
        this.chargeDropdownReadPlatformService = chargeDropdownReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.currencyReadPlatformService = currencyReadPlatformService;
        this.dropdownReadPlatformService = dropdownReadPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
    }

    @Override
    @Cacheable(value = "charges", key = "T(org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat('ch')")
    public Collection<ChargeData> retrieveAllCharges() {
        this.context.authenticatedUser();

        final ChargeMapper rm = new ChargeMapper();

        final String sql = "select " + rm.chargeSchema() + " where c.is_deleted=0 order by c.name ";

        return this.jdbcTemplate.query(sql, rm, new Object[] {});
    }

    @Override
    public ChargeData retrieveCharge(final Long chargeId) {
        try {
            this.context.authenticatedUser();

            final ChargeMapper rm = new ChargeMapper();

            final String sql = "select " + rm.chargeSchema() + " where c.id = ? and c.is_deleted=0 ";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { chargeId });
        } catch (final EmptyResultDataAccessException e) {
            throw new ChargeNotFoundException(chargeId);
        }
    }

    @Override
    public ChargeData retrieveNewChargeDetails() {

        this.context.authenticatedUser();

        final Collection<CurrencyData> currencyOptions = this.currencyReadPlatformService.retrieveAllowedCurrencies();
        final List<EnumOptionData> allowedChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService.retrieveCalculationTypes();
        final List<EnumOptionData> allowedChargeAppliesToOptions = this.chargeDropdownReadPlatformService.retrieveApplicableToTypes();
        final List<EnumOptionData> allowedChargeTimeOptions = this.chargeDropdownReadPlatformService.retrieveCollectionTimeTypes();
        final List<EnumOptionData> chargePaymentOptions = this.chargeDropdownReadPlatformService.retrivePaymentModes();
        final List<EnumOptionData> loansChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveLoanCalculationTypes();
        final List<EnumOptionData> loansChargeTimeTypeOptions = this.chargeDropdownReadPlatformService.retrieveLoanCollectionTimeTypes();
        final List<EnumOptionData> savingsChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveSavingsCalculationTypes();
        final List<EnumOptionData> savingsChargeTimeTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveSavingsCollectionTimeTypes();
        final List<EnumOptionData> feeFrequencyOptions = this.dropdownReadPlatformService.retrievePeriodFrequencyTypeOptions();
        final Collection<CodeValueData> paymentTypeOptions = this.codeValueReadPlatformService
                .retrieveCodeValuesByCode(PaymentDetailConstants.paymentTypeCodeName);
        return ChargeData.template(currencyOptions, allowedChargeCalculationTypeOptions, allowedChargeAppliesToOptions,
                allowedChargeTimeOptions, chargePaymentOptions, loansChargeCalculationTypeOptions, loansChargeTimeTypeOptions,
                savingsChargeCalculationTypeOptions, savingsChargeTimeTypeOptions, feeFrequencyOptions, paymentTypeOptions);
    }

    @Override
    public Collection<ChargeData> retrieveLoanProductCharges(final Long loanProductId) {

        this.context.authenticatedUser();

        final ChargeMapper rm = new ChargeMapper();

        final String sql = "select " + rm.loanProductChargeSchema() + " where c.is_deleted=0 and c.is_active=1 and plc.product_loan_id=?";

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanProductId });
    }

    @Override
    public Collection<ChargeData> retrieveLoanProductCharges(final Long loanProductId, final Integer chargeTime) {

        this.context.authenticatedUser();

        final ChargeMapper rm = new ChargeMapper();

        final String sql = "select " + rm.loanProductChargeSchema()
                + " where c.is_deleted=0 and c.is_active=1 and plc.product_loan_id=? and c.charge_time_enum=?";

        return this.jdbcTemplate.query(sql, rm, new Object[] { loanProductId, chargeTime });
    }

    @Override
    public Collection<ChargeData> retrieveLoanApplicableCharges(final boolean feeChargesOnly, Integer[] excludeChargeTimes) {
        this.context.authenticatedUser();

        final ChargeMapper rm = new ChargeMapper();
        String excludeClause = "";
        Object[] params = new Object[] { ChargeAppliesTo.LOAN.getValue() };
        if (excludeChargeTimes != null && excludeChargeTimes.length > 0) {
            excludeClause = " and c.charge_time_enum not in(?) ";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < excludeChargeTimes.length; i++) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(excludeChargeTimes[i]);
            }
            params = new Object[] { ChargeAppliesTo.LOAN.getValue(), sb.toString() };
        }
        String sql = "select " + rm.chargeSchema() + " where c.is_deleted=0 and c.is_active=1 and c.charge_applies_to_enum=?"
                + excludeClause + " order by c.name ";
        if (feeChargesOnly) {
            sql = "select " + rm.chargeSchema()
                    + " where c.is_deleted=0 and c.is_active=1 and c.is_penalty=0 and c.charge_applies_to_enum=? " + excludeClause
                    + "order by c.name ";
        }

        return this.jdbcTemplate.query(sql, rm, params);
    }

    @Override
    public Collection<ChargeData> retrieveLoanApplicablePenalties() {
        this.context.authenticatedUser();

        final ChargeMapper rm = new ChargeMapper();

        final String sql = "select " + rm.chargeSchema()
                + " where c.is_deleted=0 and c.is_active=1 and c.is_penalty=1 and c.charge_applies_to_enum=? order by c.name ";
        return this.jdbcTemplate.query(sql, rm, new Object[] { ChargeAppliesTo.LOAN.getValue() });
    }

    private static final class ChargeMapper implements RowMapper<ChargeData> {

        public String chargeSchema() {
            return "c.id as id, c.name as name, c.amount as amount, c.currency_code as currencyCode, "
                    + "c.charge_applies_to_enum as chargeAppliesTo, c.charge_time_enum as chargeTime, "
                    + "c.charge_payment_mode_enum as chargePaymentMode, "
                    + "c.charge_calculation_enum as chargeCalculation, c.is_penalty as penalty, "
                    + "c.is_active as active, oc.name as currencyName, oc.decimal_places as currencyDecimalPlaces, "
                    + "oc.currency_multiplesof as inMultiplesOf, oc.display_symbol as currencyDisplaySymbol, "
                    + "oc.internationalized_name_code as currencyNameCode, c.fee_on_day as feeOnDay, c.fee_on_month as feeOnMonth, "
                    + "c.fee_interval as feeInterval, c.fee_frequency as feeFrequency,c.min_cap as minCap,c.max_cap as maxCap, c.applicable_to_all_products as applicableToAllProducts "
                    + "from m_charge c " + "join m_organisation_currency oc on c.currency_code = oc.code";
        }

        public String loanProductChargeSchema() {
            return chargeSchema() + " join m_product_loan_charge plc on plc.charge_id = c.id";
        }

        public String savingsProductChargeSchema() {
            return chargeSchema() + " join m_savings_product_charge spc on spc.charge_id = c.id";
        }

        @Override
        public ChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final BigDecimal amount = rs.getBigDecimal("amount");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDecimalPlaces = JdbcSupport.getInteger(rs, "currencyDecimalPlaces");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");

            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDecimalPlaces, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            final int chargeAppliesTo = rs.getInt("chargeAppliesTo");
            final EnumOptionData chargeAppliesToType = ChargeEnumerations.chargeAppliesTo(chargeAppliesTo);

            final int chargeTime = rs.getInt("chargeTime");
            final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTime);

            final int chargeCalculation = rs.getInt("chargeCalculation");
            final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(chargeCalculation);

            final int paymentMode = rs.getInt("chargePaymentMode");
            final EnumOptionData chargePaymentMode = ChargeEnumerations.chargePaymentMode(paymentMode);

            final boolean penalty = rs.getBoolean("penalty");
            final boolean active = rs.getBoolean("active");

            final Integer feeInterval = JdbcSupport.getInteger(rs, "feeInterval");
            EnumOptionData feeFrequencyType = null;
            final Integer feeFrequency = JdbcSupport.getInteger(rs, "feeFrequency");
            if (feeFrequency != null) {
                feeFrequencyType = CommonEnumerations.termFrequencyType(feeFrequency, "feeFrequency");
            }
            MonthDay feeOnMonthDay = null;
            final Integer feeOnMonth = JdbcSupport.getInteger(rs, "feeOnMonth");
            final Integer feeOnDay = JdbcSupport.getInteger(rs, "feeOnDay");
            if (feeOnDay != null) {
                feeOnMonthDay = new MonthDay(feeOnMonth, feeOnDay);
            }
            final BigDecimal minCap = rs.getBigDecimal("minCap");
            final BigDecimal maxCap = rs.getBigDecimal("maxCap");
            final boolean applicableToAllProducts = rs.getBoolean("applicableToAllProducts");

            return ChargeData.instance(id, name, amount, currency, chargeTimeType, chargeAppliesToType, chargeCalculationType,
                    chargePaymentMode, feeOnMonthDay, feeInterval, penalty, active, minCap, maxCap, feeFrequencyType,
                    applicableToAllProducts);
        }
    }

    @Override
    public Collection<ChargeData> retrieveSavingsAccountApplicableCharges(final boolean feeChargesOnly) {
        this.context.authenticatedUser();

        final ChargeMapper rm = new ChargeMapper();

        String sql = "select "
                + rm.chargeSchema()
                + " where c.is_deleted=0 and c.is_active=1 and c.charge_applies_to_enum=? and (c.applicable_to_all_products is null or c.applicable_to_all_products = 0 ) order by c.name ";
        if (feeChargesOnly) {
            sql = "select "
                    + rm.chargeSchema()
                    + " where c.is_deleted=0 and c.is_active=1 and c.is_penalty=0 and c.charge_applies_to_enum=? and (c.applicable_to_all_products is null or c.applicable_to_all_products = 0 ) order by c.name ";
        }

        return this.jdbcTemplate.query(sql, rm, new Object[] { ChargeAppliesTo.SAVINGS.getValue() });
    }

    @Override
    public Collection<ChargeData> retrieveSavingsAccountApplicablePenalties() {
        this.context.authenticatedUser();

        final ChargeMapper rm = new ChargeMapper();

        final String sql = "select " + rm.chargeSchema()
                + " where c.is_deleted=0 and c.is_active=1 and c.is_penalty=1 and c.charge_applies_to_enum=? order by c.name ";
        return this.jdbcTemplate.query(sql, rm, new Object[] { ChargeAppliesTo.SAVINGS.getValue() });
    }

    @Override
    public Collection<ChargeData> retrieveSavingsProductCharges(final Long savingsProductId) {
        this.context.authenticatedUser();

        final ChargeMapper rm = new ChargeMapper();

        final String sql = "select " + rm.savingsProductChargeSchema()
                + " where c.is_deleted=0 and c.is_active=1 and spc.savings_product_id=?";

        return this.jdbcTemplate.query(sql, rm, new Object[] { savingsProductId });
    }

    @Override
    public Collection<PaymentTypeChargeData> retrievePaymentTypeCharges(Long chargeId) {
        this.context.authenticatedUser();

        final PaymentTypeChargeMapper rm = new PaymentTypeChargeMapper();

        final String sql = "select " + rm.paymentTypeChargeSchema() + " where ptc.charge_id = ? ";

        return this.jdbcTemplate.query(sql, rm, new Object[] { chargeId });
    }

    private static final class PaymentTypeChargeMapper implements RowMapper<PaymentTypeChargeData> {

        public String paymentTypeChargeSchema() {
            return " ptc.id as id, ptc.charge_id as chargeId, cv.id as paymentTypeId, cv.code_value as codeValue, "
                    + " ptc.charge_calculation_type_enum as chargeCalculationTypeEnum, ptc.amount as amount "
                    + " from m_payment_type_charge ptc left join m_code_value cv on cv.id=ptc.payment_type_id";
        }

        @Override
        public PaymentTypeChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final Long id = rs.getLong("id");
            final Long chargeId = rs.getLong("chargeId");
            final Long paymentTypeId = JdbcSupport.getLong(rs, "paymentTypeId");
            final String paymentTypeCode = rs.getString("codeValue");
            final int chargeCalculation = rs.getInt("chargeCalculationTypeEnum");
            final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(chargeCalculation);
            final BigDecimal amount = rs.getBigDecimal("amount");
            final CodeValueData paymentType = CodeValueData.instance(paymentTypeId, paymentTypeCode);
            return PaymentTypeChargeData.instance(id, chargeId, paymentType, chargeCalculationType, amount);
        }
    }

}