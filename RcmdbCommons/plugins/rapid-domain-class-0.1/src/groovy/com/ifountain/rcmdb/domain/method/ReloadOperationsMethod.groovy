package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import com.ifountain.rcmdb.domain.operation.DomainOperationLoadException

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 8, 2008
 * Time: 6:10:15 PM
 * To change this template use File | Settings | File Templates.
 */
class ReloadOperationsMethod extends AbstractRapidDomainStaticMethod{
    List subclasses;
    DomainOperationManager manager;
    Object logger;
    public ReloadOperationsMethod(MetaClass mc, List subclasses, DomainOperationManager manager, Object logger) {
        super(mc);
        this.logger = logger;
        this.manager = manager;
        this.subclasses = subclasses;
    }

    public boolean isWriteOperation() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Object _invoke(Class clazz, Object[] arguments) {
        def reloadSubclasses = !(arguments != null && arguments.length == 1 && arguments[0] == false)
        try
        {
            manager.loadOperation();
            if(reloadSubclasses != false )
            {
                def lastObjectNeedsToBeReloaded = null;
                try
                {
                    subclasses.each{Class subClass->
                        lastObjectNeedsToBeReloaded = subClass.name;
                        subClass.metaClass.invokeStaticMethod (subClass, "reloadOperations", [false] as Object[]);
                    }
                }
                catch(DomainOperationLoadException exception)
                {
                    logger.info("Operations of child model ${lastObjectNeedsToBeReloaded} could not reloaded. Please fix the problem an retry reloading.", exception);
                    throw new RuntimeException("Operations of child model ${lastObjectNeedsToBeReloaded} could not reloaded. Please fix the problem an retry reloading. Reason:${exception.toString()}", exception)
                }
            }
        }
        catch(DomainOperationLoadException exception)
        {
            logger.info(exception.getMessage(), exception);
            throw exception;
        }
    }
}