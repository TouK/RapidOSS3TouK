package datasource

import com.ifountain.apg.datasource.ApgDatabaseAdapter
import org.apache.log4j.Logger
import com.watch4net.apg.v2.remote.sample.jaxws.db.Suggestion
import com.watch4net.apg.v2.remote.sample.jaxws.db.Property
import com.watch4net.apg.v2.remote.sample.jaxws.db.PropertyRecord
import com.watch4net.apg.v2.remote.sample.jaxws.db.DistinctPropertyValues
import com.watch4net.apg.v2.remote.sample.jaxws.db.ObjectPropertyValues
import com.watch4net.apg.v2.remote.sample.jaxws.db.PropertyValues
import com.watch4net.apg.v2.remote.sample.jaxws.db.PropertyValue

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 6:08:19 PM
 * To change this template use File | Settings | File Templates.
 */
class ApgDatabaseDatasourceOperations extends BaseDatasourceOperations {
    def adapter;
    def onLoad() {
        this.adapter = new ApgDatabaseAdapter(getProperty("connection").name, reconnectInterval * 1000, Logger.getRootLogger());
    }

    def getSuggestions(username, password, filter, pattern, properties, limit) {
        def suggests = [];
        def suggestions = this.adapter.getSuggestions(username, password, filter, pattern, properties, limit);
        suggestions.each {Suggestion suggestion ->
            suggests.add(["Accessor": suggestion.getAccessor(), "Name": suggestion.getProperty(), "Value": suggestion.getValue()])
        }
        return suggests;
    }

    def getAvailableProperties(username, password, filter) {
        def props = [];
        def properties = this.adapter.getAvailableProperties(username, password, filter);
        properties.each {Property prop ->
            props.add(["Accessor": prop.getAccessor(), "Name": prop.getName(), "Value": prop.getValue()])
        }
        return props;
    }

    def getAvailableAggregatePeriods(username, password, filter) {
        def periods = [];
        def availablePeriods = this.adapter.getAvailableAggregatePeriods(username, password, filter);
        availablePeriods.each {Integer period ->
            periods.add(period.intValue());
        }
        return periods;
    }

    def getAvailableAccessors(username, password, filter) {
        return this.adapter.getAvailableAccessors(username, password, filter);
    }

    def getStaticObjectCount(username, password, filter) {
        return this.adapter.getStaticObjectCount(username, password, filter)
    }

    def getStaticObjectFilters(username, password, filter) {
        return this.adapter.getStaticObjectFilters(username, password, filter)
    }

    def getObjectCount(username, password, filter, startTimestamp, endTimestamp, timeFilter, limit) {
        return this.adapter.getObjectCount(username, password, filter, startTimestamp, endTimestamp, timeFilter, limit)
    }

    def getObjectFilters(username, password, filter, startTimestamp, endTimestamp, timeFilter, limit) {
        return this.adapter.getObjectFilters(username, password, filter, startTimestamp, endTimestamp, timeFilter, limit)
    }

    def getDistinctPropertyRecords(username, password, filter, properties) {
        def propRecords = [];
        def propertyRecords = this.adapter.getDistinctPropertyRecords(username, password, filter, properties)
        propertyRecords.each {PropertyRecord propRecord ->
            propRecords.add(propRecord.getValue());
        }
        return propRecords;
    }

    def getDistinctPropertyValues(username, password, filter, subFilters, properties, limit) {
        def dPropValues = [];
        def distinctPropertyValues = this.adapter.getDistinctPropertyValues(username, password, filter, subFilters, properties, limit);
        distinctPropertyValues.each {DistinctPropertyValues propValues ->
            dPropValues.add(propValues.getValue());
        }
        return dPropValues;
    }

    def getObjectProperties(username, password, filter, subFilters, properties) {
        def oPropertyValuesList = [];
        def objectPropertyValuesList = this.adapter.getObjectProperties(username, password, filter, subFilters, properties);
        objectPropertyValuesList.each {ObjectPropertyValues objectPropValues ->
            def pList = [];
            def propertiesList = objectPropValues.getProperties();
            propertiesList.each {PropertyValues propertyValues ->
                def pValues = [:]
                def id = propertyValues.getId();
                pValues.put("Id", id);
                def propertyValueList = propertyValues.getValue();
                def pValueList = [];
                propertyValueList.each {PropertyValue propValue ->
                    pValueList.add(["Name": propValue.getProperty(), "Value": propValue.getValue()]);
                }
                pValues.put("Values", pValueList);
                pList.add(pValues);
            }
            oPropertyValuesList.add(pList);
        }
        return oPropertyValuesList;
    }
}