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
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import search.SearchQueryGroup
import auth.RsUser

/**
* Created by IntelliJ IDEA.
* User: mustafa sener
* Date: Aug 26, 2008
* Time: 2:03:53 PM
* To change this template use File | Settings | File Templates.
*/
def userName = web.session.username;
def queryType = params.queryType;
def extraFilteredProps = ["rsDatasource"];
def gridViews = ui.GridView.searchEvery("username:${userName.exactQuery()} OR (username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true)", [sort: "name"]);
if (queryType == "event" || queryType == "historicalEvent") {
    def className = queryType == "notification" ? "RsEvent" : "RsHistoricalEvent";
    def allProps = [];
    GrailsDomainClass domainClass = web.grailsApplication.getDomainClass(className)
    domainClass.getSubClasses().each {
        allProps.addAll(DomainClassUtils.getFilteredProperties(it.name, extraFilteredProps));
    }
    allProps.addAll(DomainClassUtils.getFilteredProperties(className, extraFilteredProps));
    def searchQueryGroups = SearchQueryGroup.list().findAll {queryGroup ->
        queryGroup.username == userName && queryGroup.isPublic == false && (queryGroup.type == queryType || queryGroup.type == "default")
    };
    def sortProps = allProps.sort {it.name}
    def propertyMap = [:]
    web.render(contentType: 'text/xml') {
        Create {
            group {
                searchQueryGroups.each {
                    option(it.name)
                }
            }
            sortProperty {
                sortProps.each {
                    if(!propertyMap.containsKey(it.name)){
                        propertyMap.put(it.name, it.name);
                        option(it.name)
                    }
                }
            }
            viewName {
                option('default');
                gridViews.each {
                    option(it.name)
                }
            }
        }
    }
}
else if (queryType == "topology") {
    def sortProperties = [];
    GrailsDomainClass domainClass = web.grailsApplication.getDomainClass("RsTopologyObject")
    domainClass.getSubClasses().each {
        sortProperties.addAll(DomainClassUtils.getFilteredProperties(it.name, extraFilteredProps));
    }
    sortProperties.addAll(DomainClassUtils.getFilteredProperties("RsTopologyObject", extraFilteredProps));
    def searchQueryGroups = SearchQueryGroup.list().findAll {queryGroup ->
        queryGroup.username == userName && queryGroup.isPublic == false && (queryGroup.type == queryType || queryGroup.type == "default")
    };
    def sortProps = sortProperties.sort {it.name}
    def propertyMap = [:]
    web.render(contentType: 'text/xml') {
        Create {
            group {
                searchQueryGroups.each {
                    option(it.name)
                }
            }
            sortProperty {
                sortProps.each {
                    def propertyName = it.name;
                    if (!propertyMap.containsKey(propertyName)) {
                        option(propertyName)
                    }
                    propertyMap.put(propertyName, propertyName)
                }
            }
            viewName {
                option('default');
                gridViews.each {
                    option(it.name)
                }
            }
        }
    }
}
else {
    throw new Exception("Invalid query type");
}

