import org.hyperic.hq.appdef.server.session.PlatformManagerEJBImpl as PlatMan
import org.hyperic.hq.events.server.session.AlertManagerEJBImpl as AlertMan
import org.hyperic.hq.appdef.server.session.ServerManagerEJBImpl as ServerMan

import java.text.SimpleDateFormat
import org.hyperic.hibernate.PageInfo
import org.hyperic.hq.appdef.shared.AppdefEntityID
import org.hyperic.hq.appdef.shared.AppdefEntityValue
import org.hyperic.hq.authz.server.session.AuthzSubjectManagerEJBImpl
import org.hyperic.hq.authz.server.session.ResourceSortField
import org.hyperic.hq.events.server.session.AlertDefinition
import org.hyperic.hq.hqu.rendit.BaseController
import org.hyperic.hq.hqu.rendit.helpers.AlertHelper
import org.hyperic.hq.hqu.rendit.helpers.ResourceHelper
import org.hyperic.hq.hqu.rendit.util.HQUtil
import org.hyperic.hq.appdef.shared.PlatformNotFoundException
import org.hyperic.hq.appdef.shared.ServerNotFoundException
import org.hyperic.hq.authz.shared.AuthzConstants

class ExporterController
extends BaseController
{
    def ExporterController() {
        setXMLMethods(['list', 'platform', 'server', 'service', 'alert'])
    }


    def list(xml, params) {
        def overlord = HQUtil.overlord
        def rhelp = new ResourceHelper(overlord)
        def pMan = PlatMan.one
        def serverMan = ServerMan.one
        def plats = rhelp.findAllPlatforms()
        def servers = rhelp.findAllServers()
        def services = rhelp.findAllServices()

        def lasttimestamp = 0;
        def timestampParam = params.getOne('lasttimestamp')
        if (timestampParam != null) {
            lasttimestamp = Long.parseLong(timestampParam);
        }

        xml.HypericObjects('timestamp': new Date().getTime()) {
            xml.Platforms() {
                plats.each {plat ->
                    def platform = plat.toPlatform();
                    def modifiedTime = platform.modifiedTime
                    if (modifiedTime >= lasttimestamp) {
                        xml.Platform(id: platform.id, name: platform.name, location: platform.location, description: platform.description)
                    }
                }
            }
            xml.Servers() {
                servers.each {s ->
                    def server = s.toServer();
                    def modifiedTime = server.modifiedTime;
                    if (modifiedTime >= lasttimestamp) {
                        def platName = "";
                        def platformId = "";
                        try {
                            plat = pMan.getPlatformByServer(overlord.authzSubjectValue, s.toServer().id)
                            if (plat != null) {
                                platName = plat.getName()
                                platformId = plat.id;
                            }
                        }
                        catch (PlatformNotFoundException e) {
                        }
                        xml.Server(id: server.id, name: server.name, platform: platName, platformId: platformId, location: server.location, description: server.description)
                    }

                }
            }
            xml.Services() {
                services.each {sv ->
                    def service = sv.toService();
                    def modifiedTime = service.modifiedTime;
                    if (modifiedTime >= lasttimestamp) {
                        def serverName = ""
                        def platName = ""
                        def serverId = ""
                        try {
                            def server = serverMan.getServerByService(overlord.authzSubjectValue, service.id)
                            serverName = server.getName()
                            serverId = server.id;
                            platName = pMan.getPlatformByServer(overlord.authzSubjectValue, server.id).getName()
                        }
                        catch (ServerNotFoundException se) {
                        }
                        catch (PlatformNotFoundException pe) {
                        }
                        xml.Service(id: service.id, name: service.name, server: serverName, serverId: serverId, platform: platName, location: service.location, description: service.description)
                    }
                }
            }
        }
        xml
    }





    def get(xml, params) {
        def overlord = HQUtil.overlord
        def rhelp = new ResourceHelper(overlord)
        def errors = [];
        def type = params.getOne('type')
        def id = params.getOne('id');
        if (type != 'platform' && type != 'server' && type != 'service') {
            errors.add("Invalid type ${type}")
        }
        else {
            def args = [:]
            args.put(type, id.toInteger())
            def resource = rhelp.find(args);
            if (resource != null) {
                def props = ['type': type, 'id': id]
                if (type == 'platform') {
                    def platform = resource.toPlatform();
                    def platformType = platform.platformType;
                    props.put('name', platform.name)
                    props.put('creationTime', platform.creationTime)
                    props.put('modifiedTime', platform.modifiedTime)
                    props.put('location', platform.location)
                    props.put('description', platform.description)
                    props.put('fqdn', platform.fqdn)
                    props.put('cpuCount', platform.cpuCount)
                    props.put('certdn', platform.certdn)
                    props.put('commentText', platform.commentText)
                    props.put('os', platformType.os)
                    props.put('arch', platformType.arch)
                    props.put('plugin', platformType.plugin)
                    props.put('commentText', platformType.osVersion)
                }
                else if (type == 'server') {
                    def server = resource.toServer();
                    props.put('name', server.name)
                    props.put('creationTime', server.creationTime)
                    props.put('modifiedTime', server.modifiedTime)
                    props.put('location', server.location)
                    props.put('description', server.description)
                    props.put('runtimeAutodiscovery', server.runtimeAutodiscovery)
                    props.put('wasAutodiscovered', server.wasAutodiscovered)
                    props.put('autodiscoveryZombie', server.autodiscoveryZombie)
                    props.put('installPath', server.installPath)
                    props.put('servicesAutomanaged', server.servicesAutomanaged)
                    props.put('autoinventoryIdentifier', server.autoinventoryIdentifier)
                    props.put('plugin', server.serverType.plugin)
                }
                else if (type == 'service') {
                    def service = resource.toService();
                    def serviceType = service.serviceType;
                    props.put('name', service.name)
                    props.put('creationTime', service.creationTime)
                    props.put('modifiedTime', service.modifiedTime)
                    props.put('location', service.location)
                    props.put('description', service.description)
                    props.put('autodiscoveryZombie', service.autodiscoveryZombie)
                    props.put('serviceRt', service.serviceRt)
                    props.put('endUserRt', service.endUserRt)
                    props.put('plugin', serviceType.plugin)
                    props.put('isInternal', serviceType.isInternal)

                }
                xml.HypericObjects() {
                    xml.HypericObject(props) {
                        xml.Metrics() {
                            for (metric in resource.enabledMetrics) {
                                def metricData = metric.lastDataPoint
                                if (metricData == null) {
                                    xml.Metric(name: metric.template.name)
                                }
                                else {
                                    xml.Metric(name: metric.template.name, value: metricData.value, units: metric.template.units, time: metricData.timestamp)
                                }
                            }
                        }
                    }
                }
            }
            else {
                errors.add("Could not find resource with id ${id} and type ${type}");
            }
        }
        if (errors.size() > 0) {
            xml.Errors() {
                errors.each {
                    xml.Error(error: it)
                }
            }
        }
        xml
    }

}
