import org.hyperic.hq.appdef.server.session.ServerManagerEJBImpl as ServerMan
import org.hyperic.hq.appdef.server.session.PlatformManagerEJBImpl as PlatMan

import java.text.SimpleDateFormat
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.appdef.shared.ServerNotFoundException
import org.hyperic.hq.appdef.shared.PlatformNotFoundException
import org.hyperic.hq.authz.server.session.ResourceSortField
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper
import org.hyperic.hq.hqu.rendit.util.HQUtil
import org.hyperic.hq.hqu.rendit.metaclass.ResourceCategory
import org.hyperic.util.pager.PageControl

class ServerController
	extends BaseController
{
    def ServerController() {
        setXMLMethods(['list', 'get'])
    }

    def list(xml, params) {
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        //def servers2 = resourceHelper.findServers(pageInfo)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        def sMan = ServerMan.one
        def servers
        def servers2
        try {
            //servers        = sMan.getAllServers(overlord.authzSubjectValue, PageControl.PAGE_ALL)
            //servers = rhelp.findServers(pageInfo)
            servers = rhelp.findAllServers()
        }
        catch (ServerNotFoundException e) {
            servers = null
        }

        def i = 0
        def pMan = PlatMan.one
        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':new Date()) {
            xml.Servers() {
                if (servers != null) {
                    for (s in servers) {
                        def ss = rhelp.find('server': s.toServer().id)
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

                        def last_timestamp = 0
                        for (metric in ss.enabledMetrics) {
                            if (metric.template.name == "Availability") {
                            	if (metric.lastDataPoint != null)
                            		last_timestamp = metric.lastDataPoint.timestamp
                            	break;
                            }
                        }
                        xml.server(name: ss.name, id: ss.id, platform: platName, last_timestamp: last_timestamp) {
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
                        }
                        i++
                    }
                }
            }
        }
        xml
    }




    def get(xml, params) {
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        //def servers = resourceHelper.findServers(pageInfo)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        //def serverss        = rhelp.findAllServers()
        def sMan = ServerMan.one

        def serverName = params.getOne('name')
        def servers
        def servers2
        try {
            if (serverName != null) {
                //servers2 = sMan.findServersByName(overlord.authzSubjectValue, serverName)
                servers = sMan.getServersByName(overlord.authzSubjectValue, serverName)
            }
        }
        catch (ServerNotFoundException e) {
            servers = null
        }
        def pMan = PlatMan.one
        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':new Date()) {
            xml.Servers() {
                if (servers != null) {
                    for (s2 in servers) {
                        def s = rhelp.find('server': s2.getId())
                        
                        def platName
                        try {
                            platName = pMan.getPlatformByServer(overlord.authzSubjectValue, s2.id).getName()
                        }
                        catch (PlatformNotFoundException e) {
                            platName = "nur"
                        }

                        def last_timestamp = 0
                        for (metric in s.enabledMetrics) {
                            if (metric.template.name == "Availability") {
                            	if (metric.lastDataPoint != null)
                            		last_timestamp = metric.lastDataPoint.timestamp
                            	break;
                            }
                        }
                        xml.server(name: s.name, id: s.id, platform: platName, last_timestamp) {
                            for (metric in s.enabledMetrics) {
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
