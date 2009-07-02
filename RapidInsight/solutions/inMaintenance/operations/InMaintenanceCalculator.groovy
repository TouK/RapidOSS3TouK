/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 21, 2009
 * Time: 2:40:59 PM
 * To change this template use File | Settings | File Templates.
 */
class InMaintenanceCalculator {

    static def eventInBeforeInsert(event){
        def inMaintenance=RsInMaintenance.isEventInMaintenance(event);
        event.setProperty("inMaintenance",inMaintenance);
    }
    static def eventInBeforeUpdate(event,changedProps){

    }

}