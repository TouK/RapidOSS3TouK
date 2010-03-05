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
		searchProps.keySet().sort().each{ searchPropName ->
		    def searchPropVal=searchProps[searchPropName]; 
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
	
	public static def objectInAfterInsert(object)
	{

        def isRemoteActivated=com.ifountain.rcmdb.util.ExecutionContextManagerUtils.getObjectFromCurrentContext("isRemote")!=null;
        def modelName=object.class.name;
        def updatedObjectsClass=application.RapidApplication.getModelClass("UpdatedObjects");
        
        if(!isRemoteActivated)   //on local update UpdatedObjects entry is saved
        {
            updatedObjectsClass.add(modelName:modelName,objectId:object.id);
    	}
    	else  //on remote update UpdatedObjects entry is deleted ( if exists )
        {
            updatedObjectsClass.get([modelName:modelName,objectId:object.id])?.remove();
        }
        //on any update DeletedObjects entry is deleted ( if exists )
        def deletedObjectsClass=application.RapidApplication.getModelClass("DeletedObjects");
        def searchQuery=application.RapidApplication.getUtility("RedundancyUtility").getKeySearchQueryForObject(modelName,object);
        deletedObjectsClass.get([modelName:modelName,searchQuery:searchQuery])?.remove();
	}
	public static def objectInAfterUpdate(object)
	{
        objectInAfterInsert(object);
    }
	public static def objectInAfterDelete(object)
	{
		def isRemoteActivated=com.ifountain.rcmdb.util.ExecutionContextManagerUtils.getObjectFromCurrentContext("isRemote")!=null;
		if(!isRemoteActivated)  //on local update DeletedObjects entry is saved
		{
			def deletedObjectsClass=application.RapidApplication.getModelClass("DeletedObjects");
			def modelName=object.class.name;
			def searchQuery=application.RapidApplication.getUtility("RedundancyUtility").getKeySearchQueryForObject(modelName,object);
			deletedObjectsClass.add(modelName:modelName,searchQuery:searchQuery);  			
		}
		//on delete UpdatedObjects entry is deleted ( if exists )
		def updatedObjectsClass=application.RapidApplication.getModelClass("UpdatedObjects");
		updatedObjectsClass.get([modelName:object.class.name,objectId:object.id])?.remove();
	}
	
	
}