import org.hyperic.hq.appdef.server.session.ServiceManagerEJBImpl as ServMan
import org.hyperic.hq.appdef.server.session.ServerManagerEJBImpl as ServerMan
import org.hyperic.hq.appdef.server.session.PlatformManagerEJBImpl as PlatMan

import java.text.SimpleDateFormat
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.appdef.shared.ServiceNotFoundException
import org.hyperic.hq.appdef.shared.ServerNotFoundException
import org.hyperic.hq.appdef.shared.PlatformNotFoundException
import org.hyperic.hq.authz.server.session.ResourceSortField
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper
import org.hyperic.hq.hqu.rendit.util.HQUtil
import org.hyperic.util.pager.PageControl


class ServiceController
	extends BaseController
{
    def ServiceController() {
        setXMLMethods(['list', 'get'])
    }

    def list(xml, params) {
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        def sMan = ServMan.one
        def sMan2 = ServerMan.one
        def pMan = PlatMan.one

        def services
        try {
            //services = rhelp.findAllServices()
            services = sMan.getAllServices(overlord.authzSubjectValue, PageControl.PAGE_ALL)
        }
        catch (ServiceNotFoundException e) {
            services = null
        }

        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':new Date()) {
            xml.Services() {
                if (services != null) {
                    for (svc2 in services) {
                        def svc = rhelp.find('service': svc2.getId())

                        if (svc == null) {
                            xml.hata('hata1': 232323)
                            break;
                        }

                        def server
                        def serverName
                        def platName
                        try {
                            server = sMan2.getServerByService(overlord.authzSubjectValue, svc2.id)
                            serverName = server.getName()
                            platName = pMan.getPlatformByServer(overlord.authzSubjectValue, server.id).getName()
                        }
                        catch (ServerNotFoundException se) {
                            serverName = ""
                            platName = ""
                        }
                        catch (PlatformNotFoundException pe) {
                            platName = ""
                        }

                        //sMan2.getServerResourceValue 
                        def last_timestamp = 0
                        for (metric in svc.enabledMetrics) {
                            if (metric.template.name == "Availability") {
                            	if (metric.lastDataPoint != null)
                            		last_timestamp = metric.lastDataPoint.timestamp
                            	break;
                            }
                        }
                        xml.service(name: svc.name, id: svc.id, server: serverName, platform: platName, last_timestamp: last_timestamp) {
                            for (metric in svc.enabledMetrics) {
                                def metricData = metric.lastDataPoint
                                if (metricData == null) {
                                    xml.metric(name: metric.template.name)
                                }
                                else
                                    xml.metric(name: metric.template.name,
                                               value: metricData.value,
                                               units: metric.template.units,
                                               time: metricData.timestamp)
                            }
                        }
                    }
                }
            }
        }
       xml
    }


    def get(xml, params) {
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        def sMan = ServMan.one
        def sMan2 = ServerMan.one
        def pMan = PlatMan.one

        def serviceName = params.getOne('name')
        def services
        try {
            if (serviceName != null) {
                services = sMan.findServicesByName(overlord.authzSubjectValue, serviceName)
            }
        }
        catch (ServiceNotFoundException e) {
            services = null
        }
        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':new Date()) {
            xml.Services() {
                if (services != null) {
                    for (svc2 in services) {
                        def svc = rhelp.find('service': svc2.id)
      
                        def server
                        def serverName
                        def platName
                        try {
                            server = sMan2.getServerByService(overlord.authzSubjectValue, svc2.id)
                            serverName = server.getName()
                            platName = pMan.getPlatformByServer(overlord.authzSubjectValue, server.id).getName()
                        }
                        catch (ServerNotFoundException se) {
                            serverName = ""
                            platName = ""
                        }
                        catch (PlatformNotFoundException pe) {
                            platName = ""
                        }
                        def last_timestamp = 0
                        for (metric in svc.enabledMetrics) {
                            if (metric.template.name == "Availability") {
                            	if (metric.lastDataPoint != null)
                            		last_timestamp = metric.lastDataPoint.timestamp
                            	break;
                            }
                        }
                        
                        xml.service(name: svc.name, id: svc.id, server: serverName, platform: platName, last_timestamp: last_timestamp) {
                            for (metric in svc.enabledMetrics) {
                                def metricData = metric.lastDataPoint
                                if (metricData == null)
                                    xml.metric(name: metric.template.name)
                                else
                                    xml.metric(name: metric.template.name,
                                               value: metricData.value,
                                               units: metric.template.units,
                                               time: metricData.timestamp)
                            }
                        }
                    }
                }
            }
        }
       xml
    }

}
