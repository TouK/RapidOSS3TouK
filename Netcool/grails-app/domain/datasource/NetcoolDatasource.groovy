package datasource

import connection.NetcoolConnection
import datasource.SingleTableDatabaseAdapter
import org.apache.log4j.Logger

class NetcoolDatasource extends BaseDatasource{
    static searchable = {
        except = [];
    };
    static datasources = [:]


    NetcoolConnection connection ;


    static hasMany = [:]

    static constraints={
    connection(nullable:true)


    }

    static mappedBy=["connection":"netcoolDatasources"]
    static belongsTo = []

    static STRING_COL_NAMES =[];
    static FIELDMAP =[:];
    static NAMEMAP =[:];
	static CONVERSIONMAP = [:];

    def statusTableAdapter;
    def detailsTableAdapter;
    def journalTableAdapter;
    def conversionsTableAdapter;
    def masterTableAdapter;

    static transients = ['statusTableAdapter','detailsTableAdapter','journalTableAdapter','conversionsTableAdapter','masterTableAdapter']

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
      	if (!FIELDMAP || FIELDMAP.size()==0){
            populateFieldMap();
        }
        if (!CONVERSIONMAP || CONVERSIONMAP.size()==0){
            populateConversionMap();
        }
    }

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

    def addEvent(Map params1){
	    def params = [:];
	    params.putAll(params1);
        String identifier = params.Identifier;
        if (identifier == null){
            throw new Exception("Missing identifier for add operation")
        }
        if (params.ServerSerial){
	        params.remove("ServerSerial");
        }

        StringBuffer sb = new StringBuffer();
        sb.append("insert into alerts.status ");

        StringBuffer columns = new StringBuffer("(");
        StringBuffer values = new StringBuffer("(");
        for (Iterator iter = params.keySet().iterator(); iter.hasNext();){
            String columnName = (String)iter.next();
            columns.append(columnName);
            if( FIELDMAP.get(columnName)=="string"){
                values.append("'" + params.get(columnName) + "'");
            }
            else{
                values.append(params.get(columnName));
            }
            columns.append(" , ");
            values.append(" , ");
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

    def updateEvent(Map params){
        def serverserial = params.ServerSerial;
        def event = getEvent(serverserial);
		if(event.size() == 0){
			throw new Exception("Event with ServerSerial <" + serverserial + "> does not exist");
		}

        def additional = [:];
        additional.putAll(params);
        additional.remove("ServerSerial");
        additional.remove("Identifier");
        if (additional.size()==0){
	        throw new Exception("Can not update ServerSerial or Identifier!")
        }
		StringBuffer sb = new StringBuffer("update alerts.status set ");
		StringBuffer updatedColumns= new StringBuffer();
		for (Iterator iter = additional.keySet().iterator(); iter.hasNext();)
		{
			String columnName = (String) iter.next();
			updatedColumns.append(columnName);
            if( FIELDMAP.get(columnName)=="string"){
				updatedColumns.append("='");
				updatedColumns.append(additional.get(columnName));
				updatedColumns.append("'");
			}
			else{
				updatedColumns.append("=");
				updatedColumns.append(additional.get(columnName));
			}
			updatedColumns.append(", ");
		}
        updatedColumns.deleteCharAt(updatedColumns.length()-2);
		sb.append(updatedColumns.toString());
		sb.append(" where ServerSerial=").append(serverserial);

		statusTableAdapter.executeUpdate(sb.toString(), []);
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

    def setSeverityAction(serverserial, severity, userName) {
		def event = getEvent(serverserial);
		if(event.size() == 0){
			throw new Exception("Event with ServerSerial <" + serverserial + "> does not exist");
		}

        def oldSeverity = event.severity;

		def updateProps = [:];
		updateProps.put("ServerSerial", serverserial);
		def intSeverity;
		if (severity instanceof String){
            intSeverity = Integer.parseInt(severity);
        }
        else{
            intSeverity = severity;
        }
        updateProps.put("Severity",intSeverity);
        updateProps.put("Acknowledged", 0);
		statusTableAdapter.updateRecord(updateProps);
		/*def whereClause= "Colname = 'Severity'";
        def conversions = [:];
        conversions = getFromConversions(whereClause);*/
        def oldVal = "Severity"+oldSeverity;
        oldVal= CONVERSIONMAP[oldVal];
        def newVal = "Severity"+severity;
        newVal = CONVERSIONMAP[newVal];
        String text = "Alert is prioritized from ${oldVal} to ${newVal} by ${userName}";
		writeToJournal(event.serial, text);
	}

    def suppressAction(serverserial, suppress, userName){
        def event = getEvent(serverserial);
        if(event.size() == 0){
            throw new Exception("Event with ServerSerial <" + serverserial + "> does not exist");
        }

        def oldSuppress = event.suppressescl;
		def updateProps = [:];
		updateProps.put("ServerSerial", serverserial);
		def intSuppress;
		if (suppress instanceof String){
            intSuppress = Integer.parseInt(suppress);
        }
        else{
            intSuppress = suppress;
        }
        updateProps.put("SuppressEscl", intSuppress);
		statusTableAdapter.updateRecord(updateProps);

        /*def whereClause= "Colname = 'SuppressEscl'";
        def conversions = [:];
        conversions = getFromConversions(whereClause);*/
        def oldVal= CONVERSIONMAP.get("SuppressEscl"+oldSuppress);
        def newVal = CONVERSIONMAP.get("SuppressEscl"+suppress);
		String text = "Alert is prioritized from ${oldVal} to ${newVal} by ${userName}";

		writeToJournal(event.serial, text);
	}

    def taskListAction(serverserial, boolean addToTaskList){
        def updateProps =[:];
		updateProps.put("ServerSerial",serverserial);
        if(addToTaskList){
            updateProps.put("TaskList",1);
        }
		else{
			updateProps.put("TaskList",0);
        }
        statusTableAdapter.updateRecord(updateProps);
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
		String sql;
		String text;
		def event = getEvent(serverserial);
		if( event.size() == 0){
			throw new Exception("Event with ServerSerial <" + serverserial + "> does not exist");
		}

		def updateProps =[:];
		updateProps.put("ServerSerial",serverserial);
		if(isAcknowledge){
			updateProps.put("Acknowledged",1);
            text = "Alert is acknowledged by " + userName;
		}
		else{
            updateProps.put("Acknowledged",0);
			text = "Alert is unacknowledged by " + userName;
		}
        statusTableAdapter.updateRecord(updateProps);
        writeToJournal(event.serial, text);
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

    /*public def getFromConversions(whereClause){
        def conversions = [:];
        def columns = ["KeyField", "Conversion"];
        def records = conversionsTableAdapter.getRecords(whereClause, columns);
        for (record in records){
            def key = record.KeyField.toString().trim();
            def value = record.Conversion.toString().trim();
            conversions.put(key, value);
        }
        return conversions;
    }
*/
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
        statusTableAdapter.updateRecord(updateProps);

		String text = partOfAlertText + nameRecord.name.trim() + ".";
		writeToJournal(event.serial, text);
    }

    private def populateFieldMap(){
	    def query = "select * from alerts.status";
        def rs = statusTableAdapter.executeQuery(query,[],0);
	 	def meta = rs.metaData;
	 	def last = meta.columnCount+1;
		for (i in 1..< last) {
			if(meta.getColumnType(i) == 12 || meta.getColumnType(i) == 1 || meta.getColumnType(i) == -1 || meta.getColumnType(i) == 2004){
				FIELDMAP.put(meta.getColumnLabel(i),"string");
				NAMEMAP.put(meta.getColumnLabel(i).toLowerCase(), meta.getColumnLabel(i))
			}
			else{
				FIELDMAP.put(meta.getColumnLabel(i),"number");
				NAMEMAP.put(meta.getColumnLabel(i).toLowerCase(), meta.getColumnLabel(i))
			}
		}
		FIELDMAP.remove("ServerSerial");
		NAMEMAP.remove("serverserial");
		NAMEMAP.remove("class");
		NAMEMAP.put("netcoolclass","Class");
		//return FIELDMAP;
    }

    def populateConversionMap(){
        def columns = ["KeyField", "Conversion"];
        def records = conversionsTableAdapter.getRecords(columns);
        for (record in records){
            def key = record.KeyField.toString().trim();
            def value = record.Conversion.toString().trim();
            CONVERSIONMAP.put(key, value);
        }
    }
}
