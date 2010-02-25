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
import auth.RsUser
import search.SearchQueryGroup
import search.SearchQuery
import ui.map.MapGroup

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 19, 2008
* Time: 5:32:17 PM
*/

def adminUser = RsUser.RSADMIN;
def defaultEventGroup = SearchQueryGroup.add(name: "Default", username:adminUser, isPublic:true, type:"event", expanded:true);

SearchQuery.add(group: defaultEventGroup, name: "All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event", searchClass:'RsEvent');
SearchQuery.add(group: defaultEventGroup, name: "Critical Events", query: "severity:5", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event", searchClass:'RsEvent');
SearchQuery.add(group: defaultEventGroup, name: "Major Events", query: "severity:4", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event", searchClass:'RsEvent');
SearchQuery.add(group: defaultEventGroup, name: "Minor Events", query: "severity:3", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event", searchClass:'RsEvent');
SearchQuery.add(group: defaultEventGroup, name: "Warning Events", query: "severity:2", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event", searchClass:'RsEvent');
SearchQuery.add(group: defaultEventGroup, name: "Indeterminate Events", query: "severity:1", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event", searchClass:'RsEvent');
SearchQuery.add(group: defaultEventGroup, name: "Normal Events", query: "severity:0", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event", searchClass:'RsEvent');
