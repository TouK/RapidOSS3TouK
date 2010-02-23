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
def username=web.session.username;

def writer = new StringWriter();
def queryBuilder = new MarkupBuilder(writer);

SearchQueryGroup.add(name:SearchQueryGroup.MY_QUERIES, username:username, type:SearchQueryGroup.DEFAULT_TYPE);
def queryGroups = SearchQueryGroup.searchEvery("( type:${filterType.exactQuery()} OR type:${SearchQueryGroup.DEFAULT_TYPE.exactQuery()} ) AND  ( ( username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true) OR (username:${username.exactQuery()}) )");
queryBuilder.Filters
{
    queryGroups.each {SearchQueryGroup group ->
        def userName = group.username;
           queryBuilder.Filter(id: group.id, name: group.name, nodeType: "group",  isPublic:group.isPublic, expanded:group.expanded) {
              group.queries.each {SearchQuery query ->
                  if(query.type == filterType && (query.username == username || query.isPublic)){
                        queryBuilder.Filter(id: query.id, name: query.name, nodeType: "filter", viewName:query.viewName, group:group.name, searchClass:query.searchClass,
                                query: query.query, sortProperty: query.sortProperty, sortOrder: query.sortOrder, isPublic:query.isPublic)
                  }

              }
           }

    }
}
return writer.toString();