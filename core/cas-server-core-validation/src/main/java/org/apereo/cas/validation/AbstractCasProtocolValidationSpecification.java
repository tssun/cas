package org.apereo.cas.validation;

import org.apereo.cas.CasProtocolConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.servlet.http.HttpServletRequest;

/**
 * Base validation specification for the CAS protocol. This specification checks
 * for the presence of renew=true and if requested, succeeds only if ticket
 * validation is occurring from a new login.
 *
 * @author Scott Battaglia
 * @since 3.0.0
 */
@Scope(value = "prototype")
public abstract class AbstractCasProtocolValidationSpecification implements ValidationSpecification {
    protected transient Logger logger = LoggerFactory.getLogger(this.getClass());


    /** Denotes whether we should always authenticate or not. */
    private boolean renew;

    /**
     * Instantiates a new abstract cas protocol validation specification.
     */
    public AbstractCasProtocolValidationSpecification() {
    }

    /**
     * Instantiates a new abstract cas protocol validation specification.
     *
     * @param renew the renew
     */
    public AbstractCasProtocolValidationSpecification(final boolean renew) {
        this.renew = renew;
    }
    
    /**
     * Method to set the renew requirement.
     *
     * @param renew The renew value we want.
     */
    public void setRenew(final boolean renew) {
        this.renew = renew;
    }

    /**
     * Method to determine if we require renew to be true.
     *
     * @return true if renew is required, false otherwise.
     */
    public boolean isRenew() {
        return this.renew;
    }

    @Override
    public boolean isSatisfiedBy(final Assertion assertion, final HttpServletRequest request) {
        boolean satisfied = isSatisfiedByInternal(assertion);
        if (!satisfied) {
            logger.warn("{} is not satisfied by the produced assertion", getClass().getSimpleName());
            return false;
        }
        satisfied = !this.renew || assertion.isFromNewLogin();
        if (!satisfied) {
            logger.warn("{} is to enforce the [{}] CAS protocol behavior, yet the assertion is not issued from a new login",
                    getClass().getSimpleName(), CasProtocolConstants.PARAMETER_RENEW);
            return false;
        }
        return true;
    }

    @Override
    public void reset() {
        setRenew(false);
    }

    /**
     * Template method to allow for additional checks by subclassed methods
     * without needing to call super.isSatisfiedBy(...).
     * @param assertion the assertion
     * @return true, if the subclass implementation is satisfied by the assertion
     */
    protected abstract boolean isSatisfiedByInternal(Assertion assertion);
}
