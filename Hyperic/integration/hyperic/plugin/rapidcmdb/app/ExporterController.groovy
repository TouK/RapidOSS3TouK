import org.hyperic.hq.appdef.server.session.PlatformManagerEJBImpl as PlatMan
import org.hyperic.hq.events.server.session.AlertManagerEJBImpl as AlertMan
import org.hyperic.hq.appdef.server.session.ServerManagerEJBImpl as ServerMan

import java.text.SimpleDateFormat
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.appdef.shared.AppdefEntityID
import org.hyperic.hq.appdef.shared.AppdefEntityValue
import org.hyperic.hq.authz.server.session.AuthzSubjectManagerEJBImpl
import org.hyperic.hq.authz.server.session.ResourceSortField
import org.hyperic.hq.events.server.session.AlertDefinition
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqu.rendit.helpers.AlertHelper
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper
import org.hyperic.hq.hqu.rendit.util.HQUtil
import org.hyperic.hq.appdef.shared.PlatformNotFoundException
import org.hyperic.hq.appdef.shared.ServerNotFoundException
import org.hyperic.hq.authz.shared.AuthzConstants

class ExporterController
extends BaseController
{
    def ExporterController() {
        setXMLMethods(['list', 'platform', 'server', 'service', 'alert'])
    }


    def list(xml, params) {
        def overlord = HQUtil.overlord
        def rhelp = new ResourceHelper(overlord)
        def pMan = PlatMan.one
        def serverMan = ServerMan.one
        def plats = rhelp.findAllPlatforms()
        def servers = rhelp.findAllServers()
        def services = rhelp.findAllServices()

        def lasttimestamp = 0;

        xml.HypericObjects('timestamp': new Date().getTime()) {
            plats.each {plat ->
                def platform = plat.toPlatform();
                def modifiedTime = platform.modifiedTime
                if (modifiedTime >= lasttimestamp) {
                    xml.Platform(id: platform.id, name: platform.name, location: platform.location, description: platform.description)
                }
            }
            servers.each {s ->
                def server = s.toServer();
                def modifiedTime = server.modifiedTime;
                if (modifiedTime >= lasttimestamp) {
                    def platName = "";
                    try {
                        plat = pMan.getPlatformByServer(overlord.authzSubjectValue, s.toServer().id)
                        if (plat != null) {
                            platName = plat.getName()
                        }
                    }
                    catch (PlatformNotFoundException e) {
                    }
                    xml.Server(id: server.id, name: server.name, platform: platName, location: server.location, description: server.description)
                }

            }
            services.each {sv ->
                def service = sv.toService();
                def modifiedTime = service.modifiedTime;
                if (modifiedTime >= lasttimestamp) {
                    def serverName = ""
                    def platName = ""
                    try {
                        def server = serverMan.getServerByService(overlord.authzSubjectValue, service.id)
                        serverName = server.getName()
                        platName = pMan.getPlatformByServer(overlord.authzSubjectValue, server.id).getName()
                    }
                    catch (ServerNotFoundException se) {
                    }
                    catch (PlatformNotFoundException pe) {
                    }
                    xml.Service(id:service.id, name:service.name, server:serverName, platform:platName, location:service.location, description:service.description)
                }
            }
        }
        xml
    }





    def platform(xml, params) {

        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def platforms = resourceHelper.findPlatforms(pageInfo);
        def overlord = HQUtil.overlord
        def rhelp = new ResourceHelper(overlord)
        def plats = rhelp.findAllPlatforms()
        def pMan = PlatMan.one
        def i = 0
        def platName
        xml.'RapidCMDB'('source': 'Hyperic HQ', 'date': formatter.format(new Date())) {

            platforms.each {res ->
                def plat = plats.getAt(i)
                def p = pMan.findPlatformById(res.instanceId)
                xml.platform('id': p.id, 'platform-ip': p.fqdn) {
                    for (metric in plat.enabledMetrics) {
                        def metricData = metric.lastDataPoint
                        xml.'metric'('name': metric.template.name,
                                'value': metricData.value,
                                'units': metric.template.units,
                                'time': formatter.format(new Date(metricData.timestamp)))
                    }
                    i++
                }
            }
        }
        xml
    }




    def server(xml, params) {
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def servers = resourceHelper.findServers(pageInfo)
        def overlord = HQUtil.overlord
        def rhelp = new ResourceHelper(overlord)
        def serverss = rhelp.findAllServers()
        def pMan = PlatMan.one
        def i = 0
        xml.'RapidCMDB'('source': 'Hyperic HQ', 'date': formatter.format(new Date())) {
            xml.servers() {
                for (s in serverss) {
                    //def s = rhelp.find('server': s2.getId())
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
                    }
                }
            }
        }
        xml
    }




    def service(xml, params) {
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def overlord = HQUtil.overlord
        def rhelp = new ResourceHelper(overlord)
        def services = rhelp.findAllServices()
        def pMan = PlatMan.one
        def i = 0

        xml.'RapidCMDB'('source': 'Hyperic HQ', 'date': formatter.format(new Date())) {
            xml.services() {
                for (svc in services) {
                    //def svc = rhelp.find('service': svc2.getId())
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
        xml
    }






    def alert(xml, params) {
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def platforms = resourceHelper.findPlatforms(pageInfo);
        def overlord = HQUtil.overlord
        def rhelp = new ResourceHelper(overlord)
        def plats = rhelp.findAllPlatforms()
        def pMan = PlatMan.one
        def ahelp = new AlertHelper(overlord)

        def aMan = AlertMan.one
        xml.'RapidCMDB'('source': 'Hyperic HQ', 'date': formatter.format(new Date())) {
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
                           'long_reason': aMan.getLongReason(myAlert)*/
                    )
                }
            }
        }
        xml
    }
}
