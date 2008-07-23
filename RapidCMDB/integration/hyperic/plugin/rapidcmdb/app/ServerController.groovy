import org.hyperic.hq.appdef.server.session.ServerManagerEJBImpl as ServerMan

import java.text.SimpleDateFormat
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.authz.server.session.ResourceSortField
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper
import org.hyperic.hq.hqu.rendit.util.HQUtil

class ServerController
	extends BaseController
{
    def ServerController() {
        setXMLMethods(['list', 'get'])
    }

    def list(xml, params) {
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def servers = resourceHelper.findServers(pageInfo)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        def serverss        = rhelp.findAllServers()
        def sMan = ServerMan.one
        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':formatter.format(new Date())) {
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

    def get(xml, params) {
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        //def servers = resourceHelper.findServers(pageInfo)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        //def serverss        = rhelp.findAllServers()
        def sMan = ServerMan.one

        def serverName = params.getOne('name')
        def servers = sMan.findServersByName(overlord.authzSubjectValue, serverName)
        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':formatter.format(new Date())) {
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
                    }
                }
            }
        }
        xml
    }

}
