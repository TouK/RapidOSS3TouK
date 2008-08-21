import org.hyperic.hq.appdef.server.session.ServiceManagerEJBImpl as serviceMan
import org.hyperic.hq.appdef.server.session.ServerManagerEJBImpl as serverMan
import org.hyperic.hq.appdef.server.session.PlatformManagerEJBImpl as PlatMan

import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.appdef.shared.PlatformNotFoundException
import org.hyperic.hq.appdef.shared.ServerNotFoundException
import org.hyperic.hq.appdef.shared.ServiceNotFoundException
import org.hyperic.hq.authz.server.session.ResourceSortField
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper
import org.hyperic.hq.hqu.rendit.util.HQUtil
import org.hyperic.util.pager.PageControl

class StatusController
	extends BaseController
{
    def StatusController() {
        setXMLMethods(['list', 'detail'])
    }

    def list(xml, params) {
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)

        def begins = params.getOne('begin')
        def platforms
        def plats
        try {
            plats  = rhelp.findAllPlatforms()
            platforms = resourceHelper.findPlatforms(pageInfo)
        }
        catch (PlatformNotFoundException e) {
            platforms = null
        }
        def pMan = PlatMan.one
        def servMan = serviceMan.one
        def i = 0

        xml.HypericObjects('timestamp':new Date().getTime()) {
            plats.each { plat -> //res ->
                //def plat = plats.getAt(i)
                def p = pMan.findPlatformById(plat.instanceId) //res
                def last_timestamp = "0"
                def status = null
                for (metric in plat.enabledMetrics) {
                    if (metric.template.name == "Availability") {
                        if (metric.lastDataPoint != null) {
                            last_timestamp = metric.lastDataPoint.timestamp.toString()
                            status = metric.lastDataPoint.value
                        }
                        break;
                    }
                }
                if (begins <= last_timestamp)
                    xml.HypericObject(type: "platform", name: p.name, Availability: status)
            }

            def servers
            try {
                servers = rhelp.findAllServers()
            }
            catch (ServerNotFoundException e) {
                servers = null
            }

            if (servers != null) {
                for (s in servers) {
                    def ss = rhelp.find('server': s.toServer().id)
                    def last_timestamp = "0"
                    def status = null
                    for (metric in ss.enabledMetrics) {
                        if (metric.template.name == "Availability") {
                            if (metric.lastDataPoint != null)
                                last_timestamp = metric.lastDataPoint.timestamp.toString()
                                status = metric.lastDataPoint.value
                            break;
                        }
                    }
                    if (begins <= last_timestamp)
                        xml.HypericObject(type: "server", name: ss.name, Availability: status)
                }
            }

            def services
            try {
                services = servMan.getAllServices(overlord.authzSubjectValue, PageControl.PAGE_ALL)
            }
            catch (ServiceNotFoundException e) {
                services = null
            }

            if (services != null) {
                for (svc2 in services) {
                    def svc = rhelp.find('service': svc2.getId())

                    def last_timestamp = "0"
                    def status = null
                    for (metric in svc.enabledMetrics) {
                        if (metric.template.name == "Availability") {
                            if (metric.lastDataPoint != null)
                                last_timestamp = metric.lastDataPoint.timestamp.toString()
                                status = metric.lastDataPoint.value
                            break;
                        }
                    }
                    if (begins <= last_timestamp)
                        xml.HypericObject(type: "service", name: svc.name, Availability: status)
                }
            }
        }
        xml
    }


    def detail(xml, params) {
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)

        def begins = params.getOne('begin')
        def platforms
        def plats
        try {
            plats = rhelp.findAllPlatforms()
            platforms = resourceHelper.findPlatforms(pageInfo)
        }
        catch (PlatformNotFoundException e) {
            platforms = null
        }
        def pMan = PlatMan.one
        def serMan = serverMan.one
        def servMan = serviceMan.one

        xml.HypericObjects('timestamp':new Date().getTime()) {
            plats.each { plat -> //res ->
                //def plat = plats.getAt(i)

                def last_timestamp = "0"
                for (metric in plat.enabledMetrics) {
                    if (metric.template.name == "Availability") {
                        if (metric.lastDataPoint != null)
                            last_timestamp = metric.lastDataPoint.timestamp.toString()
                        break;
                    }
                }

                if (begins <= last_timestamp || last_timestamp == "0") {
                    def p = pMan.findPlatformById(plat.instanceId)
                    xml.HypericObject(type: "platform", name: p.name)
                }
            }

            def servers
            try {
                servers = rhelp.findAllServers()
            }
            catch (ServerNotFoundException e) {
                servers = null
            }

            if (servers != null) {
                for (s in servers) {
                    def ss = rhelp.find('server': s.toServer().id)
                    def last_timestamp = "0"
                    for (metric in ss.enabledMetrics) {
                        if (metric.template.name == "Availability") {
                            if (metric.lastDataPoint != null)
                                last_timestamp = metric.lastDataPoint.timestamp.toString()
                            break;
                        }
                    }

                    if (begins <= last_timestamp || last_timestamp == "0") {
                        def platName
                        def plat
                        try {
                            plat = pMan.getPlatformByServer(overlord.authzSubjectValue, s.toServer().id)
                            if (plat != null)
                                platName = plat.getName()
                            else platName = ""
                        }
                        catch (PlatformNotFoundException e) {
                            platName = ""
                        }

                        xml.HypericObject(type: "server", name: ss.name, platform: platName)
                    }
                }
            }


            def services
            try {
                //services = rhelp.findAllServices()
                services = servMan.getAllServices(overlord.authzSubjectValue, PageControl.PAGE_ALL)
            }
            catch (ServiceNotFoundException e) {
                services = null
            }

            if (services != null) {
                for (svc2 in services) {
                    def svc = rhelp.find('service': svc2.getId())
                    def last_timestamp = "0"
                    for (metric in svc.enabledMetrics) {
                        if (metric.template.name == "Availability") {
                            if (metric.lastDataPoint != null)
                                last_timestamp = metric.lastDataPoint.timestamp.toString()
                            break;
                        }
                    }

                    if (begins <= last_timestamp || last_timestamp == "0") {
                        def server
                        def serverName
                        def platName
                        try {
                            server = serMan.getServerByService(overlord.authzSubjectValue, svc2.id)
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


                        xml.HypericObject(type: "service", name: svc.name, server: serverName, platform: platName)
                    }
                }
            }
        }
        xml
    }
}