import datasource.NetcoolDatasource

class NetcoolEventOperations extends com.ifountain.rcmdb.domain.AbstractDomainOperation
{
	def propsThatCantBeChanged = ["identifier","serverserial","serial"];
	
    public void updateAtNc(Map params){
	    def tempParams = [:];
	    def ncds = NetcoolDatasource.get(name:servername);
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
	    def ncds = NetcoolDatasource.get(name:servername);
	    ncds.removeEvent(serverserial);
    }
    
    def void setIdentifier(val){
    }

	def void setServerserial(val){
    }

    def void setSerial(val){
    }
        
    def void setLocation(val){
	    updateAtNc(["location":val]);
	    location = val;
    }
    
    def void setSeverity(val){
	    updateAtNc(["severity":val]);
	    severity = val;
    }
    
    def void setNode(val){
	    updateAtNc(["node":val]);
	    node = val;
    }
    
    def void setNetcoolclass(val){
	    updateAtNc(["netcoolclass":val]);
	    netcoolclass = val;
    }
 
    def void setSuppressescl(val){
	    updateAtNc(["suppressescl":val]);
	    suppressescl = val;
    }
    
    def void setTasklist(val){
	    updateAtNc(["tasklist":val]);
	    tasklist = val;
    }
    
    def void setAcknowledged(val){
	    updateAtNc(["acknowledged":val]);
	    acknowledged = val;
    }
    
    def void setOwneruid(val){
	    updateAtNc(["owneruid":val]);
	    owneruid = val;
    }

    def void setOwnergid(val){
	    updateAtNc(["ownergid":val]);
	    ownergid = val;
    } 
    
    def void setAgent(val){
	    updateAtNc(["agent":val]);
	    agent = val;
    }      
     
	def void setLocalrootobj(val){
	    updateAtNc(["localrootobj":val]);
	    localrootobj = val;
    }      
    
    def void setX733specificprob(val){
	    updateAtNc(["x733specificprob":val]);
	    x733specificprob = val;
    }      
    
    def void setEventid(val){
	    updateAtNc(["eventid":val]);
	    eventid = val;
    }      
    
    def void setNmosobjinst(val){
	    updateAtNc(["nmosobjinst":val]);
	    nmosobjinst = val;
    }      
    
    def void setLocalsecobj(val){
	    updateAtNc(["localsecobj":val]);
	    localsecobj = val;
    }      
    
    def void setAlertgroup(val){
	    updateAtNc(["alertgroup":val]);
	    alertgroup = val;
    }      
    
    def void setUrl(val){
	    updateAtNc(["url":val]);
	    url = val;
    }      
    
    def void setX733corrnotif(val){
	    updateAtNc(["x733corrnotif":val]);
	    x733corrnotif = val;
    }      
    
    def void setNmoscausetype(val){
	    updateAtNc(["nmoscausetype":val]);
	    nmoscausetype = val;
    }      
    
    def void setPhysicalcard(val){
	    updateAtNc(["physicalcard":val]);
	    physicalcard = val;
    }      
    
    def void setLocalnodealias(val){
	    updateAtNc(["localnodealias":val]);
	    localnodealias = val;
    } 
    
    def void setFirstoccurrence(val){
	    updateAtNc(["firstoccurrence":val]);
	    firstoccurrence = val;
    } 
    
    def void setLastoccurrence(val){
	    updateAtNc(["lastoccurrence":val]);
	    lastoccurrence = val;
    }      
    
    def void setX733eventtype(val){
	    updateAtNc(["x733eventtype":val]);
	    x733eventtype = val;
    } 
    
    def void setRemotenodealias(val){
	    updateAtNc(["remotenodealias":val]);
	    remotenodealias = val;
    } 
    
    def void setPhysicalslot(val){
	    updateAtNc(["physicalslot":val]);
	    physicalslot = val;
    } 
    
    def void setService(val){
	    updateAtNc(["service":val]);
	    service = val;
    } 
    
    def void setNmosserial(val){
	    updateAtNc(["nmosserial":val]);
	    nmosserial = val;
    } 
    
    def void setPhysicalport(val){
	    updateAtNc(["physicalport":val]);
	    physicalport = val;
    } 
    
    def void setRemotepriobj(val){
	    updateAtNc(["remotepriobj":val]);
	    remotepriobj = val;
    } 
    
    def void setFlash(val){
	    updateAtNc(["flash":val]);
	    flash = val;
    } 
    
    def void setType(val){
	    updateAtNc(["type":val]);
	    type = val;
    } 
    
    def void setLocalpriobj(val){
	    updateAtNc(["localpriobj":val]);
	    localpriobj = val;
    } 
    
    def void setProcessreq(val){
	    updateAtNc(["processreq":val]);
	    processreq = val;
    }
    
    def void setSummary(val){
	    updateAtNc(["summary":val]);
	    summary = val;
    }
    
    def void setRemoterootobj(val){
	    updateAtNc(["remoterootobj":val]);
	    remoterootobj = val;
    }

    def void setRemotesecobj(val){
	    updateAtNc(["remotesecobj":val]);
	    remotesecobj = val;
    }
    
    def void setX733probablecause(val){
	    updateAtNc(["x733probablecause":val]);
	    x733probablecause = val;
    }
    
    def void setStatechange(val){
	    updateAtNc(["statechange":val]);
	    statechange = val;
    }
    
    def void setInternallast(val){
	    updateAtNc(["internallast":val]);
	    internallast = val;
    }
    
    def void setManager(val){
	    updateAtNc(["manager":val]);
	    manager = val;
    }
    
    def void setServername(val){
	    updateAtNc(["servername":val]);
	    servername = val;
    }
    
    def void setExpiretime(val){
	    updateAtNc(["expiretime":val]);
	    expiretime = val;
    }
    
    def void setNodealias(val){
	    updateAtNc(["nodealias":val]);
	    nodealias = val;
    }
    
    def void setCustomer(val){
	    updateAtNc(["customer":val]);
	    customer = val;
    }
    
    def void setPoll(val){
	    updateAtNc(["poll":val]);
	    poll = val;
    }
    
    def void setGrade(val){
	    updateAtNc(["grade":val]);
	    grade = val;
    }
    
    def void setTally(val){
	    updateAtNc(["tally":val]);
	    tally = val;
    }
    
    public void setSeverity(newValue, userName){
	    def ncds = NetcoolDatasource.get(name:servername);
	    ncds.setSeverityAction(serverserial, newValue, userName);
	    severity = newValue;
	    acknowledged = 0;		
    }
   
    public void setSuppressescl(newValue, userName){
	    def ncds = NetcoolDatasource.get(name:servername);
	    ncds.suppressAction(serverserial, newValue, userName);
	    suppressescl = newValue;
    }

    public void addToTaskList(boolean action){
	    def ncds = NetcoolDatasource.get(name:servername);
	    ncds.taskListAction(serverserial,action);
	    tasklist = action?"1":"0";
    }
    
    public void acknowledge(boolean action, userName){
	    def ncds = NetcoolDatasource.get(name:servername);
	    ncds.acknowledgeAction(serverserial, action, userName);
	    acknowledged = action?"1":"0";
    }
    
    public void assign(userId){
	 	def ncds = NetcoolDatasource.get(name:servername);
	 	ncds.assignAction(serverserial, userId);
	 	owneruid = userId;
	 	acknowledged = 0;		

    }
}