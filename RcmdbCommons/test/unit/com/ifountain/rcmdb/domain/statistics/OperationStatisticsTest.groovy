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

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import groovy.util.slurpersupport.GPathResult

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 13, 2008
 * Time: 11:22:14 AM
 * To change this template use File | Settings | File Templates.
 */
class OperationStatisticsTest extends RapidCmdbTestCase{

    protected void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        OperationStatistics.destroyInstance();
    }

    protected void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
        OperationStatistics.destroyInstance();
    }

    public void testAddOperationStatisticResult()
    {
        String statisticsXml = OperationStatistics.getInstance().getGlobalStatistics();
        def slurp = new XmlSlurper();
        def xmlObj = slurp.parseText(statisticsXml);
        def reports = xmlObj.Report;
        
        assertEquals (14, reports.size());
        def addOperationXmlNode = reports.findAll {it.@Operation.text() == OperationStatistics.ADD_OPERATION_NAME}[0];
        checkGlobalStatisticsResult (addOperationXmlNode, []);
        
        OperationStatisticResult res = new OperationStatisticResult(model:"Model1", operationDuration:1000);
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, res)


        statisticsXml = OperationStatistics.getInstance().getGlobalStatistics();
        slurp = new XmlSlurper();
        xmlObj = slurp.parseText(statisticsXml);
        reports = xmlObj.Report;

        assertEquals (14, reports.size());
        addOperationXmlNode = reports.findAll {it.@Operation.text() == OperationStatistics.ADD_OPERATION_NAME}[0];
        checkGlobalStatisticsResult(addOperationXmlNode, [res]);
        checkModelStatisticsResult(addOperationXmlNode, "Model1", [res]);

        OperationStatisticResult res2 = new OperationStatisticResult(model:"Model1", operationDuration:2000);
        OperationStatisticResult res3 = new OperationStatisticResult(model:"Model2", operationDuration:2000);
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, res2)
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, res3)

        statisticsXml = OperationStatistics.getInstance().getGlobalStatistics();
        slurp = new XmlSlurper();
        xmlObj = slurp.parseText(statisticsXml);
        reports = xmlObj.Report;

        assertEquals (14, reports.size());
        addOperationXmlNode = reports.findAll {it.@Operation.text() == OperationStatistics.ADD_OPERATION_NAME}[0];
        checkGlobalStatisticsResult(addOperationXmlNode, [res, res2, res3]);
        checkModelStatisticsResult(addOperationXmlNode, "Model1", [res, res2]);
        checkModelStatisticsResult(addOperationXmlNode, "Model2", [res3]);


        OperationStatistics.getInstance().reset();
        statisticsXml = OperationStatistics.getInstance().getGlobalStatistics();
        slurp = new XmlSlurper();
        xmlObj = slurp.parseText(statisticsXml);
        reports = xmlObj.Report;

        assertEquals (14, reports.size());
        addOperationXmlNode = reports.findAll {it.@Operation.text() == OperationStatistics.ADD_OPERATION_NAME}[0];
        checkGlobalStatisticsResult (addOperationXmlNode, []);
    }
    public void testGetGlobalStatisticsSortsClassBasedStatisticsWithModelName()
    {

        String statisticsXml = OperationStatistics.getInstance().getGlobalStatistics();
        def slurp = new XmlSlurper();
        def xmlObj = slurp.parseText(statisticsXml);
        def reports = xmlObj.Report;

        assertEquals (14, reports.size());
        def addOperationXmlNode = reports.findAll {it.@Operation.text() == OperationStatistics.ADD_OPERATION_NAME}[0];


        //adding unsorted

        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, new OperationStatisticResult(model:"Model2_10", operationDuration:1000))
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, new OperationStatisticResult(model:"Model2_0", operationDuration:1000))
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, new OperationStatisticResult(model:"Model2", operationDuration:1000))
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, new OperationStatisticResult(model:"Model2_1", operationDuration:1000))

        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, new OperationStatisticResult(model:"Model1_10", operationDuration:1000))
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, new OperationStatisticResult(model:"Model1_0", operationDuration:1000))
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, new OperationStatisticResult(model:"Model1", operationDuration:1000))
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, new OperationStatisticResult(model:"Model1_1", operationDuration:1000))



        statisticsXml = OperationStatistics.getInstance().getGlobalStatistics();
        slurp = new XmlSlurper();
        xmlObj = slurp.parseText(statisticsXml);
        reports = xmlObj.Report;

        assertEquals (14, reports.size());
        addOperationXmlNode = reports.findAll {it.@Operation.text() == OperationStatistics.ADD_OPERATION_NAME}[0];
        def modelReportList=[];
        addOperationXmlNode.ModelReport.each { modelReport ->
            modelReportList.add(modelReport.@ModelName.text());
        }
        assertEquals(["Model1","Model1_0","Model1_1","Model1_10","Model2","Model2_0","Model2_1","Model2_10"],modelReportList)

    }
    public void testGetCloneWithObjectCountCreatesANewStatisticResultWithModelNameChanged()
    {
          OperationStatisticResult res = new OperationStatisticResult(model:"Model1", operationDuration:1000,startingTime:50);
          OperationStatisticResult resWithItemCount=res.getCloneWithObjectCount(1);

          assertNotSame(res,resWithItemCount);
          assertEquals(res.operationDuration,resWithItemCount.operationDuration);
          assertEquals(res.startingTime,resWithItemCount.startingTime);
          assertEquals("Model1_1",resWithItemCount.model);


          OperationStatisticResult resWithItemCount2=res.getCloneWithObjectCount(5);

          assertNotSame(res,resWithItemCount2);
          assertEquals(res.operationDuration,resWithItemCount2.operationDuration);
          assertEquals(res.startingTime,resWithItemCount.startingTime);
          assertEquals("Model1_1",resWithItemCount2.model);

          assertEquals("Model1_10",res.getCloneWithObjectCount(10).model);
          assertEquals("Model1_10",res.getCloneWithObjectCount(20).model);
          assertEquals("Model1_100",res.getCloneWithObjectCount(100).model);

          assertEquals("Model1_0",res.getCloneWithObjectCount(0).model);
          assertEquals("Model1_0",res.getCloneWithObjectCount(null).model);


//          1000.times{
//              int count=Math.random()*100000;
//              def start=System.nanoTime();
//              res.getStatisticResultWithObjectCount(count)
//              def end=System.nanoTime();
//              println "count ${count} time ${end-start}"
//
//          }


    }

    private void checkModelStatisticsResult(xmlNode, String expectedModelName, operationResults)
    {
        def modelName = xmlNode.@ModelName.text();
        def modelNodes = xmlNode.ModelReport.findAll {it.@ModelName.text() == expectedModelName}
        assertEquals (1, modelNodes.size());
        checkGlobalStatisticsResult (modelNodes[0], operationResults);
    }

    private void checkGlobalStatisticsResult(xmlNode, operationResults)
    {
        def numberOfOperations = xmlNode.@NumberOfOperations.text().toInteger();
        assertEquals (operationResults.size(), numberOfOperations)
        def expectedTotalDur = 0;
        operationResults.each{
            expectedTotalDur += it.operationDuration;
        }
        assertEquals ((int)(100000 * expectedTotalDur/Math.pow(10,9)), (int)(100000 * xmlNode.@TotalDuration.text().toDouble()))
        if(!operationResults.isEmpty())
        {
            assertEquals ((int)(100000 * expectedTotalDur/(numberOfOperations*Math.pow(10,9))), (int)(100000 * xmlNode.@AvarageDuration.text().toDouble()))
        }
    }

   
}