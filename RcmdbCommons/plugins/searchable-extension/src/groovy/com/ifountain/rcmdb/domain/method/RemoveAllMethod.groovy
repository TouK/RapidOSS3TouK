package com.ifountain.rcmdb.domain.method
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Aug 5, 2008
 * Time: 12:14:42 PM
 * To change this template use File | Settings | File Templates.
 */
class RemoveAllMethod extends AbstractRapidDomainStaticMethod{

    public RemoveAllMethod(MetaClass mc) {
        super(mc);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public boolean isWriteOperation() {
        return true;
    }

    protected Object _invoke(Class clazz, Object[] arguments) {
        while(true)
        {
            def searchRes = CompassMethodInvoker.search(mc, "alias:*", false);
            if(searchRes.results.isEmpty())
            {
                break;
            }
            else
            {
                searchRes.results.each{
                    it.remove();
                }
            }
        }
        return null;
    }

}