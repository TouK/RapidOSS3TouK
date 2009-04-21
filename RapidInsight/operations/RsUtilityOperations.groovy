/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 16, 2009
 * Time: 2:39:03 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUtilityOperations  extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
    public static def getUtility(utilityName)
    {
        return RsUtilityOperations.class.classLoader.loadClass (utilityName);
    }

}