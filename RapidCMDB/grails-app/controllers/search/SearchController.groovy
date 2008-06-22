package search

import org.compass.core.engine.SearchEngineQueryParseException

/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Jun 1, 2008
* Time: 4:09:42 PM
* To change this template use File | Settings | File Templates.
*/
class SearchController {
    def searchableService;
    def static Map propertyConfiguration = null;
    def index = {
        if(params.submitBtn == "Save")
        {
            return save(params);
        }
        else if(params.submitBtn == "Search")
        {
            return search(params);
        }
        else if(params.submitBtn == "Remove Query")
        {
            return deleteQuery(params);
        }
        else if(params.queryId)
        {
            params.q = SearchQuery.get(id:params.queryId).query;
            return search(params);
        }
        else
        {
            return search(params);   
        }

    }

    public static List getPropertyConfiguration(String className)
    {
        if(propertyConfiguration == null)
        {
            _reloadPropertyConfiguration();
        }
        def res = propertyConfiguration[className]?propertyConfiguration[className].propertyList:null;
        return res?res:[];
    }

    public static String getLinkProperty(Object target)
    {
        def classPropConfiguration = getPropertyConfiguration(target.class.name);
        if(classPropConfiguration)
        {
            String link = "";
            classPropConfiguration.linkPropertyFormat.each
            {
                link += target[it] + " ";
            }
            if(link.length() == 0)
            {
                link = target.toString();
            }
            else
            {
                link = link.substring(0, link.length()-1);
            }
            return link;
        }
        return target.toString();
    }

    def static _reloadPropertyConfiguration()
    {
        def content = new File("SmartsSearchPropertyConfiguration.txt").getText();
        propertyConfiguration = Eval.me(content);
        if(!propertyConfiguration)
        {
            propertyConfiguration = [:]
        }
    }

    def reloadPropertyConfiguration = {
        _reloadPropertyConfiguration();
        redirect(action: index);
    }

    def deleteQuery(params){
        if(params.queryId)
        {
            SearchQuery.get(id:params.queryId)?.remove();
        }
        return [savedQueries:SearchQuery.findAllByUser(session.username)];
    }

    def search(params)
    {
        def queries = SearchQuery.findAllByUser(session.username)
        def res = [savedQueries:queries]; 
        if (!params.q?.trim()) {
            return res;
        }
        try {
            res["searchResult"] = searchableService.search(params.q, params); 
        } catch (SearchEngineQueryParseException ex) {
            res["parseException"] = true;
        }
        return res;
    }

    def save(params)
    {
        if (!params.q?.trim()) {
            return [:]
        }
        else
        {
            SearchQuery query = SearchQuery.add(user:session.username, query:params.q);
            return search(params);

        }
    }
}