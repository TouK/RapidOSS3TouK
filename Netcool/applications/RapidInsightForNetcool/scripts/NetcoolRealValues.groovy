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
class NetcoolRealValues {
    def engine = new SimpleTemplateEngine();
    public int numberOfEvents = 0;
    public eventProperties = ["ServerName", "Acknowledged", "Severity", "SuppressEscl", "Class","Tally", "OwnerUID", "OwnerGID",
            "TaskList","AlertGroup","Node","Manager","Agent"]

    public journalProperties = ["text"]
    def servernameOptions = ["NCOMS", "NCOMS2"];
    def acknowledgedOptions = [0, 1];
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
    def severityOptions = [1,2,3,4,5];
    def suppressesclOptions = [1,2,3,4,5,6,7];
    def ncclassOptions = [1,2,3,4,5,6,7,8,9,10,11,12,13];
    def owneruidOptions = [1,2];
    def ownergidOptions = [1];
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
                def allOptionsProps = getProperty(property.toLowerCase() + "Options");
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

    public Map getJournalProperties(rsDatasource, serverName, serial) {
        def ch = (System.currentTimeMillis()- nextNumber(200000))/1000;
        return [keyfield:"$serial:0:$ch", rsDatasource:rsDatasource, servername:serverName, serverserial:serial, chrono:ch, text:textOptions[nextNumber(textOptions.size())]];

    }

    public Map getEventProperties() {
        def now = System.currentTimeMillis() / 1000;
        def serial = numberOfEvents++;
        def props = [:];
        eventProperties.each {
            def propVal = getEventProperty(it)
            if (propVal != null) {
                props[it] = propVal
            }
        }

        def summary = summaryOptions[props["AlertGroup"]]
        if(summary)
        {
            summary = summary[nextNumber(summary.size())].make([summaryParam:nextNumber(10)]).toString();
            props["Summary"] = summary;
            if(summary.indexOf("Down"))
            {
                props["Severity"] = 5;
            }
            else if(summary.indexOf("Up"))
            {
                props["Severity"] = 2;
            }
        }

        def identifier = props["Node"]+props["Agent"]+props["Severity"]+props["AlertGroup"]+(numberOfEvents++)
        props["Identifier"]=identifier;
        return props;
    }

    def nextNumber(int numberOfitems) {
        return nextNumber(0, numberOfitems);
    }

    def nextNumber(int start, int numberOfitems) {
        return start + (int) (Math.random() * numberOfitems);
    }
}