import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.converter.RapidConvertUtils
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rcmdb.domain.property.RelationUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils


// ---------------------------------------
// CONFIGURATION STARTS


ID_RELATION_MAPPING=[:];
ID_RELATION_MAPPING["message.RsMessageRule.searchQueryId"]=search.SearchQuery;
ID_RELATION_MAPPING["message.RsMessageRule.calendarId"]=message.RsMessageRuleCalendar;
ID_RELATION_MAPPING["search.SearchQuery.parentQueryId"]=search.SearchQuery;
ID_RELATION_MAPPING["auth.RsUserInformation.userId"]=auth.RsUser;
ID_RELATION_MAPPING["auth.ChannelUserInformation.userId"]=auth.RsUser;
//ID_RELATION_MAPPING["auth.LdapUserInformation.userId"]=auth.RsUser;


// CONFIGURATION ENDS
// ---------------------------------------

redundancyUtility=application.RapidApplication.getUtility("RedundancyUtility");
withRelations=params.withRelations?true:false;



SEARCH_ERROR="";
def searchResults = search(params);
if (searchResults == null) {
	return ControllerUtils.convertErrorToXml(SEARCH_ERROR);
}

StringWriter sw = new StringWriter();
def builder = new MarkupBuilder(sw);
def sortOrder = 0;
def lastRsUpdatedAt=0;
//since RealUpdatedObjects request searches UpdatesObjects but shows , Real Model Data SearchQuery , MessageRule etc
// we should save real rsUpdatedAt from the searched objects (UpdatesObjects) 
if(searchResults.results.size()>0)
{
    lastRsUpdatedAt=searchResults.results[searchResults.results.size()-1].rsUpdatedAt;
}
builder.Objects(total: searchResults.total, offset: searchResults.offset,rsUpdatedAt:lastRsUpdatedAt) {
    searchResults.results.each { objectProps ->
        def props=[:]
        if(params.searchIn=="RealUpdatedObjects")
        {
           GrailsDomainClass grailsClass = ApplicationHolder.application.getDomainClass(objectProps.modelName);
           def mc = grailsClass.metaClass;
           def realObjectQuery="id:${objectProps.objectId}";
           def realSearchResults=mc.theClass.searchAsString(realObjectQuery).results;
           if(realSearchResults.size()==1)
           {
               props=realSearchResults[0];                
           }
        }
        else
        {
            props=objectProps;
        }


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
	            else if(relatedObjects == null)
                {
                   relations[relName]=[];
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
    def searchModelName=params.searchIn;
    if (searchModelName == "RealUpdatedObjects") {
        searchModelName="UpdatedObjects";
    }
    GrailsDomainClass grailsClass = ApplicationHolder.application.getDomainClass(searchModelName);
    if (grailsClass == null)
    {
        SEARCH_ERROR="No such model : ${searchModelName}";
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


    return searchResults;
}