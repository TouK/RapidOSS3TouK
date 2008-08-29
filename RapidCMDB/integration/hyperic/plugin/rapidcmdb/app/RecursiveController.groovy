import org.hyperic.hq.appdef.server.session.PlatformManagerEJBImpl as PlatMan
import org.hyperic.hq.appdef.server.session.ServerManagerEJBImpl as serverMan
import org.hyperic.hq.appdef.server.session.ServiceManagerEJBImpl as serviceMan

import org.hyperic.hibernate.PageInfo
import org.hyperic.util.pager.PageControl
import org.hyperic.hq.appdef.shared.PlatformNotFoundException
import org.hyperic.hq.authz.server.session.ResourceSortField
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper
import org.hyperic.hq.hqu.rendit.util.HQUtil

class RecursiveController
	extends BaseController
{
    def RecursiveController() {
        setXMLMethods(['list'])
    }

    def list(xml, params) {
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        def platforms
        def plats
        try {
            plats        = rhelp.findAllPlatforms()
            platforms = resourceHelper.findPlatforms(pageInfo)
        }
        catch (PlatformNotFoundException e) {
            platforms = null
        }
        def pMan = PlatMan.one
        def serMan = serverMan.one
        def servMan = serviceMan.one
        def i = 0
        assert plats.size() == platforms.size()

        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':new Date()) {
            xml.Platforms() {
                for(res in platforms) {
                    def plat = plats.getAt(i)
                    def p = pMan.findPlatformById(res.instanceId)
                    if (p == null) {
	                    i++
                    	break;
                	}
                    def last_timestamp = 0
                    for (metric in plat.enabledMetrics) {
                        if (metric.template.name == "Availability") {
                            if (metric.lastDataPoint != null)
                                last_timestamp = metric.lastDataPoint.timestamp
                            break;
                        }
                    }

                    xml.platform('id':p.id, 'name': p.name, 'ip':p.fqdn, 'last_timestamp': last_timestamp) {
                        for (metric in plat.enabledMetrics) {
                            def metricData = metric.lastDataPoint
                            xml.'metric'('name': metric.template.name,
                                         'value': metricData.value,
                                         'units': metric.template.units,
                                         'time': metricData.timestamp)
                        }


                        def servers = p.getServers().toList()
                        //def servers = serMan.getServersByPlatform(overlord.authzSubjectValue, p.id, false, PageControl.PAGE_ALL)
                        xml.Servers() {
                            for (s in servers) {
                                def ss = rhelp.find('server': s.id)
                                def server_last_timestamp = 0
			                    for (metric in ss.enabledMetrics) {
			                        if (metric.template.name == "Availability") {
			                            if (metric.lastDataPoint != null)
			                                server_last_timestamp = metric.lastDataPoint.timestamp
			                            break;
			                        }
			                    }
                                xml.server(name: ss.name, id: ss.id, platform: p.name, last_timestamp: server_last_timestamp) {
                                    for (metric in ss.enabledMetrics) {
                                        def metricData = metric.lastDataPoint
                                        if (metricData == null)
                                            xml.metric(name: metric.template.name)
                                        else
                                            xml.metric(name: metric.template.name,
                                                       value: metricData.value,
                                                       units: metric.template.units,
                                                       time: metricData.timestamp)
                                    }

                                    def services = s.getServices().toList()
                                    //def services = servMan.getServicesByServer(overlord.authzSubjectValue, p.id, PageControl.PAGE_ALL)
                                    xml.Services() {
                                        for (svc2 in services) {
                                            def svc = rhelp.find('service': svc2.getId())
                                            def service_last_timestamp = 0
						                    for (metric in svc.enabledMetrics) {
						                        if (metric.template.name == "Availability") {
						                            if (metric.lastDataPoint != null)
						                                service_last_timestamp = metric.lastDataPoint.timestamp
						                            break;
						                        }
						                    }
                                            xml.service(name: svc.name, id: svc.id, server: s.name, platform: p.name, last_timestamp: service_last_timestamp) {
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
                        }
                    }
                    i++
                }
            }
        }
        xml
    }
}
