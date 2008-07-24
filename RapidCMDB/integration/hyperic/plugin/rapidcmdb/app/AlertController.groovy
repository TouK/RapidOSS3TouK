import org.hyperic.hq.appdef.server.session.PlatformManagerEJBImpl as PlatMan
import org.hyperic.hq.appdef.server.session.ServerManagerEJBImpl as ServerMan
import org.hyperic.hq.appdef.server.session.ServiceManagerEJBImpl as ServiceMan
import org.hyperic.hq.events.server.session.AlertManagerEJBImpl as AlertMan

import java.text.SimpleDateFormat
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.appdef.shared.AppdefEntityID
import org.hyperic.hq.appdef.shared.AppdefEntityValue
import org.hyperic.hq.authz.server.session.AuthzSubjectManagerEJBImpl
import org.hyperic.hq.authz.server.session.ResourceSortField
import org.hyperic.hq.events.server.session.AlertDefinition
import org.hyperic.hq.events.server.session.AlertSortField
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqu.rendit.helpers.AlertHelper
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper
import org.hyperic.hq.hqu.rendit.util.HQUtil
import org.hyperic.hq.events.AlertSeverity
import org.hyperic.util.pager.PageControl
import org.hyperic.util.pager.PageList

class AlertController
	extends BaseController
{
    def AlertController() {
        setXMLMethods(['list', 'get'])
    }

    def list(xml, params) {
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        def pageInfo = new PageInfo(AlertSortField.DATE, true)
        def overlord     = HQUtil.overlord
        def rhelp        = new ResourceHelper(overlord)
        def plats        = rhelp.findAllPlatforms()
        def pMan = PlatMan.one
        def ahelp = new AlertHelper(overlord)
        def aMan = AlertMan.one

        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':formatter.format(new Date())) {
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
/*                                'aeid': aeid,
                                'aeid-type': aeid.authzTypeId,*/
                                'creationTime': formatter.format(new Date(myAlert.alertValue.ctime)),
                                'fixed': myAlert.fixed,
//                                'reason': aMan.getShortReason(myAlert),
                                'long_reason': aMan.getLongReason(myAlert)
                    )
                }
            }
        }
       xml
    }
    

    def get(xml, params) {
        def formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
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
        def source
        def id
        def alerts
        switch (type) {
            case "platform":
                source = pMan.getPlatformByName(owner)
                alerts = aMan.findAlerts(overlord.authzSubjectValue, AppdefEntityID.newPlatformID(source.id), PageControl.PAGE_ALL)
                break;
            case "server":
                source = new PageList()

                def sers = svrMan.findServersByName(overlord.authzSubjectValue, owner)
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
                def servs = svcMan.findServicesByName(overlord.authzSubjectValue, owner)
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

        xml.'RapidCMDB'('source':'Hyperic HQ', 'date':formatter.format(new Date())) {
            //def alerts = aMan.findAllAlerts()
            xml.'alerts'('numberOfAlerts': alerts.getTotalSize()) {
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
                                'long_reason': aMan.getLongReason(myAlert)
                    )
                }
            }
        }
       xml
    }
}
