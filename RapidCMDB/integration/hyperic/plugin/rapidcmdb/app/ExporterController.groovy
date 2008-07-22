import org.hyperic.hq.appdef.server.session.PlatformManagerEJBImpl as PlatMan
import org.hyperic.hq.events.server.session.AlertManagerEJBImpl as AlertMan

import java.text.SimpleDateFormat
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.appdef.shared.AppdefEntityID
import org.hyperic.hq.appdef.shared.AppdefEntityValue
import org.hyperic.hq.authz.server.session.AuthzSubjectManagerEJBImpl
import org.hyperic.hq.authz.server.session.ResourceSortField
import org.hyperic.hq.events.server.session.AlertCondition
import org.hyperic.hq.events.server.session.AlertDefinition
import org.hyperic.hq.events.shared.AlertConditionValue
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqu.rendit.helpers.AlertHelper
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper
import org.hyperic.hq.hqu.rendit.util.HQUtil


class ExporterController
	extends BaseController
{
    def parameters
    def ExporterController() {
        setXMLMethods(['list'])
    }

    def list(xml, params) {
        parameters = params
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def platforms = resourceHelper.findPlatforms(pageInfo);
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        def plats        = rhelp.findAllPlatforms()
        def pMan = PlatMan.one
        def i = 0
        def ahelp = new AlertHelper(overlord)

        def aMan = AlertMan.one

        //urlfor(url:"/alerts/New.do?form=NewAlertDefinitionForm")
        //(url:"/alerts/New.do?form=NewAlertDefinitionForm")
        //pageInfo = new PageInfo(AlertSortField.DATE, true)
        //def alerts = ahelp.findAlerts(AlertSeverity.LOW, pageInfo)
/*        def newAlertConVal = new AlertConditionValue()
        newAlertConVal.setId(10007)
        newAlertConVal.setName("Availability")
        newAlertConVal.setComparator("==")
        newAlertConVal.setThreshold(1.0)
        newAlertConVal.setRequired(true)
        def newAlertCond = new AlertCondition()
        newAlertCond.setAlertConditionValue(newAlertConVal)
        def newAlertDef = new AlertDefinition(newAlertCond)
        newAlertDef.addCondition(newAlertCond)*/
   /*     def alertsx = aMan.findAllAlerts()
        def newAlertDef
        for (myAlert2 in alertsx) {
            def myAlert = aMan.findAlertById(myAlert2.id)
            newAlertDef = myAlert.alertDefinition
            break
        }

        for(myAlert2 in alertsx) {
            def myAlert = aMan.findAlertById(myAlert2.id)
            aMan.deleteAlerts(myAlert.id)
        }  */

     //   def newAlert = aMan.createAlert(newAlertDef, System.currentTimeMillis())








/*


//alertConditionValue={id=10001 type=1 required=true
//        measurementId=10002 name=Availability comparator=!= threshold=1.0 option= triggerId=10001}             */





        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':formatter.format(new Date())) {

            platforms.each { res ->
            	def plat = plats.getAt(i)
                def p = pMan.findPlatformById(res.instanceId)
                xml.platform('id':p.id, 'platform-ip':p.fqdn) {
                	for (metric in plat.enabledMetrics) {
                        def metricData = metric.lastDataPoint
                        xml.'metric'('name': metric.template.name,
                        		 	 'value': metricData.value,
                        		 	 'units': metric.template.units,
                        		 	 'time': formatter.format(new Date(metricData.timestamp)))
                	}

                	def servers = p.getServers().toList()
                    xml.servers() {
                		for (s2 in servers) {
     			        	def s = rhelp.find('server': s2.getId())
     			            xml.server(name: s.name, id: s.id) {
     			        		for (metric in s.enabledMetrics) {
     			        			def metricData = metric.lastDataPoint
     			                    if (metricData == null)
     			                        xml.metric(name: metric.template.name)
     			                    else
     			                        xml.metric(name: metric.template.name,
     			                                   value: metricData.value,
                                                   units: metric.template.units,
                                                   time: formatter.format(new Date(metricData.timestamp)))
     			                }

     			                def services = s2.getServices().toList()
				                xml.services() {
	                                for (svc2 in services) {
	                                	def svc = rhelp.find('service': svc2.getId())
	                                	xml.service(name: svc.name, id: svc.id) {
	                                		for (metric in svc.enabledMetrics) {
	                                			def metricData = metric.lastDataPoint
	                                			if (metricData == null)
	                                				xml.metric(name: metric.template.name)
	                                			else
	                                				xml.metric(name: metric.template.name,
	                                						   value: metricData.value,
	                                						   units: metric.template.units,
	                                						   time: formatter.format(new Date(metricData.timestamp)))
	                                        }
	                                    }
	                                }
	                            }
     			            }
     			        }
	                }
	            i++
                }
            }
            def alerts = aMan.findAllAlerts()
            xml.'allAlerts'('numberOfAlerts': alerts.getTotalSize()) {
                for (myAlert2 in alerts) {
                    def myAlert = aMan.findAlertById(myAlert2.id)
                    AlertDefinition alertDef = myAlert.getAlertDefinition()
                    AppdefEntityID aeid = new AppdefEntityID(alertDef.getAppdefType(), alertDef.getAppdefId())
                    AppdefEntityValue aev = new AppdefEntityValue(aeid, AuthzSubjectManagerEJBImpl.getOne().getOverlordPojo())

                    xml.'alert'('id': myAlert.id,
                                'alert_name': myAlert.alertDefinition.alertDefinitionValue.name,
                                'owner_name': aev.getName(),
                                'creationTime': formatter.format(new Date(myAlert.alertValue.ctime)),
                                'fixed': myAlert.fixed,
/*                                'reason': aMan.getShortReason(myAlert),
                                'long_reason': aMan.getLongReason(myAlert),
                                'other': myAlert.definition.alertType,
                                'other2': myAlert.definition.definitionInfo,
                                'other3': myAlert.definition.escalation,
                                'other4': myAlert.definition.getId(),
                                'other5': myAlert.alertDefinition.conditions.properties,*/
                    )/* {
                        for(x in myAlert.alertDefinition.conditions) {
                            xml.'condition'('value': x.properties)
                        }
                    }*/
                }
            }
        }


       xml
    }

}
