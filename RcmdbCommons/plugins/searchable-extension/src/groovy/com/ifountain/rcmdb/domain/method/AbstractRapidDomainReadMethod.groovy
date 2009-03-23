package com.ifountain.rcmdb.domain.method
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 23, 2009
 * Time: 8:56:20 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractRapidDomainReadMethod extends AbstractRapidDomainMethod{

    public AbstractRapidDomainReadMethod(MetaClass mc) {
        super(mc); //To change body of overridden methods use File | Settings | File Templates.
    }

    public final Object invoke(Object domainObject, Object[] arguments) {
        return _invoke(domainObject, arguments);
    }
    abstract protected Object _invoke(Object domainObject, Object[] arguments);
}