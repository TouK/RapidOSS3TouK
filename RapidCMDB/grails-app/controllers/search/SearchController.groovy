package search

import org.compass.core.engine.SearchEngineQueryParseException
import org.jsecurity.SecurityUtils
import auth.RsUser

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
        else if(params.submitBtn == "Delete Saved Query")
        {
            return deleteQuery(params);
        }
        else if(params.queryId)
        {
            params.q = SearchQuery.get(params.queryId).query;
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
        def res = propertyConfiguration[className];
        return res?res:[];
    }

    def static _reloadPropertyConfiguration()
    {
        def content = new File("SmartsSearchPropertyConfiguration.txt").getText();
        propertyConfiguration = Eval.me(content);    
    }

    def reloadPropertyConfiguration = {
        _reloadPropertyConfiguration();
        redirect(action: index);
    }

    def deleteQuery(params){
        if(params.queryId)
        {
            SearchQuery.get(params.queryId)?.delete(flush:true);
        }
        def currentUser = RsUser.findByUsername(session.username);
        return [savedQueries:currentUser.queries];
    }

    def search(params)
    {
        def currentUser = RsUser.findByUsername(session.username);
        def res = [savedQueries:currentUser.queries];
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
            def rsUser = RsUser.findByUsername(session.username);
            SearchQuery query = new SearchQuery(user:rsUser, query:params.q);
            query.save();
            return search(params);

        }
    }
}