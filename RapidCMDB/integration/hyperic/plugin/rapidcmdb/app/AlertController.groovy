import org.hyperic.hq.appdef.server.session.PlatformManagerEJBImpl as PlatMan
import org.hyperic.hq.appdef.server.session.ServerManagerEJBImpl as ServerMan
import org.hyperic.hq.appdef.server.session.ServiceManagerEJBImpl as ServiceMan
import org.hyperic.hq.events.server.session.AlertManagerEJBImpl as AlertMan

import java.text.SimpleDateFormat
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.appdef.shared.*
import org.hyperic.hq.authz.server.session.AuthzSubjectManagerEJBImpl
import org.hyperic.hq.events.server.session.AlertDefinition
import org.hyperic.hq.events.server.session.AlertSortField
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqu.rendit.helpers.AlertHelper
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper
import org.hyperic.hq.hqu.rendit.util.HQUtil
import org.hyperic.util.pager.PageControl
import org.hyperic.util.pager.PageList

class AlertController
	extends BaseController
{
    def AlertController() {
        setXMLMethods(['list', 'get'])
    }

    def list(xml, params) {
        def pageInfo = new PageInfo(AlertSortField.DATE, true)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        //def plats        = rhelp.findAllPlatforms()
        def pMan = PlatMan.one
        def ahelp = new AlertHelper(overlord)
        def aMan = AlertMan.one
        def plats = pMan.getAllPlatforms(overlord.authzSubjectValue, PageControl.PAGE_ALL)

        def begin = params.getOne('begin')

        xml.HypericEvents('timestamp':new Date().getTime()) {
            def alerts = aMan.findAllAlerts()

            if (alerts != null) {
                def i = 0
                while (i < (alerts.size() / 2)) {  //because findAllAlerts, unexpectedly, returns the alert set twice
                    def myAlert2 = alerts.getAt(i)
                    def myAlert = aMan.findAlertById(myAlert2.id)
                    if (begin != null) {
                        if (myAlert.alertValue.ctime.toString() < begin.toString()) {
                            i++
                            continue;
                        }
                    }
                    AlertDefinition alertDef = myAlert.getAlertDefinition()
                    AppdefEntityID aeid = new AppdefEntityID(alertDef.getAppdefType(), alertDef.getAppdefId())
                    AppdefEntityValue aev = new AppdefEntityValue(aeid, AuthzSubjectManagerEJBImpl.getOne().getOverlordPojo())

                    xml.HypericEvent('id': myAlert.id,
                                     'name': myAlert.alertDefinition.alertDefinitionValue.name,
                                     'owner_name': aev.getName(),
                                     'timestamp': myAlert.alertValue.ctime,
                                     'fixed': myAlert.fixed,
                                     'long_reason': aMan.getLongReason(myAlert)
                    )
                    i++
                }
            }
        }
        xml
    }
    

    def get(xml, params) {
        //def pageInfo = new PageInfo(ResourceSortField.NAME, true)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        def ahelp = new AlertHelper(overlord)
        def aMan = AlertMan.one

        def pMan = PlatMan.one
        def svrMan = ServerMan.one
        def svcMan = ServiceMan.one

        def owner = params.getOne('owner_name')
        def type = params.getOne('type')
        def begin = params.getOne('begin')

        def source
        def id
        def alerts
        switch (type) {
            case "platform":
                try {
                    if (owner != null) {
                        source = pMan.getPlatformByName(owner)
                    }
                }
                catch (PlatformNotFoundException e) {
                    source = null
                }
                if (source != null) {
                    alerts = aMan.findAlerts(overlord.authzSubjectValue, AppdefEntityID.newPlatformID(source.id), PageControl.PAGE_ALL)
                }
                break;
            case "server":
                source = new PageList()
                def sers
                try {
                    if (owner != null) {
                        sers = svrMan.findServersByName(overlord.authzSubjectValue, owner)
                    }
                }
                catch (ServerNotFoundException e) {
                    sers = null
                }
                for(svr2 in sers) {
                    //def svr = rhelp.find('server': svr2.id)
                    source.add(svr2)
                }
                alerts = new PageList()
                for(x in source) {
                    alerts.addAll(aMan.findAlerts(overlord.authzSubjectValue, AppdefEntityID.newServerID(x.id), PageControl.PAGE_ALL))
                }
                break;
            case "service":
                source = new PageList()
                def servs
                try {
                    if (owner != null) {
                        servs = svcMan.findServicesByName(overlord.authzSubjectValue, owner)
                    }
                }
                catch (ServiceNotFoundException e) {
                    servs = null
                }
                for(svc2 in servs) {
                    //def svc = rhelp.find('service': svc2.id)
                    def svc = svcMan.findServiceById(svc2.id)
                    source.add(svc)
                }
                alerts = new PageList()
                for(x in source) {
                    alerts.addAll(aMan.findAlerts(overlord.authzSubjectValue, AppdefEntityID.newServiceID(x.id), PageControl.PAGE_ALL))
                }
                break;
        }

        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':new Date()) {
            //def alerts = aMan.findAllAlerts()
            xml.'Alerts'() {
                if (alerts != null) {
                    for (myAlert2 in alerts) {
                        def myAlert = aMan.findAlertById(myAlert2.id)
                        if (begin != null) {
                            if (myAlert.alertValue.ctime.toString() < begin.toString()) {
                                continue;
                            }
                        }
                        AlertDefinition alertDef = myAlert.getAlertDefinition()
                        AppdefEntityID aeid = new AppdefEntityID(alertDef.getAppdefType(), alertDef.getAppdefId())
                        AppdefEntityValue aev = new AppdefEntityValue(aeid, AuthzSubjectManagerEJBImpl.getOne().getOverlordPojo())

                        xml.'alert'('id': myAlert.id,
                                    'alert_name': myAlert.alertDefinition.alertDefinitionValue.name,
                                    'owner_name': aev.getName(),
                                    'timestamp': myAlert.alertValue.ctime,
                                    'fixed': myAlert.fixed,
                                    'long_reason': aMan.getLongReason(myAlert)
                        )
                    }
                }
            }
        }
       xml
    }
}
