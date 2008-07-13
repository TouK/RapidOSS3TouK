import search.SearchQuery
import groovy.xml.MarkupBuilder
import search.SearchQueryGroup

def queryGroups = SearchQueryGroup.list();
def writer = new StringWriter();
def queryBuilder = new MarkupBuilder(writer);
queryBuilder.Filters
{
    queryGroups.each{SearchQueryGroup group->
        queryBuilder.Filter(id:group.id, name:group.name, nodeType:"group")
        {
            group.queries.each{SearchQuery query->
                queryBuilder.Filter(id:query.id, name:query.name, nodeType:"filter", query:query.query)
            }
        }
    }
}

return writer.toString();