import search.SearchQuery
import groovy.xml.MarkupBuilder
import search.SearchQueryGroup
import auth.*;

def user = RsUser.findByUsername(params.rsUser);
def defaultGroup = SearchQueryGroup.get(name: "Default", user: user);
if (defaultGroup == null) {
    SearchQueryGroup.add(name: "Default", user: user);
}

def queryGroups = SearchQueryGroup.list();
def writer = new StringWriter();
def queryBuilder = new MarkupBuilder(writer);
queryBuilder.Filters
{
    queryGroups.each {SearchQueryGroup group ->
        if (group.user.username.equals( user.username)) {
            queryBuilder.Filter(id: group.id, name: group.name, nodeType: "group")
                    {
                        group.queries.each {SearchQuery query ->
                            queryBuilder.Filter(id: query.id, name: query.name, nodeType: "filter", query: query.query)
                        }
                    }
        }

    }
}

return writer.toString();