import auth.RsUser
import groovy.xml.MarkupBuilder
import search.SearchQuery
import search.SearchQueryGroup

def user = RsUser.findByUsername(web.session.username);
if(user == null){
    throw new Exception("User ${web.session.username} does not exist");
}

def writer = new StringWriter();
def queryBuilder = new MarkupBuilder(writer);

SearchQueryGroup.add(name:"Default", username:web.session.username);
def queryGroups = SearchQueryGroup.list();
queryBuilder.Filters
{
    queryGroups.each {SearchQueryGroup group ->
        def userName = group.username;
        if((userName.equals(RsUser.RSADMIN) && group.isPublic) || userName.equals(user.username)){
           queryBuilder.Filter(id: group.id, name: group.name, nodeType: "group",  isPublic:group.isPublic) {
              group.queries.each {SearchQuery query ->
                  queryBuilder.Filter(id: query.id, name: query.name, nodeType: "filter", query: query.query, sortProperty: query.sortProperty, sortOrder: query.sortOrder, isPublic:query.isPublic)
              }
           }
        }
    }
}
return writer.toString();