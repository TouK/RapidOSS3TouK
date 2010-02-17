import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.converter.RapidConvertUtils
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rcmdb.domain.property.RelationUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils


redundancyUtility=application.RsApplication.getUtility("RedundancyUtility");

withRelations=params.withRelations?true:false;

ID_RELATION_MAPPING=[:];
ID_RELATION_MAPPING["message.RsMessageRule.userId"]=auth.RsUser;
ID_RELATION_MAPPING["message.RsMessageRule.searchQueryId"]=search.SearchQuery;
ID_RELATION_MAPPING["auth.RsUserInformation.userId"]=auth.RsUser;
ID_RELATION_MAPPING["auth.ChannelUserInformation.userId"]=auth.RsUser;
//ID_RELATION_MAPPING["auth.LdapUserInformation.userId"]=auth.RsUser;

//comma seperated property list can be given
if(params.propertyList != null)
{
    def propertyList = [];
    StringUtils.splitPreserveAllTokens(params.propertyList, ",").each {propName->
        propertyList.add(propName.trim());
    }
    params.propertyList = propertyList;
    if(!propertyList.contains("id"))
    {
        propertyList.add ("id")
    }

}

SEARCH_ERROR="";
def searchResults = search(params);
if (searchResults == null) {
	return ControllerUtils.convertErrorToXml(SEARCH_ERROR);
}

StringWriter sw = new StringWriter();
def builder = new MarkupBuilder(sw);
def sortOrder = 0;
builder.Objects(total: searchResults.total, offset: searchResults.offset) {
    searchResults.results.each {props ->
    	def relations=[:];  
    	def idRelations=[:];
    	//find related objects if relations is requested
    	if(withRelations)
    	{
	        def domainClass=ApplicationHolder.application.getDomainClass(props.alias);
	        def relationsMeta=DomainClassUtils.getRelations(domainClass);
	
	        
	        
	        relationsMeta.each{ relName , metaData ->                	
	            def relatedObjects=RelationUtils.getRelatedObjectsByObjectId(props.id,metaData)                    
	            if(relatedObjects instanceof Collection)
	            {
	            	if(relatedObjects.size()>0)
	            	{
	                	relations[relName]=relatedObjects;
	            	}
	            }
	            else
	            {
	            	relations[relName]=[relatedObjects];
	            }
	        }
    	}
    	//find id relations always
    	props.each{ propName , propVal ->
    		def relatedModel=ID_RELATION_MAPPING.get("${props.alias}.${propName}".toString());
    		if(relatedModel!=null)
    		{
    			def relatedObject=relatedModel.get(id:propVal);
    			if(relatedObject!=null)
    			{
    				idRelations[propName]=relatedObject;
    			}
    		}
    	}
    	
        builder.Object(props) {
        	//add relations if requested
        	if(withRelations)
        	{
	            relations.each{ relName , relatedObjects ->
	                relatedObjects.each{ relatedObject ->
	                    def relatedObjectProps=[:];
	                    //relatedObjectProps.putAll(relatedObject.asMap());
	                    relatedObjectProps.relationName=relName;
	                    relatedObjectProps.alias=relatedObject.class.name;
	                    relatedObjectProps.searchQuery=redundancyUtility.getKeySearchQueryForObject(relatedObject.class.name,relatedObject);
	                    builder.RelatedObject(relatedObjectProps);
	                }
	            }	            
        	}
        	//add id relations always
        	idRelations.each{ relName , relatedObject ->
				def relatedObjectProps=[:];
				relatedObjectProps.relationName=relName;
				relatedObjectProps.alias=relatedObject.class.name;    				
				relatedObjectProps.searchQuery=redundancyUtility.getKeySearchQueryForObject(relatedObject.class.name,relatedObject);
				builder.IdRelatedObject(relatedObjectProps);
        	}
        }

    }
}

return sw.toString();

def search(params) {
    def query = params.query;
    if (query == "" || query == null)
    {
        query = "alias:*";
    }
    if(params.max == null){
        params.max = "1000"
    }
    def searchResults;
    if (params.searchIn != null) {
        GrailsDomainClass grailsClass = ApplicationHolder.application.getDomainClass(params.searchIn);
        if (grailsClass == null)
        {
        	SEARCH_ERROR="No such model : ${params.searchIn}";            
            return;
        }
        def mc = grailsClass.metaClass;
        try {
            searchResults = mc.theClass.searchAsString(query, params)
        }
        catch (Throwable e) {
        	SEARCH_ERROR="invalid.search.query : ${query}, Reason : ${e.getMessage()}";            
            return;
        }

    }
    else {
    	SEARCH_ERROR="searchIn parameter is not specified";
        return;    
    }
    return searchResults;
}