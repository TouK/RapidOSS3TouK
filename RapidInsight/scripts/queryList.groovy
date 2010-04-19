import auth.RsUser
import groovy.xml.MarkupBuilder
import search.SearchQuery
import search.SearchQueryGroup

/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Nov 12, 2008
 * Time: 10:50:39 AM
 */
def filterType = params.type;
def username = web.session.username;

def writer = new StringWriter();
def builder = new MarkupBuilder(writer);

SearchQueryGroup.add(name: SearchQueryGroup.MY_QUERIES, username: username, type: SearchQueryGroup.DEFAULT_TYPE, expanded: true);
def queryGroups = SearchQueryGroup.getVisibleGroups(username, filterType)
def queryHierarchy = [:]
queryGroups.each {group ->
    def currentHierarchyMap = [:]
    queryHierarchy[group] = currentHierarchyMap;
    def queries = SearchQuery.getVisibleQueries(group, username, filterType)
    populateQueryHierarchy(0, currentHierarchyMap, queries);
}

builder.Filters
{
    queryHierarchy.each {SearchQueryGroup group, Map subQueries ->
        builder.Filter(id: group.id, name: group.name, nodeType: "group", isPublic: group.isPublic, expanded: group.expanded) {
            constructSubqueryXML(builder, subQueries, group.name)
        }
    }
}

def populateQueryHierarchy(parentQueryId, hierarchyMap, queries) {
    def currentLevelQueries = queries.findAll {it.parentQueryId == parentQueryId};
    currentLevelQueries.each {query ->
        def currentHierarchyMap = [:]
        hierarchyMap[query] = currentHierarchyMap;
        populateQueryHierarchy(query.id, currentHierarchyMap, queries);
    }
}

def constructSubqueryXML(builder, subQueries, groupName) {
    subQueries.each {query, currentSubQueries ->
        builder.Filter(id: query.id, name: query.name, nodeType: "filter", viewName: query.viewName, group: groupName, searchClass: query.searchClass, expanded:query.expanded,
                query: query.query, sortProperty: query.sortProperty, sortOrder: query.sortOrder, isPublic: query.isPublic, parentQueryId:query.parentQueryId) {
            constructSubqueryXML(builder, currentSubQueries, groupName);
        }
    }
}
return writer.toString();