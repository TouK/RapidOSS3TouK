import org.hyperic.hq.appdef.server.session.PlatformManagerEJBImpl as PlatMan

import java.text.SimpleDateFormat
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.authz.server.session.ResourceSortField
import org.hyperic.hq.appdef.server.session.Platform
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper
import org.hyperic.hq.hqu.rendit.util.HQUtil

import org.hyperic.hq.authz.server.session.ResourceManagerEJBImpl as ResMan

class PlatformController
	extends BaseController
{
    def PlatformController() {
        setXMLMethods(['list', 'get'])
    }

    def list(xml, params) {
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def platforms = resourceHelper.findPlatforms(pageInfo);
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        def plats        = rhelp.findAllPlatforms()
        def pMan = PlatMan.one
        def i = 0
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
	            i++
                }
            }
        }
       xml
    }

    def get(xml, params) {
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def overlord     = HQUtil.overlord
        def rhelp = new ResourceHelper(overlord)
        def pMan = PlatMan.one
        def platName = params.getOne('name')
        def platform = pMan.getPlatformByName(platName)

        def plat = rhelp.find('platform': platform.id)
        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':formatter.format(new Date())) {
            def p = pMan.findPlatformById(plat.instanceId)
            xml.platform('id':p.id, 'platform-ip':p.fqdn) {
                for (metric in plat.enabledMetrics) {
                    def metricData = metric.lastDataPoint
                    xml.'metric'('name': metric.template.name,
                                 'value': metricData.value,
                                 'units': metric.template.units,
                                 'time': formatter.format(new Date(metricData.timestamp)))
                }
            }
        }
       xml
    }
}
