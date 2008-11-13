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
        modelStatistics[ADD_OPERATION_NAME] = [:];
        modelStatistics[REMOVE_OPERATION_NAME] = [:];
        modelStatistics[ADD_RELATION_OPERATION_NAME] = [:];
        modelStatistics[REMOVE_RELATION_OPERATION_NAME] = [:];
        modelStatistics[UPDATE_OPERATION_NAME] = [:];
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
        numberOfOperationsPerformed++
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
    long operationDuration;
    long startingTime = -1;
    public void startTime()
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