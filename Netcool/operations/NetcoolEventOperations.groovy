import com.ifountain.core.domain.annotations.*;
import datasource.*;

class NetcoolEventOperations extends com.ifountain.rcmdb.domain.AbstractDomainOperation
{
	def propsThatCantBeChanged = ["identifier","serverserial","serial"];

    public void updateAtNc(Map params){
	    def tempParams = [:];
	    def ncds = NetcoolDatasource.get(name:connectorname);
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
	    def ncds = NetcoolDatasource.get(name:connectorname);
	    ncds.removeEvent(serverserial);
    }

    public void setSeverity(newValue, userName){
	    def ncds = NetcoolDatasource.get(name:connectorname);
        ncds.setSeverityAction(serverserial, NetcoolConversionParameter.getRealValue("Severity", newValue), userName);
        severity = newValue;
	    acknowledged = NetcoolConversionParameter.getConvertedValue("Acknowledged", 0);

    }

    public void setSuppressescl(newValue, userName){
	    def ncds = NetcoolDatasource.get(name:connectorname);
	    ncds.suppressAction(serverserial, NetcoolConversionParameter.getRealValue("SuppressEscl", newValue), userName);
	    suppressescl = newValue;
    }

    public void addToTaskList(boolean action){
	    def ncds = NetcoolDatasource.get(name:connectorname);
	    ncds.taskListAction(serverserial,action);
	    tasklist = action?1:0;
    }

    public void acknowledge(boolean action, userName){
	    def ncds = NetcoolDatasource.get(name:connectorname);
	    ncds.acknowledgeAction(serverserial, action, userName);
        if(action)
        {
            acknowledged = NetcoolConversionParameter.getConvertedValue("Acknowledged", 1);
        }
        else
        {
            acknowledged = NetcoolConversionParameter.getConvertedValue("Acknowledged", 0);
        }
    }

    public void assign(newValue){
	 	def ncds = NetcoolDatasource.get(name:connectorname);
	 	ncds.assignAction(serverserial, NetcoolConversionParameter.getRealValue("OwnerUID", newValue));
	 	owneruid = newValue;
	 	acknowledged = NetcoolConversionParameter.getConvertedValue("Acknowledged", 0);

    }
}