import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import groovy.text.SimpleTemplateEngine

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 31, 2008
 * Time: 1:58:51 PM
 * To change this template use File | Settings | File Templates.
 */
class NetcoolDemoValues {
    def engine = new SimpleTemplateEngine();
    public int numberOfEvents = 0;
    public eventProperties = ["servername", "connectorname", "acknowledge", "severity", "suppressescl", "ncclass","tally", "owneruid", "ownergid",
            "tasklist","alertgroup","node","manager","agent"]

    public journalProperties = ["text"]
    def servernameOptions = ["NCOMS", "NCOMS2"];
    def connectornameOptions = ["con1", "con2"];
    def acknowledgeOptions = ["Yes", "No"];
    def tallyOptions = [1]
    def textOptions = [
        "Alert is unacknowledged by rsadmin",
        """Alert is acknowledged by rsadmin""",
        "Alert is prioritized from 4 to 5 by Unknown",
        "Alert is prioritized from 2 to 3 by Unknown",
        "Alert is prioritized from 1 to 2 by Unknown",
        "Alert is prioritized from 2 to 5 by Unknown",
        "Put in task list by rsadmin"
    ]
    def severityOptions = [
            "Clear",
            "Indeterminate",
            "Warning",
            "Minor",
            "Major",
            "Critical"];
    def suppressesclOptions = [
            "Normal",
            "Escalated",
            "Escalated-Level 2",
            "Escalated-Level 3",
            "Suppressed",
            "Hidden",
            "Maintenance"];
    def ncclassOptions = [
            "Siemens EWSD",
            "Vitria",
            "Zaffire",
            "Visual Networks",
            "Visualware",
            "Scientific Atlanta",
            "Xtera Communications",
            "NTP ISM",
            "HP IT/Operations Center",
            "Copper Mountain",
            "Nokia SBT",
            "Viewgate Networks",
            "Visionael Corporation",
            "Wavesmith Networks",
            "IDS Generic",
            "IDS-POST",
            "White Rock Networks",
            "IDS-RDEP",
            "ISS SiteProtector",
            "ISS WorkGroup Manager",
            "Network Flight Recorder",
            "ADC Metrica NPR",
            "Juniper-JUNOS  syslog",
            "Juniper-JUNOS SNMP",
            "Juniper-JUNOSe SNMP",
            "Juniper-JUNOSe syslog",
            "Tripwire for Servers (E-Mail)",
            "Tripwire for Servers (NT Event Log)",
            "Tripwire for Servers (Syslog)",
            "Entercept Security Technologies HIDS (Syslog)",
            "Nortel INM (Sonet/SDH)",
            "SonicWALL Firewall (Syslog)",
            "SonicWALL (SNMP Trap)",
            "Opnet Technologies",
            "IDS-POST",
            "Syslog Probe",
            "Alcatel OS-OS",
            "Dantel Pointmaster",
            "Impact",
            "Primal Solutions",
            "Procket Networks",
            "Netscreen Technologies (IDP)",
            "Quarry Technologies",
            "Scientific Atlanta"
    ];
    def owneruidOptions = ["Root", "Nobody"];
    def ownergidOptions = ["Public"];
    def tasklistOptions = [0, 1];
    def summaryOptions = [
            Link: [engine.createTemplate("Link Down on port \${summaryParam}")],
            Systems: [engine.createTemplate("Machine \${summaryParam} has gone online"), engine.createTemplate("Port failure : port \${summaryParam} reset"), engine.createTemplate("Diskspace alert")],
            Stats: [engine.createTemplate("Machine \${summaryParam} has gone online"), engine.createTemplate("Port failure : port \${summaryParam} reset"), engine.createTemplate("Diskspace alert")],
            JavaAdmin: [engine.createTemplate("A JavaAdmin process \${summaryParam} running on ossmuse has disconnected")],
            GATEWAY:[engine.createTemplate("A GATEWAY process \${summaryParam} running on OSSMUSE has disconnected")]
    ];//summarynin icine dinamik bisey koyalim bir rakam olabilir

    def agentOptions = [
        "MachineMon",
        "LinkMon",
        "MachineLogs",
        "MachineStats",
        "MachineLogs"
    ];
    def nodeOptions = [
            "muppet",
            "link",
            "myNode",
            "moose",
            "ossmuse",
            "dewey",
            "orac",
            "vixen",
            "wombat",
            "angel"];//nodeun sonuna 1-100 arasi random ekle
    def alertgroupOptions = [
            "Systems",
            "Link",
            "JavaAdmin",
            "GATEWAY",
            "Stats"]; //SYStem
    def managerOptions = [
            "Simnet Probe",
            "SecurityWatch",
            "ConnectionWatch"];            //simnetprob trap prop
    def eventNameOptions = ["Yes", "No"];//event+node

    public Object getEventProperty(String property) {
        try {
            return getProperty(property)
        }
        catch (Throwable t1) {
            try {
                def allOptionsProps = getProperty(property + "Options");
                return allOptionsProps[nextNumber(allOptionsProps.size())]
            }
            catch (Throwable t2) {
                return null;
            }
        }

    }


    def getNode() {
        return nodeOptions[nextNumber(nodeOptions.size())] + nextNumber(1000000);
    }

    public Map getJournalProperties(connectorName, serverName, serial) {
        def ch = (System.currentTimeMillis()- nextNumber(200000))/1000;
        return [keyfield:"$serial:0;$ch", connectorName:connectorName, servername:servername, serverserial:serial, chrono:ch, text:textOptions[nextNumber(textOptions.size())]];

    }

    public Map getEventProperties() {
        def now = System.currentTimeMillis() / 1000;
        def serial = numberOfEvents++;
        def props = [serverserial:serial, serial:serial, statechange: now, firstoccurrence: now, internallast: now, lastoccurrence: now];
        eventProperties.each {
            def propVal = getEventProperty(it)
            if (propVal != null) {
                props[it] = propVal
            }
        }

        def summary = summaryOptions[props["alertgroup"]]
        if(summary)
        {
            summary = summary[nextNumber(summary.size())].make([summaryParam:nextNumber(10)]).toString();
            props["summary"] = summary;
            if(summary.indexOf("Down"))
            {
                props["severity"] = "Critical";
            }
            else if(summary.indexOf("Up"))
            {
                props["severity"] = "Indeterminate";
            }
        }

        def identifier = props["node"]+props["agent"]+props["severity"]+props["alertgroup"]
        props["identifier"]=identifier;
        return props;
    }

    def nextNumber(int numberOfitems) {
        return nextNumber(0, numberOfitems);
    }

    def nextNumber(int start, int numberOfitems) {
        return start + (int) (Math.random() * numberOfitems);
    }
}