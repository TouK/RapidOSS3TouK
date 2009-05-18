package message
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 18, 2009
 * Time: 3:50:37 PM
 * To change this template use File | Settings | File Templates.
 */
class RsMessageRuleOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    public static Map getDestinationConfig() {
        ////////////////////////// Match destination type with user channel information type where user destination is stored ////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        return ["email": "email"]
    }

    public static Set getDestinationNames() {
        def destinationConfig = getDestinationConfig();
        return new ArrayList(destinationConfig.keySet());
    }
}