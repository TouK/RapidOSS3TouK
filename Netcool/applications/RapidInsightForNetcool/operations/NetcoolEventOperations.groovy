import com.ifountain.core.domain.annotations.*;
import datasource.*
import connector.NetcoolConnector

class NetcoolEventOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
	def propsThatCantBeChanged = ["identifier","serverserial","serial"];

    public void updateAtNc(Map params){
	    def tempParams = [:];
	    def ncds = getDatasource(rsDatasource);
	    tempParams.put("ServerSerial", serverserial);
	    params.each{prop, newValue->
	    	def propLowerCase = prop.toLowerCase();
	    	if (propLowerCase != "serverserial" && propLowerCase != "identifier"){
		    	def propNameinDs = NetcoolDatasource.NAMEMAP[propLowerCase];
		    	if (propNameinDs!=null){
					tempParams.put(propNameinDs,newValue);
					propLowerCase = newValue;
				}
				else{
					throw new Exception("No such property ${prop}");
				}
			}
			else{
				throw new Exception("Can not modify ServerSerial or Identifier");
			}


	    }
	    ncds.updateEvent(tempParams);
    }

    public void removeFromNc(){
	    def ncds = getDatasource(rsDatasource);
	    ncds.removeEvent(serverserial);
    }

    public void setSeverity(newValue, userName){
	    def ncds = getDatasource(rsDatasource);
        ncds.setSeverityAction(serverserial, newValue, userName);
        update(severity : newValue);
	    update(acknowledged:NetcoolConversionParameter.getConvertedValue("Acknowledged", 0));

    }

    public void setSuppressescl(newValue, userName){
	    def ncds = getDatasource(rsDatasource);
	    ncds.suppressAction(serverserial,  newValue, userName);
	    update(state : newValue);
    }

    public void addToTaskList(boolean action){
	    def ncds = getDatasource(rsDatasource);
	    ncds.taskListAction(serverserial,action);
	    update(tasklist : action?1:0);
    }

    public void acknowledge(boolean action, userName){
	    def ncds = getDatasource(rsDatasource);
	    ncds.acknowledgeAction(serverserial, action, userName);
        if(action)
        {
            update(acknowledged : NetcoolConversionParameter.getConvertedValue("Acknowledged", 1));
        }
        else
        {
            update(acknowledged : NetcoolConversionParameter.getConvertedValue("Acknowledged", 0));
        }
    }

    public void assign(newValue){
	 	def ncds = getDatasource(rsDatasource);
	 	ncds.assignAction(serverserial, NetcoolConversionParameter.getRealValue("OwnerUID", newValue));
	 	update(owneruid : newValue);
	 	update(acknowledged : NetcoolConversionParameter.getConvertedValue("Acknowledged", 0));

    }

    def getDatasource(rsDatasource){
        def datasourceName = NetcoolConnector.getDatasourceName(rsDatasource);
        return NetcoolDatasource.get(name:datasourceName);
    }
}