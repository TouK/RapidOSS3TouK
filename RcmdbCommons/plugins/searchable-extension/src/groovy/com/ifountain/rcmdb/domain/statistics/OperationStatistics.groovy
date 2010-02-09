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
package com.ifountain.rcmdb.domain.statistics

import groovy.xml.MarkupBuilder

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 12, 2008
 * Time: 4:47:25 PM
 * To change this template use File | Settings | File Templates.
 */
class OperationStatistics {

    public static final ADD_OPERATION_NAME = "Add";
    public static final AFTER_INSERT_OPERATION_NAME = "AfterInsert";
    public static final BEFORE_INSERT_OPERATION_NAME = "BeforeInsert";
    public static final ADD_RELATION_OPERATION_NAME = "AddRelation";
    public static final REMOVE_OPERATION_NAME = "Remove";
    public static final AFTER_DELETE_OPERATION_NAME = "AfterDelete";
    public static final BEFORE_DELETE_OPERATION_NAME = "BeforeDelete";
    public static final REMOVE_RELATION_OPERATION_NAME = "RemoveRelation";
    public static final UPDATE_OPERATION_NAME = "Update";
    public static final AFTER_UPDATE_OPERATION_NAME = "AfterUpdate";
    public static final BEFORE_UPDATE_OPERATION_NAME = "BeforeUpdate";
    public static final SEARCH_OPERATION_NAME = "Search";
    public static final SEARCH_TOP_OPERATION_NAME = "SearchTop";
    public static final COUNT_HITS_OPERATION_NAME = "CountHits";
    public static final GET_RELATED_MODEL_PROPERTY_VALUES_OPERATION_NAME ="GetRelatedModelPropertyValues";
    public static final GET_PROPERTY_VALUES_OPERATION_NAME ="GetPropertyValues";
    public static final SEARCH_AS_STRING_OPERATION_NAME ="SearchAsString";
    public static final PROPERTY_SUMMARY_OPERATION_NAME ="PropertySummary";
    public static final REMOVE_ALL_OPERATION_NAME ="RemoveAll";

    private static OperationStatistics operationStatisticsObject;
    private static Object getInstanceLock = new Object();
    Map operationStatistics = [:];
    Map modelStatistics = [:];
    private OperationStatistics()
    {
        reset();    
    }

    public static OperationStatistics getInstance()
    {
        synchronized (getInstanceLock)
        {
            if(operationStatisticsObject == null)
            {
                operationStatisticsObject = new OperationStatistics();
            }
            return operationStatisticsObject;
        }
    }

    public static void destroyInstance()
    {
        synchronized (getInstanceLock)
        {
            operationStatisticsObject = null;
        }
    }

    public void reset()
    {
        operationStatistics[ADD_OPERATION_NAME] = new GlobalOperationStatisticResult(name:ADD_OPERATION_NAME);
        operationStatistics[AFTER_INSERT_OPERATION_NAME] = new GlobalOperationStatisticResult(name:AFTER_INSERT_OPERATION_NAME);
        operationStatistics[BEFORE_INSERT_OPERATION_NAME] = new GlobalOperationStatisticResult(name:BEFORE_INSERT_OPERATION_NAME);
        operationStatistics[REMOVE_OPERATION_NAME] = new GlobalOperationStatisticResult(name:REMOVE_OPERATION_NAME);
        operationStatistics[AFTER_DELETE_OPERATION_NAME] = new GlobalOperationStatisticResult(name:AFTER_DELETE_OPERATION_NAME);
        operationStatistics[BEFORE_DELETE_OPERATION_NAME] = new GlobalOperationStatisticResult(name:BEFORE_DELETE_OPERATION_NAME);
        operationStatistics[UPDATE_OPERATION_NAME] = new GlobalOperationStatisticResult(name:UPDATE_OPERATION_NAME);
        operationStatistics[AFTER_UPDATE_OPERATION_NAME] = new GlobalOperationStatisticResult(name:AFTER_UPDATE_OPERATION_NAME);
        operationStatistics[BEFORE_UPDATE_OPERATION_NAME] = new GlobalOperationStatisticResult(name:BEFORE_UPDATE_OPERATION_NAME);
        operationStatistics[ADD_RELATION_OPERATION_NAME] = new GlobalOperationStatisticResult(name:ADD_RELATION_OPERATION_NAME);
        operationStatistics[REMOVE_RELATION_OPERATION_NAME] = new GlobalOperationStatisticResult(name:REMOVE_RELATION_OPERATION_NAME);
        operationStatistics[SEARCH_OPERATION_NAME] = new GlobalOperationStatisticResult(name:SEARCH_OPERATION_NAME);
        operationStatistics[SEARCH_AS_STRING_OPERATION_NAME] = new GlobalOperationStatisticResult(name:SEARCH_AS_STRING_OPERATION_NAME);
        operationStatistics[GET_PROPERTY_VALUES_OPERATION_NAME] = new GlobalOperationStatisticResult(name:GET_PROPERTY_VALUES_OPERATION_NAME);
        operationStatistics[GET_RELATED_MODEL_PROPERTY_VALUES_OPERATION_NAME] = new GlobalOperationStatisticResult(name:GET_RELATED_MODEL_PROPERTY_VALUES_OPERATION_NAME);
        operationStatistics[SEARCH_TOP_OPERATION_NAME] = new GlobalOperationStatisticResult(name:SEARCH_TOP_OPERATION_NAME);
        operationStatistics[COUNT_HITS_OPERATION_NAME] = new GlobalOperationStatisticResult(name:COUNT_HITS_OPERATION_NAME);
        operationStatistics[PROPERTY_SUMMARY_OPERATION_NAME] = new GlobalOperationStatisticResult(name:PROPERTY_SUMMARY_OPERATION_NAME);
        operationStatistics[REMOVE_ALL_OPERATION_NAME] = new GlobalOperationStatisticResult(name:REMOVE_ALL_OPERATION_NAME);
        modelStatistics[ADD_OPERATION_NAME] = [:];
        modelStatistics[AFTER_INSERT_OPERATION_NAME] = [:];
        modelStatistics[BEFORE_INSERT_OPERATION_NAME] = [:];
        modelStatistics[REMOVE_OPERATION_NAME] = [:];
        modelStatistics[BEFORE_DELETE_OPERATION_NAME] = [:];
        modelStatistics[AFTER_DELETE_OPERATION_NAME] = [:];
        modelStatistics[UPDATE_OPERATION_NAME] = [:];
        modelStatistics[AFTER_UPDATE_OPERATION_NAME] = [:];
        modelStatistics[BEFORE_UPDATE_OPERATION_NAME] = [:];
        modelStatistics[ADD_RELATION_OPERATION_NAME] = [:];
        modelStatistics[REMOVE_RELATION_OPERATION_NAME] = [:];
        modelStatistics[SEARCH_OPERATION_NAME] = [:];
        modelStatistics[SEARCH_AS_STRING_OPERATION_NAME] = [:];
        modelStatistics[GET_PROPERTY_VALUES_OPERATION_NAME] = [:];
        modelStatistics[GET_RELATED_MODEL_PROPERTY_VALUES_OPERATION_NAME] =[:];
        modelStatistics[SEARCH_TOP_OPERATION_NAME] = [:];
        modelStatistics[COUNT_HITS_OPERATION_NAME] = [:];
        modelStatistics[PROPERTY_SUMMARY_OPERATION_NAME] = [:];        
        modelStatistics[REMOVE_ALL_OPERATION_NAME] = [:];
    }
    
    public String getGlobalStatistics()
    {
        def sw = new StringWriter();
        MarkupBuilder builder = new MarkupBuilder(sw);
        builder.Reports()
        {
            operationStatistics.each{String operationName, GlobalOperationStatisticResult result-> 
                builder.Report(result.getGeneralReport ())
                {
                    def classBasedStatistics = modelStatistics[operationName];
                    def sortedClassBasedEntries=modelStatistics[operationName].entrySet().sort {it.key}
                    sortedClassBasedEntries.each { entry ->
                        String modelName=entry.key;
                        GlobalOperationStatisticResult modelResult=entry.value;
                        def modelReport = modelResult.getGeneralReport();
                        modelReport["ModelName"] = modelName;
                        builder.ModelReport(modelReport);
                    }                          
                }
            }
        }
        return sw.toString();
    }
    public Map getOperationStatisticsAsMap(operationName)
    {
        def stats=[:];
        stats.global=operationStatistics[operationName].getGeneralReport().clone();
        def sortedClassBasedEntries=modelStatistics[operationName].entrySet().sort {it.key};
        sortedClassBasedEntries.each { entry ->
            String modelName=entry.key;
            GlobalOperationStatisticResult modelResult=entry.value;
            stats[modelName]=modelResult.getGeneralReport().clone();
        }
        return stats;
    }

    public void addStatisticResult(String operationType, OperationStatisticResult result)
    {
        result.stop();
        GlobalOperationStatisticResult globalResult = operationStatistics[operationType]
        if(globalResult != null)
        {
            if(!result.isSubStatistics)
            {
                globalResult.addOperationStatisticResult (result);
            }
            def modelStatistic = getModelStatistic(operationType, result.model)
            modelStatistic.addOperationStatisticResult (result);
        }
    }

    public GlobalOperationStatisticResult getModelStatistic(String operationType, String modelName)
    {
        Map modelStatisticsOfOperation = modelStatistics[operationType];
        GlobalOperationStatisticResult modelStatistic = modelStatisticsOfOperation[modelName]
        if(modelStatistic == null)
        {
            modelStatistic = new GlobalOperationStatisticResult(name:operationType);
            modelStatisticsOfOperation[modelName] = modelStatistic;
        }
        return modelStatistic;
    }
}
class GlobalOperationStatisticResult
{
    long numberOfOperationsPerformed;
    String name;
    long totalOperationDuration;
    public synchronized void addOperationStatisticResult(OperationStatisticResult result)
    {
        numberOfOperationsPerformed+=result.numberOfOperations;
        totalOperationDuration += result.operationDuration;
    }

    public Map getGeneralReport()
    {
        def totalDurationInSecs = totalOperationDuration/Math.pow(10, 9);
        return [Operation:name, NumberOfOperations:numberOfOperationsPerformed, TotalDuration:totalDurationInSecs, AvarageDuration:totalDurationInSecs/numberOfOperationsPerformed];
    }

}
class OperationStatisticResult
{
    String model;
    long operationDuration = 0;
    long numberOfOperations =1;
    long startingTime = -1;
    boolean isSubStatistics=false;
    static private double LN_10=Math.log(10);
    
    public void start()
    {
        startingTime = System.nanoTime()   
    }

    public void stop()
    {
        if(startingTime != -1)
        {
            operationDuration += System.nanoTime()-startingTime;
        }
        startingTime = -1;
    }
    public OperationStatisticResult getSubStatisticsWithObjectCount(count)
    {
        int countSuffix=0;
        if(count >0 && count != null)
        {
            countSuffix=Math.pow(10,Math.floor(Math.log(count)/LN_10).toInteger());
        }
        
        return new OperationStatisticResult(model:this.model+"_"+countSuffix,operationDuration:this.operationDuration,startingTime:this.startingTime,isSubStatistics:true);
    }
}