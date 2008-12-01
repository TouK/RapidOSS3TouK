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
        def overlord = HQUtil.overlord
        def rhelp = new ResourceHelper(overlord)

        def lasttimestamp = 0;
        def timestampParam = params.getOne('lasttimestamp')
        if (timestampParam != null) {
            lasttimestamp = Long.parseLong(timestampParam);
        }

        def plats = rhelp.findAllPlatforms()
        def servers = rhelp.findAllServers()
        def services = rhelp.findAllServices()

        xml.HypericObjects('timestamp': new Date().getTime()) {
            xml.Platforms() {
                plats.each {plat ->
                    def platform = plat.toPlatform();
                    def status = null
                    def last_timestamp = 0
                    for (metric in plat.enabledMetrics) {
                        if (metric.template.name == "Availability") {
                            if (metric.lastDataPoint != null) {
                                last_timestamp = metric.lastDataPoint.timestamp
                                status = metric.lastDataPoint.value
                            }
                            break;
                        }
                    }
                    if (lasttimestamp <= last_timestamp)
                        xml.Platform(id: platform.id, name: platform.name, Availability: status)
                }
            }
            xml.Servers() {
                servers.each {serv ->
                    def server = serv.toServer();
                    def status = null
                    def last_timestamp = 0
                    for (metric in serv.enabledMetrics) {
                        if (metric.template.name == "Availability") {
                            if (metric.lastDataPoint != null) {
                                last_timestamp = metric.lastDataPoint.timestamp
                                status = metric.lastDataPoint.value
                            }
                            break;
                        }
                    }
                    if (lasttimestamp <= last_timestamp)
                        xml.Server(id: server.id, name: server.name, Availability: status)
                }
            }
            xml.Services() {
                services.each {serv ->
                    def service = serv.toService();
                    def status = null
                    def last_timestamp = 0
                    for (metric in serv.enabledMetrics) {
                        if (metric.template.name == "Availability") {
                            if (metric.lastDataPoint != null) {
                                last_timestamp = metric.lastDataPoint.timestamp
                                status = metric.lastDataPoint.value
                            }
                            break;
                        }
                    }
                    if (lasttimestamp <= last_timestamp)
                        xml.Service(id: service.id, name: service.name, Availability: status)
                }
            }
        }
        xml
    }
}