import datasources.SingleTableDatabaseAdapter
import org.apache.log4j.Logger

class NetcoolDatasource extends BaseDatasource{
    NetcoolConnection connection;

    static def STRING_COL_NAMES =[];

    def statusTableAdapter;
    def detailsTableAdapter;
    def journalTableAdapter;
    def conversionsTableAdapter;
    def masterTableAdapter;

    static transients = ['statusTableAdapter','detailsTableAdapter','journalTableAdapter','conversionsTableAdapter','masterTableAdapter']
//    ,
//                         'statusTable','detailsTable','journalTable','conversionsTable','masterTable',
//                         'statusTableKey','detailsTableKey','journalTableKey','conversionsTableKey','masterTableKey']

    def onLoad = {
        def statusTable = "alerts.status";
        def detailsTable = "alerts.details";
        def journalTable = "alerts.journal";
        def conversionsTable = "alerts.conversions";
        def masterTable = "master.names";

        def statusTableKey = "Identifier";
        def detailsTableKey = "Identifier";
        def journalTableKey = "Serial";
        def conversionsTableKey = "Colname";
        def masterTableKey = "UID";
       
        this.statusTableAdapter = new SingleTableDatabaseAdapter(connection.name, statusTable, statusTableKey, 0, Logger.getRootLogger());
        this.detailsTableAdapter = new SingleTableDatabaseAdapter(connection.name, detailsTable, detailsTableKey, 0, Logger.getRootLogger());
        this.journalTableAdapter = new SingleTableDatabaseAdapter(connection.name, journalTable, journalTableKey, 0, Logger.getRootLogger());
        this.conversionsTableAdapter = new SingleTableDatabaseAdapter(connection.name, conversionsTable, conversionsTableKey, 0, Logger.getRootLogger());
        this.masterTableAdapter = new SingleTableDatabaseAdapter(connection.name, masterTable, masterTableKey, 0, Logger.getRootLogger());
        if (STRING_COL_NAMES == null){
            populateStringColumnNames();
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
        def props = statusTableAdapter.getRecordMultiKey(keys, [propName]);
        return props;
    }

    def addEvent(Map params){
        String identifier = params.Identifier;
        if (identifier == null){
            throw new Exception("Missing identifier for add operation")
        }
        StringBuffer sb = new StringBuffer();
        sb.append("insert into alerts.status ");

        StringBuffer columns = new StringBuffer("(");//"(Identifier ");
        StringBuffer values = new StringBuffer("(");//"('" + identifier + "'");
        for (Iterator iter = params.keySet().iterator(); iter.hasNext();){
            String columnName = (String)iter.next();
            columns.append(columnName);
            if( STRING_COL_NAMES.contains(columnName)){
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
        def identifier = params.Identifier;
        def event = getEvent(identifier);
		if(event.size() == 0){
			throw new Exception("Event <" + identifier + "> does not exist");
		}

        def additional = [:];
        additional.putAll(params);
        additional.remove("Identifier");
		StringBuffer sb = new StringBuffer("update alerts.status set ");
		StringBuffer updatedColumns= new StringBuffer();
		for (Iterator iter = additional.keySet().iterator(); iter.hasNext();)
		{
			String columnName = (String) iter.next();
			updatedColumns.append(columnName);
			if(STRING_COL_NAMES.contains(columnName)){
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
		sb.append(" where Identifier='").append(identifier).append("'");
		statusTableAdapter.executeUpdate(sb.toString(), []);
    }

    def getEvent(identifier){
        def event = statusTableAdapter.getRecord(identifier, []);
        return event;
    }

    def getEvent(identifier, List columns){
        def event = statusTableAdapter.getRecord(identifier, columns);
        return event;
    }

    def removeEvent(identifier){
        def event = getEvent(identifier);
		if(event.size() == 0)
		{
			throw new Exception("Event <" + identifier + "> does not exist");
		}
		statusTableAdapter.removeRecord(identifier);
		detailsTableAdapter.removeRecord(identifier);
		journalTableAdapter.removeRecord(Integer.parseInt(event.serial));
    }

    def list(){
        // ??????????????
    }

    def taskListAction(String identifier, boolean addToTaskList){
        def updateProps =[:];
		updateProps.put("Identifier",identifier);
        if(addToTaskList){
            updateProps.put("TaskList",1);
        }
		else{
			updateProps.put("TaskList",0);
        }
        statusTableAdapter.updateRecord(updateProps);
    }

    def assignAction(identifier, ownerUID){
        String partOfAlertText= "Alert assigned to user ";
        assign(identifier, ownerUID, partOfAlertText);
    }

    def takeOwnershipAction(identifier, ownerUID) throws Exception	{
          String partOfAlertText= "Ownership of alert taken by ";
          assign(identifier, ownerUID, partOfAlertText);
    }

    def setSeverityAction(identifier, severity, userName) {
		def event = getEvent(identifier);
		if(event.size() == 0){
			throw new Exception("Event <" + identifier + "> does not exist");
		}

        def oldSeverity = event.severity;

		def updateProps = [:];
		updateProps.put("Identifier", identifier);
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

		def whereClause= "Colname = 'Severity'";
        def severityValues = [:];
        severityValues = getFromConversions(whereClause);

        def oldVal= conversions.get("Severity"+oldSeverity);
        def newVal = conversions.get("Severity"+severity);

        String text = "Alert prioritized from " + oldVal + " to " + newVal + " by " + userName;
		writeToJournal(event.serial, text);
	}

    def suppressAction(identifier, suppress, userName){
        def event = getEvent(identifier);
        if(event.size() == 0){
            throw new Exception("Event <" + identifier + "> does not exist");
        }

        def oldSuppress = event.suppressescl;

		def updateProps = [:];
		updateProps.put("Identifier", identifier);
				def intSeverity;
		if (suppress instanceof String){
            intSuppress = Integer.parseInt(suppress);
        }
        else{
            intSuppress = suppress;
        }
        updateProps.put("SuppressEscl", intSuppress);
		statusTableAdapter.updateRecord(updateProps);

        def whereClause= "Colname = 'SuppressEscl'";
        def conversions = [:];
        conversions = getFromConversions(whereClause);

        def oldVal= conversions.get("SuppressEscl"+oldSuppress);
        def newVal = conversions.get("SuppressEscl"+suppress);

		String text = "Alert prioritized from " + oldVal + " to " + newVal + " by " + userName + ".";

		writeToJournal(event.serial, text);
	}

    def acknowledgeAction(String identifier, String userName, boolean isAcknowledge) {
		String sql;
		String text;
		def event = getEvent(identifier);
		if( event.size() == 0){
			throw new Exception("Event <" + identifier + "> does not exist");
		}

		def updateProps =[:];
		updateProps.put("Identifier",identifier);
		if(isAcknowledge){
			updateProps.put("Acknowledged",1);
            text = "Alert acknowledged by " + userName;
		}
		else{
            updateProps.put("Acknowledged",0);
			text = "Alert unAcknowledged by " + userName;
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
        println sb.toString();
		journalTableAdapter.executeUpdate(sb.toString(), []);
    }

    private def getFromConversions(whereClause){
        def conversions = [:];
        def columns = ["KeyField", "Conversion"];
        def result = conversionsTableAdapter.getRecords(whereClause, columns);
        def last = result.size()-1;
        for (i in 0..last){
            conversions.put(result.get("KeyField").toString().trim(), record.get("Conversion").toString().trim());
        }
        return conversions;
    }

    private def assign(identifier, ownerUID, partOfAlertText){
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

        def event = getEvent(identifier);
        if (event.size()==0){
            throw new Exception("Event <" + identifier + "> does not exist");
        }

        def updateProps =[:];
        updateProps.put("Identifier",identifier);
        updateProps.put("OwnerUID",intOwnerUID);
        updateProps.put("Acknowledged",0);
        statusTableAdapter.updateRecord(updateProps);

		String text = partOfAlertText + nameRecord.name.trim() + ".";
		writeToJournal(event.serial, text);
    }

    private def populateStringColumnNames(){
        def query = "select * from alerts.status where StateChange >=" + Integer.MAX_VALUE ;
        def result = statusTableAdapter.executeQuery(query,[],0);
        def data = result.getMetaData();
        int i =1;
        while( i <= data.getColumnCount()){
            if(data.getColumnType(i) == 12 || data.getColumnType(i) == 1 || data.getColumnType(i) == -1 || data.getColumnType(i) == 2004){
                STRING_COL_NAMES.add(data.getColumnName(i));
            }
            i++;
        }
    }
}
