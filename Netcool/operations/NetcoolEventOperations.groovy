import com.ifountain.core.domain.annotations.*;
import datasource.*;

class NetcoolEventOperations  extends com.ifountain.rcmdb.domain.AbstractDomainOperation {
	
    public void syncNc(Map params){
	    def tempParams = [:];
	    tempParams.put("ServerSerial", serverserial);
	    params.each{prop, newValue->
	    	def propLowerCase = prop.toLowerCase();
	    	if (propLowerCase != "serverserial" && propLowerCase != "identifier"){
		    	def propNameinDs = NetcoolDatasource.NAMEMAP[propLowerCase];
		    	if (propNameinDs!=null){
					tempParams.put(propNameinDs,newValue);
				}
				else{
					throw new Exception("No such property ${prop}");		
				}
			}
			else{
				throw new Exception("Can not modify ServerSerial or Identifier");		
			}
	    }
	    NCDS.updateEvent(tempParams);
    }
    
    public void removeNc(){
	    NCDS.removeEvent(serverserial);
    }
    
    public void setProperty(String prop, Object newVal){
	    if (id!=null){ // THE INSTANCE IS NOT SAVED TO RCMDB YET, IE: setProperty() IS CALLED FOR NetcoolEvent.add()
		    def params = [:];
			params.put(prop,newVal);
			syncNc(params);
		}
		super.setProperty(prop, newVal);
    }
    
    def void setIdentifier(val){
	    if (id==null){
		    identifier = val;
	    }
    }

    def void setServername(val){
	    if (id==null){
		    servername = val;
    	    if (NCDS==null){
			    NCDS = NetcoolDatasource.get(name:val);
		    }
	    }
    }
    
	def void setServerserial(val){
		if (id==null){
		    serverserial = val;
	    }
    }

    def void setSerial(val){
	    if (id==null){
		    serial = val;
	    }
    }
        
    public void setSeverity(val, userName){
        def oldVal= NCDS.CONVERSIONMAP["Severity"+severity];
        def newVal = NCDS.CONVERSIONMAP["Severity"+val];
        String text = "Alert is prioritized from ${oldVal} to ${newVal} by ${userName}";
		NCDS.writeToJournal(serverserial, text);
		
	    severity = val;
	    acknowledged = 0;		
    }
   
    public void setSuppressescl(val, userName){
	    def oldVal= CONVERSIONMAP.get("SuppressEscl"+suppress);
        def newVal = CONVERSIONMAP.get("SuppressEscl"+val);
		String text = "Alert is prioritized from ${oldVal} to ${newVal} by ${userName}";

		NCDS.writeToJournal(serverserial, text);
	    suppressescl = val;
    }

    public void addToTaskList(boolean action){
	    tasklist = action?"1":"0";
    }
    
    public void acknowledge(boolean action, userName){
	    if(action){
            text = "Alert is acknowledged by " + userName;
		}
		else{
			text = "Alert is unacknowledged by " + userName;
		}
		NCDS.writeToJournal(serverserial, text);
		
	    acknowledged = action?"1":"0";
    }
    
    public void assign(userId){
		String text = "Event is assigned to $userId";
		NCDS.writeToJournal(serverserial, text);
		owneruid = userId;
	 	acknowledged = 0;		

    }
}
