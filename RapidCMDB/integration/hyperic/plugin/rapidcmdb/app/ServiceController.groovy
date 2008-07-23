import org.hyperic.hq.appdef.server.session.ServiceManagerEJBImpl as ServMan

import java.text.SimpleDateFormat
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.authz.server.session.ResourceSortField
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper
import org.hyperic.hq.hqu.rendit.util.HQUtil

class ServiceController
	extends BaseController
{
    def ServiceController() {
        setXMLMethods(['list', 'get'])
    }

    def list(xml, params) {
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        def services        = rhelp.findAllServices()

        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':formatter.format(new Date())) {
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

    def get(xml, params) {
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        def sMan = ServMan.one

        def serviceName = params.getOne('name')
        def services = sMan.findServicesByName(overlord.authzSubjectValue, serviceName)
        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':formatter.format(new Date())) {
            xml.services() {
                for (svc2 in services) {
                    def svc = rhelp.find('service': svc2.id)
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

}
