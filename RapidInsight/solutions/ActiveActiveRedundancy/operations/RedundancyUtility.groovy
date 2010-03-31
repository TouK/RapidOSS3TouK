public class RedundancyUtility
{
	public static def getKeySearchQueryForObject(alias,relatedObjectProps)
	{
		def domainKeys=getKeyNamesOfObject(alias,relatedObjectProps);
		return getKeySearchQueryForObjectWithKeys(domainKeys,relatedObjectProps);
	}
	public static def getKeySearchQueryForObjectWithKeys(domainKeys,relatedObjectProps)
	{
		def searchProps=[:];
		if(domainKeys.size()==0)
		{
			searchProps["id"]=relatedObjectProps["id"];
		}
		else
		{
			domainKeys.each{ keyPropName ->
				searchProps[keyPropName]=relatedObjectProps[keyPropName];
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
	public static def getKeyNamesOfObject(alias,relatedObject)
    {
        def domainClass=org.codehaus.groovy.grails.commons.ApplicationHolder.application.getDomainClass(alias);
		return com.ifountain.rcmdb.domain.util.DomainClassUtils.getKeys(domainClass);
    }
	
	public static def objectInAfterInsert(object)
	{

        def isRemoteActivated=com.ifountain.rcmdb.util.ExecutionContextManagerUtils.getObjectFromCurrentContext("isRemote")!=null;
        def modelName=object.class.name;
        def updatedObjectsClass=application.RapidApplication.getModelClass("UpdatedObjects");
        
        if(!isRemoteActivated)   //on local update UpdatedObjects entry is saved
        {
            //When same object is updated again since modelName and object id is not changed, updatedObjects record will not be updated
            //rsUpdatedAt given here to allow same object record to be updated again
            updatedObjectsClass.add(modelName:modelName,objectId:object.id,rsUpdatedAt:Date.now());
    	}
    	else  //on remote update UpdatedObjects entry is deleted ( if exists )
        {
            updatedObjectsClass.get([modelName:modelName,objectId:object.id])?.remove();
        }
        //on any update DeletedObjects entry is deleted ( if exists )
        def deletedObjectsClass=application.RapidApplication.getModelClass("DeletedObjects");
        def searchQuery=getKeySearchQueryForObject(modelName,object);
        deletedObjectsClass.get([modelName:modelName,searchQuery:searchQuery])?.remove();
	}
	public static def objectInAfterUpdate(object,params)
	{
        def isRemoteActivated=com.ifountain.rcmdb.util.ExecutionContextManagerUtils.getObjectFromCurrentContext("isRemote")!=null;
        def modelName=object.class.name;
        def updatedObjectsClass=application.RapidApplication.getModelClass("UpdatedObjects");
        def deletedObjectsClass=application.RapidApplication.getModelClass("DeletedObjects");

        if(!isRemoteActivated)   //on local update UpdatedObjects entry is saved
        {
            //When same object is updated again since modelName and object id is not changed, updatedObjects record will not be updated
            //rsUpdatedAt given here to allow same object record to be updated again
            updatedObjectsClass.add(modelName:modelName,objectId:object.id,rsUpdatedAt:Date.now());

            //If key properties are changed, a DeletedObjects should be created with old key property values
            def domainKeys=getKeyNamesOfObject(modelName,object);
            def updatedKeys=domainKeys.findAll{params.updatedProps.containsKey(it)};
            if(updatedKeys.size()>0)
            {
                 def oldKeyPropValues=[:]
                 domainKeys.each{ keyName ->
                    if(updatedKeys.contains(keyName))
                    {
                       oldKeyPropValues[keyName]=params.updatedProps[keyName];
                    }
                    else
                    {
                      oldKeyPropValues[keyName]=object[keyName];
                    }
                 }
                 def searchQueryForOldKeyValues=getKeySearchQueryForObjectWithKeys(domainKeys,oldKeyPropValues);
			     deletedObjectsClass.add(modelName:modelName,searchQuery:searchQueryForOldKeyValues);
            }
        }
    	else  //on remote update UpdatedObjects entry is deleted ( if exists )
        {
            updatedObjectsClass.get([modelName:modelName,objectId:object.id])?.remove();
        }
        //on any update DeletedObjects entry is deleted ( if exists )        
        def searchQuery=getKeySearchQueryForObject(modelName,object);
        deletedObjectsClass.get([modelName:modelName,searchQuery:searchQuery])?.remove();
    }
	public static def objectInAfterDelete(object)
	{
		def isRemoteActivated=com.ifountain.rcmdb.util.ExecutionContextManagerUtils.getObjectFromCurrentContext("isRemote")!=null;
		if(!isRemoteActivated)  //on local update DeletedObjects entry is saved
		{
			def deletedObjectsClass=application.RapidApplication.getModelClass("DeletedObjects");
			def modelName=object.class.name;
			def searchQuery=getKeySearchQueryForObject(modelName,object);
			deletedObjectsClass.add(modelName:modelName,searchQuery:searchQuery);  			
		}
		//on delete UpdatedObjects entry is deleted ( if exists )
		def updatedObjectsClass=application.RapidApplication.getModelClass("UpdatedObjects");
		updatedObjectsClass.get([modelName:object.class.name,objectId:object.id])?.remove();
	}
	
	
}