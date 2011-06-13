// ----------------- CONFIGURATION STARTS -----------------
DS_NAME = "dbDatasource";
TABLE_NAME ="DEVICES";
TIMESTAMP_FIELD = "LAST_UPDATE_TIME" //LAST_UPDATE_TIME is supposed to be a number timestamp in db
TIMESTAMP_LOOKUP_KEY = "dbPollerTimestamp"
MODEL = RsComputerSystem
BULK_SIZE = 100;
MAX_TIMESTAMP_INTERVAL = 60000;
// ----------------- CONFIGURATION ENDS -----------------

def SQL = "select * from FROM ${TABLE_NAME} where ${TIMESTAMP_FIELD} > ? AND ${TIMESTAMP_FIELD} < ? order by ${TIMESTAMP_FIELD}"

def ds = datasource.DatabaseDatasource.getOnDemand(name:DS_NAME);
if(ds == null){
  throw new Exception("No db datasource defined with name ${DS_NAME}");
}

def lastProcessedRecordTimestamp = getLastPrcessedRecordTimestamp();

BULK_ACTIONS = [];
ds.runQuery(SQL, [lastProcessedRecordTimestamp, lastProcessedRecordTimestamp+MAX_TIMESTAMP_INTERVAL], 10){dbRecord->
  def data = [:]
  data.name = dbRecord.NAME
  data.className = dbRecord.CLASS_NAME
  data.description = dbRecord.DESCRIPTION
  data.displayName = dbRecord.NAME
  data.snmpAddress = dbRecord.SNMP_ADDRESS
  data.systemName = dbRecord.SYSTEM_NAME
  data.vendor = dbRecord.VENDOR
  data.model = dbRecord.MODEL
  data.rsDatasource = ds.name

  addBulkItem(data);
  lastProcessedRecordTimestamp = dbRecord[TIMESTAMP_LOOKUP_KEY]
}
processBulkActions();

RsLookup.add(name:TIMESTAMP_LOOKUP_KEY, value:lastProcessedRecordTimestamp);



def addBulkItem(dbRecordProps){
  BULK_ACTIONS.add(dbRecordProps);
  if(BULK_ACTIONS.size() >= BULK_SIZE){
    processBulkActions();
  }
}

def processBulkActions(){
  application.RapidApplication.executeBatch{
      BULK_ACTIONS.each{ dbRecordProps ->
          MODEL.add(dbRecordProps);
      }
  }
  BULK_ACTIONS.clear();
}


def getLastPrcessedRecordTimestamp(){
  def tsLookup = RsLookup.get(name:TIMESTAMP_LOOKUP_KEY)
  def timestamp = 0;
  if(tsLookup != null){
    timestamp = Long.parseLong(tsLookup.value)
  }
  return timestamp;
}