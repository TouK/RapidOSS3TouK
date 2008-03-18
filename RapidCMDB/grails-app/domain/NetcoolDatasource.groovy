import datasources.SingleTableDatabaseAdapter;
import org.apache.log4j.Logger;

class NetcoolDatasource {
    NetcoolConnection connection;
    final String statusTable = "alerts.status";
    final String detailsTable = "alerts.details";
    final String journalTable = "alerts.journal";
    final String conversionsTable = "alerts.conversions";
    final String masterTable = "master.names";

    final String statusTableKey = "Identifier";
    final String detailsTableKey = "Identifier";
    final String journalTableKey = "Serial";
    final String conversionsTableKey = "Colname";
    final String masterTableKey = "UID";
    static def STRING_COL_NAMES =[];

    def statusTableAdapter;
    def detailsTableAdapter;
    def journalTableAdapter;
    def conversionsTableAdapter;
    def masterTableAdapter;

    static transients = ["statusTableAdapter","detailsTableAdapter","journalTableAdapter","conversionsTableAdapter","masterTableAdapter",
                         "statusTable","detailsTable","journalTable","conversionsTable","masterTable",
                         "statusTableKey","detailsTableKey","journalTableKey","conversionsTableKey","masterTableKey"]

    def onLoad = {
        this.statusTableAdapter = new SingleTableDatabaseAdapter(connection.name, statusTable, statusTableKey, 0, Logger.getRootLogger());
        this.detailsTableAdapter = new SingleTableDatabaseAdapter(connection.name, detailsTable, detailsTableKey, 0, Logger.getRootLogger());
        this.journalTableAdapter = new SingleTableDatabaseAdapter(connection.name, journalTable, journalTableKey, 0, Logger.getRootLogger());
        this.conversionsTableAdapter = new SingleTableDatabaseAdapter(connection.name, conversionsTable, conversionsTableKey, 0, Logger.getRootLogger());
        this.masterTableAdapter = new SingleTableDatabaseAdapter(connection.name, masterTable, masterTableKey, 0, Logger.getRootLogger());
        if (STRING_COL_NAMES.size()==0){
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

    def addEvent(Map props){
        String identifier = props.Identifier;
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
        columns.deleteCharAt(columns.length-2);
        columns.append(")");
        values.deleteCharAt(values.length-2);
        values.append(")");

        sb.append(columns.toString());
        sb.append(" values ");
        sb.append(values.toString());
		statusTableAdapter.executeQuery(sb.toString(), []);
    }

    def updateEvent(Map params){
        def identifier = params.Identifier;
        def event = getEvent(identifier);
		if(event.size() == 0){
			throw new Exception("Event <" + identifier + "> does not exist");
		}

        def additionalParams = [:];
        additionalParams.putAll(params);
        additionalParams.remove("Identifier");
		StringBuffer sb = new StringBuffer("update alerts.status set ");
		StringBuffer updatedColumns= new StringBuffer();
		boolean flag = false;
		for (Iterator iter = additionalParams.keySet().iterator(); iter.hasNext();)
		{
			String columnName = (String) iter.next();
			if(flag)
				updatedColumns.append(", ");
			updatedColumns.append(columnName);

			if(STRING_COL_NAMES.contains(columnName)){
				updatedColumns.append("='");
				updatedColumns.append(additional.get(columnName));
				updatedColumns.append("'");
			}
			else{
				updatedColumns.append("=");
				updatedColumns.append(additionalParams.get(columnName));
			}
			flag = true;
		}
		sb.append(updatedColumns.toString());
		sb.append(" where Identifier='").append(identifier).append("'");
		statusTableAdapter.executeQuery(sb.toString(), []);
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
		journalTableAdapter.removeRecord(event.SERIAL);
    }

    def list(){
        // ??????????????
    }

    def taskListAction(String identifier, boolean addToTaskList){
        def updateProps =[:];
		updateProps.put("Identifier",identifier);
        if(addToTaskList){
            updateProps.put("TaskList","1");
        }
		else{
			updateProps.put("TaskList","0");
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

        def oldSeverity = event.SEVERITY;

		def updateProps = [:];
		updateProps.put("Identifier", identifier);
        updateProps.put("Severity", severity);
        updateProps.put("Acknowledged", "0");
		statusTableAdapter.updateRecord(updateProps);

		def whereClause= "Colname = 'Severity'";
        def severityValues = [:];
        severityValues = getFromConversions(whereClause);

        def oldVal= conversions.get("Severity"+oldSeverity);
        def newVal = conversions.get("Severity"+severity);

        String text = "Alert prioritized from " + oldVal + " to " + newVal + " by " + userName;
		writeToJournal(event.SERIAL, text);
	}

    def suppressAction(identifier, suppress, userName){
        def event = getEvent(identifier);
        if(event.size() == 0){
            throw new Exception("Event <" + identifier + "> does not exist");
        }

        def oldSuppress = event.SUPPRESSESCL;

		def updateProps = [:];
		updateProps.put("Identifier", identifier);
        updateProps.put("SuppressEscl", suppress);
		statusTableAdapter.updateRecord(updateProps);

        def whereClause= "Colname = 'SuppressEscl'";
        def conversions = [:];
        conversions = getFromConversions(whereClause);

        def oldVal= conversions.get("SuppressEscl"+oldSuppress);
        def newVal = conversions.get("SuppressEscl"+suppress);

		String text = "Alert prioritized from " + oldVal + " to " + newVal + " by " + userName + ".";

		writeToJournal(event.SERIAL, text);
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
			updateProps.put("Acknowledged","1");
            text = "Alert acknowledged by " + userName;
		}
		else{
            updateProps.put("Acknowledged","0");
			text = "Alert unAcknowledged by " + userName;
		}
        statusTableAdapter.updateRecord(updateProps);
        writeToJournal(event.SERIAL, text);
	}

	def writeToJournal(serial, text){
        def newProps =[:];
        newProps.put("Serial",serial);
		int date;
        date = (int)(System.currentTimeMillis()/1000);
		def keyfield = (serial + ":0:" + date);
        newProps.put("Keyfield",keyfield);
        newProps.put("Chrono",date);
		newProps.put("Text1",text);
		journalTableAdapter.addRecord(newProps);
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
        def nameRecord = masterTableAdapter.getRecord(ownerUID);

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
        updateProps.put("OwnerUID",ownerUID);
        updateProps.put("Acknowledged","0");
        statusTableAdapter.updateRecord(updateProps);

		String text = partOfAlertText + userName.trim() + ".";
		writeToJournal(event.SERIAL, text);
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
