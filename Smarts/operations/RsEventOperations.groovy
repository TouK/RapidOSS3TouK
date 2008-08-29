
    
    class RsEventOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
    {
        public void acknowledge(boolean action, userName){
		    if(action)
	            update(acknowledged : true);
	        else
	            update(acknowledged : false);
	    }
	    public void setOwnership(boolean action, userName){
		    if(action)
	            update(owner : userName);
	        else
	            update(owner : "root");
	    }    
    }
    