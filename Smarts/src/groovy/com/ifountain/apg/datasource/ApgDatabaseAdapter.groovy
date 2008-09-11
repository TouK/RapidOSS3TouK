package com.ifountain.apg.datasource;

import com.ifountain.apg.datasource.actions.*;
import com.ifountain.core.datasource.BaseAdapter;
import com.watch4net.apg.v2.remote.sample.jaxws.db.*;

import java.util.List;
import java.util.Map;
import java.lang.Exception;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
 * Date: Sep 10, 2008
 * Time: 9:11:47 AM
 * To change this template use File | Settings | File Templates.
 */
class ApgDatabaseAdapter extends BaseAdapter {

    public DatabaseAccessorService authenticate(String username, String password) throws Exception {
        DatabaseAuthenticateAction authAction = new DatabaseAuthenticateAction(username, password);
        executeAction(authAction);
        return authAction.getDbService();
    }

    public List<TimeSeries> getObjectData(String username, String password, String filter, List<String> subFilters, int startTimestamp, int endTimestamp,
                                          String timeFilter, int period, List<Aggregation> fields, List<String> selectedVariables, int limit) throws Exception {
        DatabaseAccessorService dbService = authenticate(username, password);
        GetObjectDataAction getObjectDataAction = new GetObjectDataAction(dbService, filter, subFilters, startTimestamp, endTimestamp,
                timeFilter, period, fields, selectedVariables, limit);
        executeAction(getObjectDataAction);
        return getObjectDataAction.getObjectData();
    }

    public List<TimeSerie> getAggregatedData(String username, String password, String filter, List<String> subFilter, int startTimestamp, int endTimestamp,
                                             String timeFilter, int period, Aggregations aggregations) throws Exception {
        DatabaseAccessorService dbService = authenticate(username, password);
        GetAggregationDataAction getAggregateDataAction = new GetAggregationDataAction(dbService, filter, subFilter, startTimestamp, endTimestamp, timeFilter, period, aggregations);
        executeAction(getAggregateDataAction);
        return getAggregateDataAction.getAggregateData();
    }

    public List<ObjectPropertyValues> getObjectProperties(String username, String password, String filter, List<String> subFilters, List<String> properties) throws Exception {
        DatabaseAccessorService dbService = authenticate(username, password);
        GetObjectPropertiesAction action = new GetObjectPropertiesAction(dbService, filter, subFilters, properties);
        executeAction(action);
        return action.getObjectProperties();
    }

    public List<DistinctPropertyValues> getDistinctPropertyValues(String username, String password, String filter, List<String> subFilters, List<String> properties, int limit) throws Exception {
        DatabaseAccessorService dbService = authenticate(username, password);
        GetDistinctPropertyValuesAction action = new GetDistinctPropertyValuesAction(dbService, filter, subFilters, properties, limit);
        executeAction(action);
        return action.getPropertyValues();
    }

    public List<PropertyRecord> getDistinctPropertyRecords(String username, String password, String filter, List<String> properties) throws Exception {
        DatabaseAccessorService dbService = authenticate(username, password);
        GetDistinctPropertyRecordsAction action = new GetDistinctPropertyRecordsAction(dbService, filter, properties);
        executeAction(action);
        return action.getPropertyRecords();
    }

    public List<String> getObjectFilters(String username, String password, String filter, int startTimestamp, int endTimestamp, String timeFilter, int limit) throws Exception {
        DatabaseAccessorService dbService = authenticate(username, password);
        GetObjectFiltersAction action = new GetObjectFiltersAction(dbService, filter, startTimestamp, endTimestamp, timeFilter, limit);
        executeAction(action);
        return action.getObjectFilters();
    }

    public int getObjectCount(String username, String password, String filter, int startTimestamp, int endTimestamp, String timeFilter, int limit) throws Exception {
        DatabaseAccessorService dbService = authenticate(username, password);
        GetObjectCountAction action = new GetObjectCountAction(dbService, filter, startTimestamp, endTimestamp, timeFilter, limit);
        executeAction(action);
        return action.getObjectCount();
    }

    public List<String> getStaticObjectFilters(String username, String password, String filter) throws Exception {
        DatabaseAccessorService dbService = authenticate(username, password);
        GetStaticObjectFiltersAction action = new GetStaticObjectFiltersAction(dbService, filter);
        executeAction(action);
        return action.getStaticObjectFilters();
    }

    public int getStaticObjectCount(String username, String password, String filter) throws Exception {
        DatabaseAccessorService dbService = authenticate(username, password);
        GetStaticObjectCountAction action = new GetStaticObjectCountAction(dbService, filter);
        executeAction(action);
        return action.getStaticObjectCount();
    }

    public List<String> getAvailableAccessors(String username, String password, String filter) throws Exception {
        DatabaseAccessorService dbService = authenticate(username, password);
        GetAvailableAccessorsAction action = new GetAvailableAccessorsAction(dbService, filter);
        executeAction(action);
        return action.getAvailableAccessors();
    }

    public List<Integer> getAvailableAggregatePeriods(String username, String password, String filter) throws Exception {
        DatabaseAccessorService dbService = authenticate(username, password);
        GetAvailableAggregatePeriodsAction action = new GetAvailableAggregatePeriodsAction(dbService, filter);
        executeAction(action);
        return action.getAvailableAggregatePeriods();
    }

    public List<Property> getAvailableProperties(String username, String password, String filter) throws Exception {
        DatabaseAccessorService dbService = authenticate(username, password);
        GetAvailablePropertiesAction action = new GetAvailablePropertiesAction(dbService, filter);
        executeAction(action);
        return action.getAvailableProperties();
    }

    public List<Suggestion> getSuggestions(String username, String password, String filter,String pattern,List<String> properties,int limit)throws Exception{
        DatabaseAccessorService dbService = authenticate(username, password);
        GetSuggestionsAction action = new GetSuggestionsAction(dbService, filter, pattern, properties, limit);
        executeAction(action);
        return action.getSuggesstions();
    }


    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) {
        return null;
    }
}