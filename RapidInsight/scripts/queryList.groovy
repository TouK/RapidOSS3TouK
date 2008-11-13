/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Nov 12, 2008
 * Time: 10:50:39 AM
 */
import auth.RsUser
import groovy.xml.MarkupBuilder
import search.SearchQuery
import search.SearchQueryGroup

def filterType = params.type;
def user = RsUser.findByUsername(web.session.username);
if(user == null){
    throw new Exception("User ${web.session.username} does not exist");
}

def writer = new StringWriter();
def queryBuilder = new MarkupBuilder(writer);

SearchQueryGroup.add(name:"My Queries", username:web.session.username, type:"default");
def queryGroups = SearchQueryGroup.searchEvery("type:\"${filterType}\" OR type:\"default\"");
queryBuilder.Filters
{
    queryGroups.each {SearchQueryGroup group ->
        def userName = group.username;
        if((userName.equals(RsUser.RSADMIN) && group.isPublic) || userName.equals(user.username)){
           queryBuilder.Filter(id: group.id, name: group.name, nodeType: "group",  isPublic:group.isPublic) {
              group.queries.each {SearchQuery query ->
                  if(query.type == filterType || query.type == ""){
                        queryBuilder.Filter(id: query.id, name: query.name, nodeType: "filter", viewName:query.viewName, group:group.name,
                                query: query.query, sortProperty: query.sortProperty, sortOrder: query.sortOrder, isPublic:query.isPublic)
                  }

              }
           }
        }
    }
}
return writer.toString();