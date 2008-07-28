package datasource

import connection.NetcoolConnection
import datasource.SingleTableDatabaseAdapter
import org.apache.log4j.Logger
import java.text.MessageFormat
import groovy.text.SimpleTemplateEngine

class NetcoolDatasource extends BaseDatasource{
    static searchable = {
        except = ['realvalue', 'convertedValue', 'conversionParams', "fieldMap",'statusTableAdapter','detailsTableAdapter','journalTableAdapter','conversionsTableAdapter','masterTableAdapter'];
    };
    static datasources = [:]


    NetcoolConnection connection ;


    static hasMany = [:]

    static constraints={
    connection(nullable:true)


    }

    static mappedBy=["connection":"netcoolDatasources"]
    static belongsTo = []

    def statusTableAdapter;
    def detailsTableAdapter;
    def journalTableAdapter;
    def conversionsTableAdapter;
    def masterTableAdapter;

    static transients = ['realvalue', 'convertedValue', 'conversionParams', "fieldMap",'statusTableAdapter','detailsTableAdapter','journalTableAdapter','conversionsTableAdapter','masterTableAdapter']

    def onLoad = {
        def statusTable = "alerts.status";
        def detailsTable = "alerts.details";
        def journalTable = "alerts.journal";
        def conversionsTable = "alerts.conversions";
        def masterTable = "master.names";

        def statusTableKey = "ServerSerial";
        def detailsTableKey = "Identifier";
        def journalTableKey = "Serial";
        def conversionsTableKey = "Colname";
        def masterTableKey = "UID";

        this.statusTableAdapter = new SingleTableDatabaseAdapter(connection.name, statusTable, statusTableKey, 0, Logger.getRootLogger());
        this.detailsTableAdapter = new SingleTableDatabaseAdapter(connection.name, detailsTable, detailsTableKey, 0, Logger.getRootLogger());
        this.journalTableAdapter = new SingleTableDatabaseAdapter(connection.name, journalTable, journalTableKey, 0, Logger.getRootLogger());
        this.conversionsTableAdapter = new SingleTableDatabaseAdapter(connection.name, conversionsTable, conversionsTableKey, 0, Logger.getRootLogger());
        this.masterTableAdapter = new SingleTableDatabaseAdapter(connection.name, masterTable, masterTableKey, 0, Logger.getRootLogger());
    }



    private static ACTION_OWNER_PROPERTY_NAME = "____ACTION_OWNER___";
    private static journalMessages = new SimpleTemplateEngine();
    private static Map JOURNAL_MESSAGES = [Severity:journalMessages.createTemplate("Alert is prioritized from \${oldValue} to \${newValue} by \${audit}"),
    SuppressEscl:journalMessages.createTemplate("Alert is prioritized from \${oldValue} to \${newValue} by \${audit}"),
    Acknowledged:journalMessages.createTemplate("Alert is \${newValue == 1?'':'un'}acknowledged by \${audit}")]
    static JOURNAL_TRACKING_PROPERTY_LIST = ["Severity", "SuppressEscl", "Acknowledged"]



    def addEvent(Map params){
        String identifier = params.Identifier;
        if (identifier == null){
            throw new Exception("Missing identifier for add operation")
        }
        StringBuffer sb = new StringBuffer();
        sb.append("insert into alerts.status ");

        StringBuffer columns = new StringBuffer("(");
        StringBuffer values = new StringBuffer("(");
        params1.each{String columnName, Object colValue->
            if(columnName != "ServerSerial" && columnName != "Identifier" )
            {
                columns.append(columnName);
                if( colValue instanceof Number){
                    values.append(colValue);
                }
                else{
                    values.append("'" + colValue + "'");
                }
                columns.append(" , ");
                values.append(" , ");
            }
        }
        columns.deleteCharAt(columns.length()-2);
        columns.append(")");
        values.deleteCharAt(values.length()-2);
        values.append(")");

        sb.append(columns.toString());
        sb.append(" values ");
        sb.append(values.toString());
		statusTableAdapter.executeUpdate(sb.toString(), []);
    }

    def updateEvent(Map params ){
        def serverserial = params.ServerSerial;
        def event = getEvent(serverserial);
		if(event.size() == 0){
			throw new Exception("Event with ServerSerial <" + serverserial + "> does not exist");
		}

		StringBuffer sb = new StringBuffer("update alerts.status set ");
		StringBuffer updatedColumns= new StringBuffer();
        def actionOwner = params.remove(ACTION_OWNER_PROPERTY_NAME);
        actionOwner = actionOwner?actionOwner:"Unknown";
        params.each{String columnName, Object colValue->
            if(columnName != "ServerSerial" && columnName != "Identifier" && columnName != "journalParams" )
            {
                updatedColumns.append(columnName);
                if( colValue instanceof Number){
                    updatedColumns.append("=");
                    updatedColumns.append(colValue);
                }
                else{
                    updatedColumns.append("='");
                    updatedColumns.append(colValue);
                    updatedColumns.append("'");
                }
                updatedColumns.append(", ");
            }
        }
        updatedColumns.deleteCharAt(updatedColumns.length()-2);
		sb.append(updatedColumns.toString());
		sb.append(" where ServerSerial=").append(serverserial);

		statusTableAdapter.executeUpdate(sb.toString(), []);

        JOURNAL_TRACKING_PROPERTY_LIST.each{String propertyName->
            if(params[propertyName] != null && event[propertyName] != params[propertyName]
                && ( !params.journalParams || (params.journalParams[propertyName] == null)
                || (params.journalParams[propertyName] != null && params.journalParams[propertyName] == "true")) )
            {
                def oldVal = NetcoolConversionParameter.getConvertedValue(propertyName, event[propertyName]);
                def newVal = NetcoolConversionParameter.getConvertedValue(propertyName, params[propertyName]);
                def text = JOURNAL_MESSAGES[propertyName].make([oldValue:oldVal, newValue:newVal, audit:actionOwner]);
                writeToJournal(serverserial, text);
            }
        }
    }

    def removeEvent(serverserial){
        def event = getEvent(serverserial);
		if(event.size() == 0)
		{
			throw new Exception("Event with ServerSerial <" + serverserial + "> does not exist");
		}
		statusTableAdapter.removeRecord(serverserial);
		detailsTableAdapter.removeRecord(event.identifier);
		journalTableAdapter.removeRecord(Integer.parseInt(event.serial));
    }

    def setSeverityAction(serverserial, severity, userName) {
        def updateProps = [:]
        def journalParams = [:]
        updateProps.put("ServerSerial",serverserial);
        updateProps.put("Severity",severity);
        updateProps.put(ACTION_OWNER_PROPERTY_NAME,userName);
        updateProps.put("Acknowledged", 0);
        journalParams.put("Acknowledged", false);
        updateProps.put( "journalParams", journalParams);
		def updatedEvent = updateEvent(updateProps );
	}

    def suppressAction(serverserial, suppress, userName){
		def updateProps = [:];
        updateProps.put("ServerSerial", serverserial);
        updateProps.put("SuppressEscl", suppress);
		updateEvent(updateProps);
	}

    def taskListAction(serverserial, boolean addToTaskList){
        def updateProps =[:];
		updateProps.put("ServerSerial",serverserial);
        updateProps.put("TaskList",addToTaskList?1:0)
        updateEvent(updateProps);
    }

    def assignAction(serverserial, ownerUID){
        String partOfAlertText= "Alert is assigned to user ";
        assign(serverserial, ownerUID, partOfAlertText);
    }

    def takeOwnershipAction(serverserial, ownerUID) throws Exception	{
          String partOfAlertText= "Ownership of alert taken by ";
          assign(serverserial, ownerUID, partOfAlertText);
    }

    def acknowledgeAction(serverserial, boolean isAcknowledge, userName) {
		def updateProps =[:];
		updateProps.put("ServerSerial",serverserial);
		updateProps.put("Acknowledged",isAcknowledge?1:0);
        updateProps.put(ACTION_OWNER_PROPERTY_NAME,userName);
        updateEvent(updateProps);
	}

	def writeToJournal(serial, text){
        def newProps =[:];
        def intSerial;
        if (serial instanceof String){
            intSerial = Integer.parseInt(serial);
        }
        else{
            intSerial = serial;
        }

        newProps.put("Serial",intSerial);
		int date;
        date = (int)(System.currentTimeMillis()/1000);
		def keyfield = (serial + ":0:" + date);
        newProps.put("KeyField",keyfield);
        newProps.put("Chrono",date);
		newProps.put("Text1",text);
		StringBuffer sb = new StringBuffer();
        sb.append("insert into alerts.journal (Serial,KeyField,Chrono,Text1) values (");
        sb.append("${intSerial},'${keyfield}',${date},'${text}')");
		journalTableAdapter.executeUpdate(sb.toString(), []);
    }

    private def assign(serverserial, ownerUID, partOfAlertText){
        def intOwnerUID;
        if (ownerUID instanceof String){
            intOwnerUID = Integer.parseInt(ownerUID);
        }
        else{
            intOwnerUID = ownerUID;
        }

        def nameRecord = masterTableAdapter.getRecord(intOwnerUID);
        if(nameRecord.size() == 0)
        {
            throw new Exception("User <" + ownerUID + "> does not exist");
        }

        def event = getEvent(serverserial);
        if (event.size()==0){
            throw new Exception("Event with ServerSerial <" + serverserial + "> does not exist");
        }

        def updateProps =[:];
        updateProps.put("ServerSerial",serverserial);
        updateProps.put("OwnerUID",intOwnerUID);
        updateProps.put("Acknowledged",0);
        updateEvent(updateProps);

		String text = partOfAlertText + nameRecord.name.trim() + ".";
		writeToJournal(event.serial, text);
    }

    /*
    *
    * GET OPERATIONS
    *
    *
     */

    def getProperty(Map keys, String propName)
    {
        def props = statusTableAdapter.getRecordMultiKey(keys, [propName]);
        if (props)
        {
            return props[propName];
        }
        return "";
    }

    def getProperties(Map keys, List properties)
    {
        def props = statusTableAdapter.getRecordMultiKey(keys, properties);
        return props;
    }
    def getEventByIdentifier(identifier){
	    def query = "select * from alerts.status where Identifier = ?";
        def result = statusTableAdapter.executeQuery(query, [identifier]);
		if(result.size() > 0){
            return result[0];
        }
        else{
            return [:];
        }
    }

    def getEvent(serverserial){
        def event = statusTableAdapter.getRecord(serverserial, []);
        return event;
    }

    def getEvent(serverserial, List columns){
        def event = statusTableAdapter.getRecord(serverserial, columns);
        return event;
    }

    def getEvents(){
        def results = statusTableAdapter.getRecords();
        return results;
    }

    def getEvents(List colList){
        def results = statusTableAdapter.getRecords(colList);
        return results;
    }

    def getEvents(String whereClause){
        def results = statusTableAdapter.getRecords(whereClause);
        return results;
    }

    def getJournalEntries(String whereClause){
        def results = journalTableAdapter.getRecords(whereClause);
        return results;
    }


    def getSerial(identifier){
		def event = getEventByIdentifier(identifier);
		if(event.size() == 0){
			throw new Exception("Event <" + identifier+ "> does not exist");
		}
		return Integer.parseInt(event.serverserial);
    }

    def getSerialFromNetcool(identifier){
	    def query = "Identifier='$identifier'";
	    def event = statusTableAdapter.getRecords(query);
		if(event.size() == 0){
			throw new Exception("Event <" + identifier+ "> does not exist");
		}
		return Integer.parseInt(event[0].serverserial);
    }

    public Map getFieldMap(){
        def fieldMap = [:];
        def query = "select * from alerts.status";
        def rs = statusTableAdapter.executeQuery(query,[],0);
	 	def meta = rs.metaData;
	 	def last = meta.columnCount+1;
		for (i in 1..< last) {
			if(meta.getColumnType(i) == 12 || meta.getColumnType(i) == 1 || meta.getColumnType(i) == -1 || meta.getColumnType(i) == 2004){
				fieldMap.put(meta.getColumnLabel(i),"string");
			}
			else{
				fieldMap.put(meta.getColumnLabel(i),"number");
			}
		}
		return fieldMap;
    }

    def getConversionParams(){
        return conversionsTableAdapter.getRecords();
    }
}
