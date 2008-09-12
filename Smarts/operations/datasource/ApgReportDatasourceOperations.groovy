package datasource

import com.ifountain.apg.datasource.ApgReportAdapter
import org.apache.log4j.Logger
import com.watch4net.apg.v2.remote.sample.jaxws.report.ReportProperties
import com.watch4net.apg.v2.remote.sample.jaxws.report.ReportProperty
import com.watch4net.apg.v2.remote.sample.jaxws.report.ReportPropertyKey
import com.watch4net.apg.v2.remote.sample.jaxws.report.RealNode
import com.watch4net.apg.v2.remote.sample.jaxws.report.Formula
import com.watch4net.apg.v2.remote.sample.jaxws.report.FormulaSettingDefinition
import com.watch4net.apg.v2.remote.sample.jaxws.report.FormulaResultDefinition
import com.watch4net.apg.v2.remote.sample.jaxws.report.LinkedNode
import com.watch4net.apg.v2.remote.sample.jaxws.report.EmptyFormulaParameterDefinition
import com.watch4net.apg.v2.remote.sample.jaxws.report.FilterFormulaParameterDefinition
import com.watch4net.apg.v2.remote.sample.jaxws.report.ConstantFormulaParameterDefinition
import com.watch4net.apg.v2.remote.sample.jaxws.report.PropertyFormulaParameterDefinition
import com.watch4net.apg.v2.remote.sample.jaxws.report.ResultFormulaParameterDefinition
import com.watch4net.apg.v2.remote.sample.jaxws.report.CombinedFormulaParameterDefinition
import com.watch4net.apg.v2.remote.sample.jaxws.report.RuntimePreferences
import com.watch4net.apg.v2.remote.sample.jaxws.report.ReportPreferences
import com.watch4net.apg.v2.remote.sample.jaxws.report.ReportMode
import com.watch4net.apg.v2.remote.sample.jaxws.report.AggregationFunction
import com.watch4net.apg.v2.remote.sample.jaxws.report.GraphInfoDisplayMode
import com.watch4net.apg.v2.remote.sample.jaxws.report.DisplayPreferences
import com.watch4net.apg.v2.remote.sample.jaxws.report.DisplayPolicy
import com.watch4net.apg.v2.remote.sample.jaxws.report.NodeFilter
import com.watch4net.apg.v2.remote.sample.jaxws.report.NodeExpansion
import com.watch4net.apg.v2.remote.sample.jaxws.report.FilterMode
import com.watch4net.apg.v2.remote.sample.jaxws.report.PropertyReplaceSetting
import com.watch4net.apg.v2.remote.sample.jaxws.report.NodePropertyNodeColumn
import com.watch4net.apg.v2.remote.sample.jaxws.report.PropertyNodeColumn
import com.watch4net.apg.v2.remote.sample.jaxws.report.SortMode
import com.watch4net.apg.v2.remote.sample.jaxws.report.ValueNodeColumn
import com.watch4net.apg.v2.remote.sample.jaxws.report.ValueFormatter
import com.watch4net.apg.v2.remote.sample.jaxws.report.ScaleOperation
import com.watch4net.apg.v2.remote.sample.jaxws.report.GraphElement
import com.watch4net.apg.v2.remote.sample.jaxws.report.ErrorElement
import javax.xml.ws.Holder
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 6:07:30 PM
 * To change this template use File | Settings | File Templates.
 */
class ApgReportDatasourceOperations extends BaseDatasourceOperations {
    def adapter;
    def onLoad() {
        this.adapter = new ApgReportAdapter(getProperty("connection").name, reconnectInterval * 1000, Logger.getRootLogger());
    }

    def getReport(username, password, properties, node) {
        ReportProperties reportProperties = new ReportProperties();
        properties.each {key, value ->
            ReportProperty p = new ReportProperty();
            p.setKey(ReportPropertyKey.fromValue(key));
            p.setValue(value);
            reportProperties.getProperty().add(p);
        }
        RealNode root = __addNode(node, null);
        Holder<GraphElement> hg = new Holder<GraphElement>();
        Holder<ErrorElement> he = new Holder<ErrorElement>();
        port.getReport(properties, root, null, hg, he, null, null);

        if (he.value != null) {
            // the report generated an error !
            throw new Exception(he.value.getMessage());
        } else if (hg.value != null) {
            // we got the graph
            def image = (BufferedImage) hg.value.getGraph();
            def id = hg.value.getId();
            def url = "reports/${id}.png"
            ImageIO.write(image, "png", new File("web-app/${url}"))
            return url;
        } else {
            // wow, this is pretty unexpected...
            throw new Exception("There's no graph in the response !");
        }

    }

    def __addNode(nodeMap, parentNode) {
        RealNode node = new RealNode();
        if (nodeMap["name"] != null) {
            node.setName(nodeMap["name"])
        }
        if (nodeMap["type"] != null) {
            node.setType(nodeMap["type"])
        }
        if (nodeMap["properties"] != null) {
            nodeMap.properties.each {p ->
                def propType = p["type"];
                switch (propType) {
                    case "RuntimePreferences":
                        def timeRangeExpression = p["timeRangeExpression"]
                        def selectedVariablesString = p["selectedVariablesString"]
                        RuntimePreferences runtimePreferences = new RuntimePreferences();
                        runtimePreferences.setTimeRangeExpression(timeRangeExpression)
                        runtimePreferences.setSelectedVariablesString(selectedVariablesString)
                        node.getProperty().add(runtimePreferences);
                        break;
                    case "ReportPreferences":
                        node.getProperty().add(__getReportPreferences(p))
                        break;
                    case "DisplayPreferences":
                        def browsingDisabled = p["browsingDisabled"]
                        def displayPolicy = p["displayPolicy"]
                        DisplayPreferences dp = new DisplayPreferences();
                        dp.setBrowsingDisabled(browsingDisabled)
                        if (displayPolicy != null) {
                            dp.setDisplayPolicy(DisplayPolicy.fromValue(displayPolicy))
                        }
                        node.getProperty().add(dp)
                        break;
                    case "NodeFilter":
                        def filterExpression = p["filterExpression"]
                        def selectUnmatched = p["selectUnmatched"]
                        NodeFilter nf = new NodeFilter();
                        nf.setFilterExpression(filterExpression)
                        nf.setSelectUnmatched(selectUnmatched)
                        node.getProperty().add(nf)
                        break;
                    case "NodeExpansion":
                        def expandOn = p["expandOn"]
                        def filterMode = p["filterMode"]
                        NodeExpansion ne = new NodeExpansion();
                        ne.setExpandOn(expandOn)
                        if (filterMode != null) {
                            ne.setFilterMode(FilterMode.fromValue(filterMode))
                        }
                        node.getProperty().add(ne)
                        break;
                    case "PropertyReplaceSetting":
                        def pattern = p["pattern"]
                        def target = p["target"]
                        PropertyReplaceSetting prs = new PropertyReplaceSetting();
                        prs.setPattern(pattern)
                        prs.setTarget(target)
                        node.getProperty().add(prs);
                        break;
                    case "NodePropertyNodeColumn":
                        def name = p["name"]
                        def columnFilterCondition = p["columnFilterCondition"]
                        def sortMode = p["sortMode"]
                        def nodeProperty = p["nodeProperty"]
                        NodePropertyNodeColumn npnc = new NodePropertyNodeColumn();
                        npnc.setName(name)
                        npnc.setColumnFilterCondition(columnFilterCondition)
                        if (sortMode != null) {
                            npnc.setSortMode(SortMode.fromValue(sortMode))
                        }
                        npnc.setNodeProperty(nodeProperty);
                        node.getProperty().add(npnc);
                        break;
                    case "PropertyNodeColumn":
                        def property = p["property"]
                        def limit = p["limit"]
                        def name = p["name"]
                        def columnFilterCondition = p["columnFilterCondition"]
                        def sortMode = p["sortMode"]
                        PropertyNodeColumn pnc = new PropertyNodeColumn()
                        pnc.setName(name)
                        pnc.setColumnFilterCondition(columnFilterCondition)
                        if (sortMode != null) {
                            pnc.setSortMode(SortMode.fromValue(sortMode))
                        }
                        pnc.setProperty(property)
                        pnc.setLimit(limit);
                        break;
                    case "ValueNodeColumn":
                        node.getProperty().add(__getValueNodeColumn(p))
                        break;
                }
            }
        }

        if (nodeMap["formulas"] != null) {
            Formula f = new Formula();
            nodeMap.formulas.each {formula ->
                def settings = formula["settings"];
                if (settings != null) {
                    settings.each {key, value ->
                        FormulaSettingDefinition sd = new FormulaSettingDefinition();
                        sd.setName(key)
                        sd.setValue(value)
                        f.getSetting().add(sd);
                    }
                }
                def results = formula["results"];
                if (results != null) {
                    results.each {result ->
                        FormulaResultDefinition rd = new FormulaResultDefinition();
                        rd.setName(result["name"])
                        rd.setDefault(result["default"])
                        rd.setGraphable(result["graphable"])
                        f.getResult().add(rd);
                    }
                }
                def parameters = formula["parameters"];
                if (parameters != null) {
                    parameters.each {param ->
                        __addFormulaParameter(param, f.getParameter())
                    }

                }
            }
            node.getFormula().add(f);
        }
        if (nodeMap["links"] != null) {
            nodeMap.links.each {
                LinkedNode linkNode = new LinkedNode();
                linkNode.setLinkID(it)
                node.getLinkOrNode().add(linkNode);
            }
        }
        if (nodeMap["nodes"] != null) {
            nodeMap.nodes.each {
                __addNode(node, it)
            }
        }
        if (parentNode != null) {
            parentNode.getLinkOrNode().add(node);
        }
        return node;
    }

    def __addFormulaParameter(param, paramList) {
        def paramType = param["type"]
        def paramName = param["name"]
        switch (paramType) {
            case "Empty":
                EmptyFormulaParameterDefinition pDef = new EmptyFormulaParameterDefinition();
                pDef.setName(paramName);
                paramList.add(pDef)
                break;
            case "Filter":
                def filter = param["filter"];
                FilterFormulaParameterDefinition fDef = new FilterFormulaParameterDefinition()
                fDef.setName(paramName);
                fDef.setFilter(filter);
                paramList.add(fDef);
                break;
            case "Constant":
                def value = param["value"]
                ConstantFormulaParameterDefinition cDef = new ConstantFormulaParameterDefinition();
                cDef.setName(paramName)
                cDef.setValue(value)
                paramList.add(cDef);
                break;
            case "Property":
                def property = param["property"]
                PropertyFormulaParameterDefinition pDef = new PropertyFormulaParameterDefinition();
                pDef.setName(paramName);
                pDef.setProperty(property)
                paramList.add(pDef)
                break;
            case "Result":
                def result = param["result"]
                ResultFormulaParameterDefinition rDef = new ResultFormulaParameterDefinition();
                rDef.setName(paramName);
                rDef.setResult(result)
                paramList.add(rDef)
                break;
            case "Combined":
                def parameters = param["parameters"];
                if (parameters != null) {
                    CombinedFormulaParameterDefinition cDef = new CombinedFormulaParameterDefinition();
                    cDef.setName(paramName);
                    parameters.each {
                        __addFormulaParameter(it, cDef.getParameter());
                    }
                    paramList.add(cDef)
                }
                break;
        }
    }

    def __getReportPreferences(propertyMap) {
        ReportPreferences rp = new ReportPreferences();
        def displayMode = propertyMap["displayMode"];
        def duration = propertyMap["duration"];
        def timeZoneId = propertyMap["timeZoneId"];
        def preferredPeriod = propertyMap["preferredPeriod"];
        def timeFilterExpression = propertyMap["timeFilterExpression"];
        def displayedProperties = propertyMap["displayedProperties"];
        def description = propertyMap["description"];
        def legendProperties = propertyMap["legendProperties"];
        def displayUnselectedVariables = propertyMap["displayUnselectedVariables"];
        def criticalThreshold = propertyMap["criticalThreshold"];
        def majorThreshold = propertyMap["majorThreshold"];
        def maxValue = propertyMap["maxValue"];
        def minValue = propertyMap["minValue"];
        def graphScaleFactor = propertyMap["graphScaleFactor"];
        def paging = propertyMap["paging"];
        def treePaging = propertyMap["treePaging"];
        def propertyLimit = propertyMap["propertyLimit"];
        def dataLifeTime = propertyMap["dataLifeTime"];
        def defaultMode = propertMap["defaultMode"]
        def preferredAggregate = propertMap["preferredAggregate"]
        def graphInfoDisplayMode = propertMap["graphInfoDisplayMode"]
        rp.setDisplayMode(displayMode);
        rp.setDuration(duration)
        rp.setTimeZoneId(timeZoneId);
        rp.setPreferredPeriod(preferredPeriod)
        rp.setTimeFilterExpression(timeFilterExpression)
        rp.setDisplayedProperties(displayedProperties)
        rp.setDescription(description)
        rp.setLegendProperties(legendProperties)
        rp.setDisplayUnselectedVariables(displayUnselectedVariables)
        rp.setCriticalThreshold(criticalThreshold)
        rp.setMajorThreshold(majorThreshold)
        rp.setMaxValue(maxValue)
        rp.setMinValue(minValue)
        rp.setGraphScaleFactor(graphScaleFactor)
        rp.setPaging(paging)
        rp.setTreePaging(treePaging)
        rp.setPropertyLimit(propertyLimit)
        rp.setDataLifeTime(dataLifeTime)
        if (defaultMode != null) {
            rp.setDefaultMode(ReportMode.fromValue(defaultMode));
        }
        if (preferredAggregate != null) {
            rp.setPreferredAggregate(AggregationFunction.fromValue(preferredAggregate))
        }
        if (graphInfoDisplayMode != null) {
            rp.setGraphInfoDisplayMode(GraphInfoDisplayMode.fromValue(graphInfoDisplayMode))
        }
        return rp;
    }

    def __getValueNodeColumn(propertyMap) {
        ValueNodeColumn vnc = new ValueNodeColumn();
        def name = propertyMap["name"]
        def columnFilterCondition = propertyMap["columnFilterCondition"]
        def sortMode = propertyMap["sortMode"]
        def majorLevel = propertyMap["sortMode"]
        def criticalLevel = propertyMap["criticalLevel"]
        def nonCriticalHidden = propertyMap["nonCriticalHidden"]
        def nonMajorHidden = propertyMap["nonMajorHidden"]
        def resultName = propertyMap["resultName"]
        def useComplement = propertyMap["useComplement"]
        def replaceNullWithZero = propertyMap["replaceNullWithZero"]
        def forcePeriod = propertyMap["forcePeriod"]
        def perLineTimeFilter = propertyMap["perLineTimeFilter"]
        def criticityAsc = propertyMap["criticityAsc"]
        def period = propertyMap["period"]
        def durationOverride = propertyMap["durationOverride"]
        def useTimeRange = propertyMap["useTimeRange"]
        def timeThreshold = propertyMap["timeThreshold"]
        def roundingAccuracy = propertyMap["roundingAccuracy"]
        def filterExpression = propertyMap["filterExpression"]
        def scaleFactor = propertyMap["scaleFactor"]
        def formatter = propertyMap["formatter"]
        def aggregationFunc = propertyMap["aggregationFunc"]
        def valuesAggregationFunc = propertyMap["valuesAggregationFunc"]
        def scaleOperation = propertyMap["scaleOperation"]

        vnc.setName(name);
        vnc.setColumnFilterCondition(columnFilterCondition)
        vnc.setMajorLevel(majorLevel)
        vnc.setCriticalLevel(criticalLevel)
        vnc.setNonCriticalHidden(nonCriticalHidden)
        vnc.setNonMajorHidden(nonMajorHidden)
        vnc.setResultName(resultName)
        vnc.setUseComplement(useComplement)
        vnc.setReplaceNullWithZero(replaceNullWithZero)
        vnc.setForcePeriod(forcePeriod)
        vnc.setPerLineTimeFilter(perLineTimeFilter)
        vnc.setCriticityAsc(criticityAsc)
        vnc.setPeriod(period)
        vnc.setDurationOverride(durationOverride)
        vnc.setUseTimeRange(useTimeRange)
        vnc.setTimeThreshold(timeThreshold)
        vnc.setRoundingAccuracy(roundingAccuracy)
        vnc.setFilterExpression(filterExpression)
        vnc.setScaleFactor(scaleFactor)
        if (sortMode != null) {
            vnc.setSortMode(SortMode.fromValue(sortMode))
        }
        if (formatter != null) {
            vnc.setFormatter(ValueFormatter.fromValue(formatter))
        }
        if (aggregationFunc != null) {
            vnc.setAggregationFunc(AggregationFunction.fromValue(aggregationFunc))
        }
        if (valuesAggregationFunc != null) {
            vnc.setValuesAggregationFunc(AggregationFunction.fromValue(valuesAggregationFunc))
        }
        if (scaleOperation != null) {
            vnc.setScaleOperation(ScaleOperation.fromValue(scaleOperation))
        }
        return vnc;
    }

}