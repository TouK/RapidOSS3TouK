public class RedundancyUtility
{
	public static def getKeySearchQueryForObject(alias,relatedObject)
	{	
		def domainClass=org.codehaus.groovy.grails.commons.ApplicationHolder.application.getDomainClass(alias);
		def domainKeys=com.ifountain.rcmdb.domain.util.DomainClassUtils.getKeys(domainClass);
		def searchProps=[:];
		if(domainKeys.size()==0)
		{
			searchProps["id"]=relatedObject["id"];                		
		}
		else
		{
			domainKeys.each{ keyPropName ->	                			
				searchProps[keyPropName]=relatedObject[keyPropName];
			}
		}
		def searchQuery="";
		searchProps.each{ searchPropName , searchPropVal ->
			if(searchPropVal instanceof String)
			{
				searchQuery+="${searchPropName}:${searchPropVal.exactQuery()} ";
			}
			else
			{
				searchQuery+="${searchPropName}:${searchPropVal} ";
			}
		}
		return searchQuery;
	}
	
	public static def objectInBeforeInsert(object)
	{
		def isRemoteActivated=com.ifountain.rcmdb.util.ExecutionContextManagerUtils.getObjectFromCurrentContext("isRemote")!=null;
    	if(isRemoteActivated)
    	{
    		object.setProperty("isLocal",false);
    	}
    	else
    	{
    		object.setProperty("isLocal",true);
    	}
	}
	public static def objectInBeforeUpdate(object)
	{
        objectInBeforeInsert(object);
    }
	public static def objectInAfterDelete(object)
	{
		def isRemoteActivated=com.ifountain.rcmdb.util.ExecutionContextManagerUtils.getObjectFromCurrentContext("isRemote")!=null;
		if(!isRemoteActivated)
		{
			def deletedObjectClass=application.RsApplication.getModelClass("DeletedObjects");
			def modelName=object.class.name;
			def searchQuery=application.RsApplication.getUtility("RedundancyUtility").getKeySearchQueryForObject(modelName,object);
			deletedObjectClass.add(modelName:modelName,searchQuery:searchQuery);
			
		}
	}
	
	
}