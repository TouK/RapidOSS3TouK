package datasource

import com.ifountain.apg.datasource.ApgDatabaseAdapter
import com.watch4net.apg.v2.remote.sample.jaxws.db.*
import org.apache.log4j.Logger
import groovy.lang.PropertyValue

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

    def getSuggestions(filter, pattern, properties, limit) {
        def suggests = [];
        def suggestions = this.adapter.getSuggestions(connection.username, connection.userPassword, filter, pattern, properties, limit);
        suggestions.each {Suggestion suggestion ->
            suggests.add(["accessor": suggestion.getAccessor(), "name": suggestion.getProperty(), "value": suggestion.getValue()])
        }
        return suggests;
    }

    def getAvailableProperties(filter) {
        def props = [];
        def properties = this.adapter.getAvailableProperties(connection.username, connection.userPassword, filter);
        properties.each {Property prop ->
            props.add(["accessor": prop.getAccessor(), "name": prop.getName(), "value": prop.getValue()])
        }
        return props;
    }

    def getAvailableAggregatePeriods(filter) {
        def periods = [];
        def availablePeriods = this.adapter.getAvailableAggregatePeriods(connection.username, connection.userPassword, filter);
        availablePeriods.each {Integer period ->
            periods.add(period.intValue());
        }
        return periods;
    }

    def getAvailableAccessors(filter) {
        return this.adapter.getAvailableAccessors(connection.username, connection.userPassword, filter);
    }

    def getStaticObjectCount(filter) {
        return this.adapter.getStaticObjectCount(connection.username, connection.userPassword, filter)
    }

    def getStaticObjectFilters(filter) {
        return this.adapter.getStaticObjectFilters(connection.username, connection.userPassword, filter)
    }

    def getObjectCount(filter, startTimestamp, endTimestamp, timeFilter, limit) {
        return this.adapter.getObjectCount(connection.username, connection.userPassword, filter, startTimestamp, endTimestamp, timeFilter, limit)
    }

    def getObjectFilters(filter, startTimestamp, endTimestamp, timeFilter, limit) {
        return this.adapter.getObjectFilters(connection.username, connection.userPassword, filter, startTimestamp, endTimestamp, timeFilter, limit)
    }

    def getDistinctPropertyRecords(filter, properties) {
        def propRecords = [];
        def propertyRecords = this.adapter.getDistinctPropertyRecords(connection.username, connection.userPassword, filter, properties)
        propertyRecords.each {PropertyRecord propRecord ->
            propRecords.add(propRecord.getValue());
        }
        return propRecords;
    }

    def getDistinctPropertyValues(filter, subFilters, properties, limit) {
        def dPropValues = [];
        def distinctPropertyValues = this.adapter.getDistinctPropertyValues(connection.username, connection.userPassword, filter, subFilters, properties, limit);
        distinctPropertyValues.each {DistinctPropertyValues propValues ->
            dPropValues.add(propValues.getValue());
        }
        return dPropValues;
    }

    def getObjectProperties(filter, subFilters, properties) {
        def oPropertyValuesList = [];
        def objectPropertyValuesList = this.adapter.getObjectProperties(connection.username, connection.userPassword, filter, subFilters, properties);
        objectPropertyValuesList.each {ObjectPropertyValues objectPropValues ->
            def pList = [];
            def propertiesList = objectPropValues.getProperties();
            propertiesList.each {PropertyValues propertyValues ->
                def pValues = [:]
                def id = propertyValues.getId();
                pValues.put("id", id);
                def propertyValueList = propertyValues.getValue();
                def pValueList = [];
                propertyValueList.each {PropertyValue propValue ->
                    pValueList.add(["name": propValue.getProperty(), "value": propValue.getValue()]);
                }
                pValues.put("values", pValueList);
                pList.add(pValues);
            }
            oPropertyValuesList.add(pList);
        }
        return oPropertyValuesList;
    }

    def getObjectData(filter, subFilters, startTimestamp, endTimestamp, timeFilter, period, fields, selectedVariables, limit) {
        def fieldsList = [];
        fields.each {
            fieldsList.add(Aggregation.fromValue(it));
        }
        def objectData = this.adapter.getObjectData(connection.username, connection.userPassword, filter, subFilters, startTimestamp, endTimestamp, timeFilter, period, fieldsList, selectedVariables, limit)
        def oData = [];
        objectData.each {TimeSeries timeSeries ->
            def tSeries = [];
            timeSeries.getTimeserie().each {TimeSerie timeSerie ->
                def tSerie = [:]
                tSerie.put("id", timeSerie.getId())
                tSerie.put("fields", timeSerie.getFields())
                tSerie.put("length", timeSerie.getLength())
                def tv = [];
                timeSerie.getTv().each {TimeSerieValue timeSerieValue ->
                    def tSerieValue = [:]
                    tSerieValue.put("t", timeSerieValue.getT());
                    def v = [];
                    v.addAll(timeSerieValue.getV())
                    tSerieValue.put("v", v);
                    tv.add(tSerieValue);
                }
                tSerie.put("tv", tv);
                tSeries.add(tSerie);
            }
            oData.add(tSeries);
        }
        return oData;
    }

    def getAggregatedData(filter, subFilter, startTimestamp, endTimestamp, timeFilter, period, aggregations) {

        def aggs;
        if (aggregations != null) {
            aggs = new Aggregations();
            aggs.setSpacial(Aggregation.fromValue(aggregations["spacial"]))
            if (aggregations["count"] != null) {
                aggs.setCount(Aggregation.fromValue(aggregations["count"]))
            }
            if (aggregations["temporal"] != null) {
                aggs.setTemporal(Aggregation.fromValue(aggregations["temporal"]))
            }
        }

        def aggData = [];
        def aggregatedData = this.adapter.getAggregatedData(connection.username, connection.userPassword, filter, subFilter, startTimestamp, endTimestamp, timeFilter, period, aggs);
        aggregatedData.each {TimeSerie timeSerie ->
            def tSerie = [:]
            tSerie.put("id", timeSerie.getId())
            tSerie.put("fields", timeSerie.getFields())
            tSerie.put("length", timeSerie.getLength())
            def tv = [];
            timeSerie.getTv().each {TimeSerieValue timeSerieValue ->
                def tSerieValue = [:]
                tSerieValue.put("t", timeSerieValue.getT());
                def v = [];
                v.addAll(timeSerieValue.getV())
                tSerieValue.put("v", v);
                tv.add(tSerieValue);
            }
            tSerie.put("tv", tv);
            aggData.add(tSerie);
        }
        return aggData;
    }
}