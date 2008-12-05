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
    public static final ADD_RELATION_OPERATION_NAME = "AddRelation";
    public static final REMOVE_OPERATION_NAME = "Remove";
    public static final REMOVE_RELATION_OPERATION_NAME = "RemoveRelation";
    public static final UPDATE_OPERATION_NAME = "Update";
    public static final SEARCH_OPERATION_NAME = "Search";
    public static final SEARCH_TOP_OPERATION_NAME = "SearchTop";
    public static final COUNT_HITS_OPERATION_NAME = "CountHits";
    private static OperationStatistics operationStatisticsObject;
    Map operationStatistics = [:];
    Map modelStatistics = [:];
    private OperationStatistics()
    {
        reset();    
    }

    public static OperationStatistics getInstance()
    {
        if(operationStatisticsObject == null)
        {
            operationStatisticsObject = new OperationStatistics();
        }
        return operationStatisticsObject;
    }

    public static void destroyInstance()
    {
        operationStatisticsObject = null;
    }

    public void reset()
    {
        operationStatistics[ADD_OPERATION_NAME] = new GlobalOperationStatisticResult(name:ADD_OPERATION_NAME);
        operationStatistics[REMOVE_OPERATION_NAME] = new GlobalOperationStatisticResult(name:REMOVE_OPERATION_NAME);
        operationStatistics[ADD_RELATION_OPERATION_NAME] = new GlobalOperationStatisticResult(name:ADD_RELATION_OPERATION_NAME);
        operationStatistics[REMOVE_RELATION_OPERATION_NAME] = new GlobalOperationStatisticResult(name:REMOVE_RELATION_OPERATION_NAME);
        operationStatistics[UPDATE_OPERATION_NAME] = new GlobalOperationStatisticResult(name:UPDATE_OPERATION_NAME);
        operationStatistics[SEARCH_OPERATION_NAME] = new GlobalOperationStatisticResult(name:SEARCH_OPERATION_NAME);
        operationStatistics[SEARCH_TOP_OPERATION_NAME] = new GlobalOperationStatisticResult(name:SEARCH_TOP_OPERATION_NAME);
        operationStatistics[COUNT_HITS_OPERATION_NAME] = new GlobalOperationStatisticResult(name:COUNT_HITS_OPERATION_NAME);
        modelStatistics[ADD_OPERATION_NAME] = [:];
        modelStatistics[REMOVE_OPERATION_NAME] = [:];
        modelStatistics[ADD_RELATION_OPERATION_NAME] = [:];
        modelStatistics[REMOVE_RELATION_OPERATION_NAME] = [:];
        modelStatistics[UPDATE_OPERATION_NAME] = [:];
        modelStatistics[SEARCH_OPERATION_NAME] = [:];
        modelStatistics[SEARCH_TOP_OPERATION_NAME] = [:];
        modelStatistics[COUNT_HITS_OPERATION_NAME] = [:];
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
                    classBasedStatistics.each{String modelName, GlobalOperationStatisticResult modelResult->
                        def modelReport = modelResult.getGeneralReport();
                        modelReport["ModelName"] = modelName;
                        builder.ModelReport(modelReport);
                    }
                }
            }
        }
        return sw.toString();
    }

    public void addStatisticResult(String operationType, OperationStatisticResult result)
    {
        result.stop();
        GlobalOperationStatisticResult globalResult = operationStatistics[operationType]
        if(globalResult != null)
        {
            globalResult.addOperationStatisticResult (result);
            def modelStatistic = getModelStatistic(operationType, result.model)
            modelStatistic.addOperationStatisticResult (result);
        }
    }

    private GlobalOperationStatisticResult getModelStatistic(String operationType, String modelName)
    {
        Map modelStatisticsOfOperation = modelStatistics[operationType];
        GlobalOperationStatisticResult modelStatistic = modelStatisticsOfOperation[modelName]
        if(modelStatistic == null)
        {
            modelStatistic = new GlobalOperationStatisticResult();
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

    public Map getClassBasedStatistics()
    {
        def classStatisticsList = [];
        setModelStatistics.each{String modelName, GlobalOperationStatisticResult result->
            classStatisticsList.add(result.get);
        }
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
}