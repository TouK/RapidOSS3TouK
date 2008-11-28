import datasource.*
import com.ifountain.rcmdb.util.RapidDateUtilities

def interfacesDs = SingleTableDatabaseDatasource.get(name:"openNmsInterfacesDs")
def nodesDs = SingleTableDatabaseDatasource.get(name:"openNmsNodesDs")
def ifServicesDs = SingleTableDatabaseDatasource.get(name:"openNmsInterfaceServicesDs")
def servicesDs = SingleTableDatabaseDatasource.get(name:"openNmsServicesDs")
def snmpInterfaceDs = SingleTableDatabaseDatasource.get(name:"openNmsSnmpInterfacesDs")
def openNmsServer = RsManagementSystem.get(name:"openNmsServer")
openNmsGraphDs=HttpDatasource.get(name:"openNmsHttpDs");

openNmsGraphDs.doGetRequest("j_acegi_security_check", ["j_username":"admin","j_password":"admin"]);

if(!openNmsServer)
{
    openNmsServer=RsManagementSystem.add(name:"openNmsServer");
}
// openNmsServer.lastPolledAt = "1218148765718"
def stringDateValue = Date.toDate(openNmsServer.lastPolledAt).toString("yyyy-MM-dd HH:mm:ss.SSS")
logger.info("stringDateValue: " + stringDateValue)

OpenNmsNode.list()*.remove()
OpenNmsIpInterface.list()*.remove()
OpenNmsService.list()*.remove()
OpenNmsGraph.list()*.remove()


def serviceNames = [:]
servicesDs.getRecords().each {
	serviceNames[it.SERVICEID] = it.SERVICENAME
}

//def nodes = nodesDs.getRecords("LASTCAPSDPOLL > '${stringDateValue}'")
def nodes = nodesDs.getRecords();

nodes.each{ n ->

	def nProps = [:]
	nProps.name = n.NODEID
	nProps.dpName = n.DPNAME
	nProps.type = n.NODETYPE
	nProps.createdAt = Date.toDate(n.NODECREATETIME)
	nProps.lastPolledAt = Date.toDate(n.LASTCAPSDPOLL)
	nProps.sysOid = n.NODESYSOID
	nProps.sysName = n.NODESYSNAME
	nProps.sysDescription = n.NODESYSDESCRIPTION
	nProps.sysLocation = n.NODESYSLOCATION
	nProps.netbiosName = n.NODENETBIOSNAME
	nProps.domainName = n.NODEDOMAINNAME
	nProps.operatingSystem = n.OPERATINGSYSTEM
	nProps.foreignSource = n.FOREIGNSOURCE
	nProps.foreignId = n.FOREIGNID

	nProps.nodeName = n.NODELABEL
	nProps.className = "OpenNmsNode"
	nProps.displayName=nProps.nodeName
    nProps.rsDatasource=nodesDs.connection.name

	logger.debug("Adding OpenNmsNode with props ${nProps}");

	def nodeObj = OpenNmsNode.add(nProps)
	if (nodeObj.hasErrors()) {
		logger.error("nProps: " + nProps)
		logger.error("could not add node: " + nodeObj.errors)
	} else {

		if(n.NODESYSOID)
		{
            addGraphsToObject(n.NODEID,nodeObj,"node",[:]);
		}

		def ipInterfaces = interfacesDs.getRecords("NODEID=${n.NODEID}")
		ipInterfaces.each{ i ->
			//logger.info("ip interface table " + i)
			def iProps = [:]
			iProps.name = i.NODEID + "-" + i.ID
			iProps.ipAddress = i.IPADDR
			if (i.ISMANAGED == "M") {
				iProps.isManaged = true
			} else {
				iProps.isManaged = false
			}
			iProps.lastPolledAt = Date.toDate(i.IPLASTCAPSDPOLL)

			iProps.className = "OpenNmsIpInterface"
			iProps.displayName=iProps.ipAddress
            iProps.rsDatasource=interfacesDs.connection.name

			if (i.SNMPINTERFACEID != "") {
				def sI = snmpInterfaceDs.getRecord(i.SNMPINTERFACEID.toInteger())
				logger.info("sI " + sI)
				iProps.snmpInterfaceId = i.SNMPINTERFACEID
				iProps.netmask = sI.SNMPIPADENTNETMASK
				iProps.macAddress = sI.SNMPPHYSADDR
				iProps.ifIndex = sI.SNMPIFINDEX
				iProps.ifDescription = sI.SNMPIFDESCR
				iProps.ifType = getIfTypeString(sI.SNMPIFTYPE)
				iProps.ifName = sI.SNMPIFNAME
				iProps.ifSpeed = sI.SNMPIFSPEED
				iProps.ifAlias = sI.SNMPIFALIAS
				iProps.adminStatus = getIfStatusString(sI.SNMPIFADMINSTATUS)
				iProps.operStatus = getIfStatusString(sI.SNMPIFOPERSTATUS)
			}
			logger.debug("Adding OpenNmsIpInterface with props ${iProps}");
			def ipInterfaceObj = OpenNmsIpInterface.add(iProps)
			if (ipInterfaceObj.hasErrors()) {
				logger.error("nProps: " + nProps)
				logger.error("could not add ipInterface: " + ipInterfaceObj.errors)
			} else {
                nodeObj.addRelation("ipInterfaces":[ipInterfaceObj])

                addGraphsToObject(i.NODEID,ipInterfaceObj,"ipinterface",["ipinterfaceid":i.ID]);

				def ifServices = ifServicesDs.getRecords("IPINTERFACEID=${i.ID}")
				ifServices.each{ s ->
					def sProps = [:]
					sProps.name =s.NODEID + "-" + s.IPINTERFACEID + "-" + s.ID
					sProps.lastGoodAt = s.LASTGOOD
					sProps.lastFailedAt = s.LASTFAIL
					sProps.qualifier = s.QUALIFIER
					sProps.status = s.STATUS
					sProps.source = s.SOURCE
					sProps.notify = s.NOTIFY

					sProps.serviceName = serviceNames[s.SERVICEID]
					sProps.className = "OpenNmsService"
					sProps.displayName=sProps.serviceName
                    sProps.rsDatasource=ifServicesDs.connection.name

                    logger.debug("Adding OpenNmsService with props ${sProps}");
					def ifServiceObj = OpenNmsService.add(sProps)
					if (ifServiceObj.hasErrors()) {
						logger.error("sProps: " + sProps)
						logger.error("could not add ipInterface: " + ifServiceObj.errors)
					} else {
						ipInterfaceObj.addRelation("services":[ifServiceObj])
					}
				}
			}
		}
	}
}

def getIfTypeString(def ifTypeNum) {
	List ifTypes = [
		"&nbsp;",                     //0 (not supported)
		"other",                    //1
		"regular1822",              //2
		"hdh1822",                  //3
		"ddn-x25",                  //4
		"rfc877-x25",               //5
		"ethernetCsmacd",           //6
		"iso88023Csmacd",           //7
		"iso88024TokenBus",         //8
		"iso88025TokenRing",        //9
		"iso88026Man",              //10
		"starLan",                  //11
		"proteon-10Mbit",           //12
		"proteon-80Mbit",           //13
		"hyperchannel",             //14
		"fddi",                     //15
		"lapb",                     //16
		"sdlc",                     //17
		"ds1",                      //18
		"e1",                       //19
		"basicISDN",                //20
		"primaryISDN",              //21
		"propPointToPointSerial",   //22
		"ppp",                      //23
		"softwareLoopback",         //24
		"eon",                      //25
		"ethernet-3Mbit",           //26
		"nsip",                     //27
		"slip",                     //28
		"ultra",                    //29
		"ds3",                      //30
		"sip",                      //31
		"frame-relay",              //32
		"rs232",                    //33
		"para",                     //34
		"arcnet",                   //35
		"arcnetPlus",               //36
		"atm",                      //37
		"miox25",                   //38
		"sonet",                    //39
		"x25ple",                   //40
		"is0880211c",               //41
		"localTalk",                //42
		"smdsDxi",                  //43
		"frameRelayService",        //44
		"v35",                      //45
		"hssi",                     //46
		"hippi",                    //47
		"modem",                    //48
		"aa15",                     //49
		"sonetPath",                //50
		"sonetVT",                  //51
		"smdsIcip",                 //52
		"propVirtual",              //53
		"propMultiplexor",          //54
		"ieee80212",                //55
		"fibreChannel",             //56
		"hippiInterface",           //57
		"frameRelayInterconnect",   //58
		"aflane8023",               //59
		"aflane8025",               //60
		"cctEmul",                  //61
		"fastEther",                //62
		"isdn",                     //63
		"v11",                      //64
		"v36",                      //65
		"g703at64k",                //66
		"g703at2mb",                //67
		"qllc",                     //68
		"fastEtherFX",              //69
		"channel",                  //70
		"ieee80211",                //71
		"ibm370parChan",            //72
		"escon",                    //73
		"dlsw",                     //74
		"isdns",                    //75
		"isdnu",                    //76
		"lapd",                     //77
		"ipSwitch",                 //78
		"rsrb",                     //79
		"atmLogical",               //80
		"ds0",                      //81
		"ds0Bundle",                //82
		"bsc",                      //83
		"async",                    //84
		"cnr",                      //85
		"iso88025Dtr",              //86
		"eplrs",                    //87
		"arap",                     //88
		"propCnls",                 //89
		"hostPad",                  //90
		"termPad",                  //91
		"frameRelayMPI",            //92
		"x213",                     //93
		"adsl",                     //94
		"radsl",                    //95
		"sdsl",                     //96
		"vdsl",                     //97
		"iso88025CRFPInt",          //98
		"myrinet",                  //99
		"voiceEM",                  //100
		"voiceFXO",                 //101
		"voiceFXS",                 //102
		"voiceEncap",               //103
		"voiceOverIp",              //104
		"atmDxi",                   //105
		"atmFuni",                  //106
		"atmIma",                   //107
		"pppMultilinkBundle",       //108
		"ipOverCdlc",               //109
		"ipOverClaw",               //110
		"stackToStack",             //111
		"virtualIpAddress",         //112
		"mpc",                      //113
		"ipOverAtm",                //114
		"iso88025Fiber",            //115
		"tdlc",                     //116
		"gigabitEthernet",          //117
		"hdlc",                     //118
		"lapf",                     //119
		"v37",                      //120
		"x25mlp",                   //121
		"x25huntGroup",             //122
		"trasnpHdlc",               //123
		"interleave",               //124
		"fast",                     //125
		"ip",                       //126
		"docsCableMaclayer",        //127
		"docsCableDownstream",      //128
		"docsCableUpstream",        //129
		"a12MppSwitch",             //130
		"tunnel",                   //131
		"coffee",                   //132
		"ces",                      //133
		"atmSubInterface",          //134
		"l2vlan",                   //135
		"l3ipvlan",                 //136
		"l3ipxvlan",                //137
		"digitalPowerline",         //138
		"mediaMailOverIp",          //139
		"dtm",                      //140
		"dcn",                      //141
		"ipForward",                //142
		"msdsl",                    //143
		"ieee1394",                 //144
		"if-gsn",                   //145
		"dvbRccMacLayer",           //146
		"dvbRccDownstream",         //147
		"dvbRccUpstream",           //148
		"atmVirtual",               //149
		"mplsTunnel",               //150
		"srp",                      //151
		"voiceOverAtm",             //152
		"voiceOverFrameRelay",      //153
		"idsl",                     //154
		"compositeLink",            //155
		"ss7SigLink",               //156
		"propWirelessP2P",          //157
		"frForward",                //158
		"rfc1483",                  //159
		"usb",                      //160
		"ieee8023adLag",            //161
		"bgppolicyaccounting",      //162
		"frf16MfrBundle",           //163
		"h323Gatekeeper",           //164
		"h323Proxy",                //165
		"mpls",                     //166
		"mfSigLink",                //167
		"hdsl2",                    //168
		"shdsl",                    //169
		"ds1FDL",                   //170
		"pos",                      //171
		"dvbAsiIn",                 //172
		"dvbAsiOut",                //173
		"plc",                      //174
		"nfas",                     //175
		"tr008",                    //176
		"gr303RDT",                 //177
		"gr303IDT",                 //178
		"isup",                     //179
		"propDocsWirelessMaclayer",      //180
		"propDocsWirelessDownstream",    //181
		"propDocsWirelessUpstream",      //182
		"hiperlan2",                //183
		"propBWAp2Mp",              //184
		"sonetOverheadChannel",     //185
		"digitalWrapperOverheadChannel", //186
		"aal2",                     //187
		"radioMAC",                 //188
		"atmRadio",                 //189
		"imt",                      //190
		"mvl",                      //191
		"reachDSL",                 //192
		"frDlciEndPt",              //193
		"atmVciEndPt",              //194
		"opticalChannel",           //195
		"opticalTransport"          //196
	]
	if (ifTypeNum == "") {return}
	int num = ifTypeNum.toInteger()
	if (num < ifTypes.size()) {
		return ifTypes[num]
	} else {
		return "Unknown (" + num + ")"
	}

}

def getIfStatusString(def ifStatusNum) {
	List operAdminStatus = [
		"&nbsp;",          //0 (not supported)
		"Up",              //1
		"Down",            //2
		"Testing",         //3
		"Unknown",         //4
		"Dormant",         //5
		"NotPresent",      //6
		"LowerLayerDown"   //7
	]
	if (ifStatusNum == "") {return}

	int num = ifStatusNum.toInteger()

	if (num < operAdminStatus.size()) {
		return operAdminStatus[num]
	} else {
		return "Unknown (" + ifStatusNum + ")"
	}
}

if (nodes.size() > 0) {
	openNmsServer.lastPolledAt = Date.now()
}


def addGraphsToObject(nodeId,openNmsObject,objectType,additionalParams)
{
     def requestParams=[:]
     requestParams["reports"]="all"
     requestParams["nodeid"]=nodeId
     requestParams["type"]=objectType

     if(objectType=="ipinterface")
     {
         requestParams["ipinterfaceid"]=additionalParams["ipinterfaceid"];
     }

    logger.debug("Requesting graphs of Object ${openNmsObject.name}");
    def graphsXml=openNmsGraphDs.doGetRequest("rapidcmdb/graphresultsasxml.jsp",requestParams);

    def parser = new XmlParser()
    def nodeGraphs = parser.parseText(graphsXml)

    for(graph in nodeGraphs.Graph)
    {
        logger.debug("Adding Graph with url ${graph."@url"}");
        def graphObj=OpenNmsGraph.add(url:graph."@url");
        if(graphObj.hasErrors())
        {
            logger.warn("Could not add graps. Reason: ${graphObj.errors}")
        }

        logger.debug("Adding graph to Object ${openNmsObject.name} with url ${graph."@url"} as a relation");
        openNmsObject.addRelation("graphs":[graphObj]);
        if(openNmsObject.hasErrors())
        {
            logger.warn("Could not add Graph Relation. Reason: ${openNmsObject.errors}")
        }

    }
    logger.debug("The Node ${openNmsObject.name} has graphs ${openNmsObject.graphs}");

}