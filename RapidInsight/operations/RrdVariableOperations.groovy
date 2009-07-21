
import com.ifountain.rcmdb.rrd.RrdUtils;

public class RrdVariableOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    static final long ONE_HOUR = 3600000L
    static final long ONE_DAY = 24 * ONE_HOUR
    def conf = [
            year:24*3600,
            month:3600,
            week:120,
    ]

    public static final String DATABASE_NAME = "databaseName";
    public static final String DATASOURCE = "datasource";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String VERTICAL_LABEL = "vlabel";
    public static final String HORIZONTAL_LABEL = "hlabel";
    public static final String LINE = "line";
    public static final String AREA = "area";
    public static final String STACK = "stack";
    public static final String HRULE = "hrule";
    public static final String VRULE = "vrule";
    public static final String RPN = "rpn";
    public static final String DSNAME = "dsname";
    public static final String NAME = "name";
    public static final String COLOR = "color";
    public static final String DESCRIPTION = "description";
    public static final String THICKNESS = "thickness";
    public static final String TITLE = "title";
    public static final String MAX = "max";
    public static final String MIN = "min";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String TYPE = "type";
    public static final String ARCHIVE = "archive";
    public static final String FUNCTION = "function";
    public static final String STEP = "step";
    public static final String NUMBER_OF_DATAPOINTS = "numberOfDatapoints";
    public static final String XFF = "xff"
    public static final String HEARTBEAT= "heartbeat";
    public static final String STEPS = "steps"
    public static final String ROWS = "rows"
    public static final String ROW = "row"
    public static final String RRD_VARIABLES = "rrdVariables";
    public static final String RRD_VARIABLE = "rrdVariable";
    public static final String GRAPH_TEMPLATE = "template";
    public static final String DESTINATION = "destination";

    def createDB() {
        createDefaultArchives();
        RrdUtils.createDatabase (createDBConfig())
    }

    def removeDB() {
        RrdUtils.removeDatabase(fileSource())
    }

    def updateDB(value, time=Date.now()) {
        def data = "" + time + ":" + value
        RrdUtils.updateData(fileSource(), data)
    }

    static def updateDB(databaseName, values, times) {
        if(values.size() == times.size())
        {
            def dataList = []
            for(int i = 0; i < values.size(); i++) {
                dataList.add("" + times[i] + ":" + values[i])
            }
            RrdUtils.updateData(databaseName, dataList)
        }
    }

    def graph(Map config) {

        Map rVariable = [:];
        rVariable[RRD_VARIABLE] = name;
        if(config.containsKey(COLOR) ){
            rVariable[COLOR] = config.get(COLOR);
        }
        if(config.containsKey(THICKNESS)){
            rVariable[THICKNESS] = config.get(THICKNESS)
        }
        if(config.containsKey(TYPE)) {
            rVariable[TYPE] = config.get(TYPE);
        }
        if(config.containsKey(RPN)) {
            rVariable[RPN] = config.get(RPN);
        }
        if (config.containsKey(DESCRIPTION) ){
           rVariable[DESCRIPTION] = config.get(DESCRIPTION)
           config.remove (DESCRIPTION);
       }

       def vlist = [];
       vlist.add(rVariable);
       config[RRD_VARIABLES] = vlist;

       return graphMultiple(config);
    }

    static def graphMultiple(listOfVariables, config){
        def variableList = []
        listOfVariables.each{
            variableList.add([rrdVariable:it.toString()])
        }

        config[RRD_VARIABLES] = variableList
        graphMultiple(config)
    }

    static def graphMultiple(Map config){
        String typeVar = "line";

        Map fConfig = getGeneralSettingsMap(config);

        if(config.containsKey(TYPE) ){
          typeVar = config.get(TYPE);
        }

        if(!config.containsKey(RRD_VARIABLES) ){
            throw new Exception("No rrd variable is specified");
        }
        def rrdVariables = config.get(RRD_VARIABLES);

        def datasourceList = [];
        fConfig[AREA] = [];
        fConfig[LINE] = [];
        fConfig[STACK] = [];
        def typeList = [];
        def rrdVar;
        for(int i=0; i<rrdVariables.size(); i++){
            rrdVar = RrdVariable.get(name:rrdVariables[i][RRD_VARIABLE]);
            if(rrdVariables[i].containsKey(FUNCTION) ){
                def datasourceMap = [:];
                datasourceMap[NAME] = rrdVar.name;
                datasourceMap[DATABASE_NAME] = rrdVar.fileSource();
                datasourceMap[DSNAME] = rrdVar.name;
                datasourceMap[FUNCTION] = rrdVariables[i][FUNCTION];
                datasourceList.add(datasourceMap);

            }else{
                rrdVar.archives.each{
                    def datasourceMap = [:];
                    datasourceMap[NAME] = rrdVar.name;
                    datasourceMap[DATABASE_NAME] = rrdVar.fileSource();
                    datasourceMap[DSNAME] = rrdVar.name;
                    datasourceMap[FUNCTION] = it.function;
                    datasourceList.add(datasourceMap);
                }
            }
            if(rrdVariables[i].containsKey(RPN) ){
                def datasourceMap = [:];
                datasourceMap[NAME] = rrdVariables[i][RPN];
                datasourceMap[RPN] = rrdVariables[i][RPN];
                datasourceList.add(datasourceMap);
            }
            def typeMap = [:];
            typeMap[NAME] = rrdVariables[i].containsKey(RPN) ? rrdVariables[i][RPN] : rrdVar.name

            if(rrdVariables[i].containsKey(DESCRIPTION))
                typeMap[DESCRIPTION] = rrdVariables[i][DESCRIPTION]
            else if(config.containsKey(DESCRIPTION) && rrdVariables.size() == 1)
                typeMap[DESCRIPTION] = config[DESCRIPTION]
            else
                typeMap[DESCRIPTION] = rrdVar.name;

            if(rrdVariables[i].containsKey(COLOR))
                typeMap[COLOR] = rrdVariables[i][COLOR];

            typeMap[THICKNESS] = rrdVariables[i].containsKey(THICKNESS) ? rrdVariables[i][THICKNESS]:2;

            if(rrdVariables[i].containsKey(TYPE) ){
                try{
                    fConfig[rrdVariables[i][TYPE]].add(typeMap)
                }catch (Exception ex){
                    throw new Exception("Not valid type: "+ rrdVariables[i][TYPE]);
                }
            }
            else{
                fConfig[typeVar].add(typeMap);
            }
        }
        Map dbInfo = RrdUtils.getDatabaseInfo(rrdVar.name+".rrd");
        if(!fConfig.containsKey (START_TIME)){
            fConfig[START_TIME] = dbInfo[START_TIME];
        }
        if(!fConfig.containsKey (END_TIME)){
            fConfig[END_TIME] = dbInfo[END_TIME];
        }
        fConfig[DATASOURCE] = datasourceList;

        byte[] bytes = RrdUtils.graph(fConfig);

        return bytes
    }

    private static Map getGeneralSettingsMap(Map config){

       Map fConfig = [:];
       if(config.containsKey(RrdUtils.GRAPH_TEMPLATE)){
           fConfig = getGeneralSettingsMapWithTemplate(config);
       }

       if(config.containsKey(START_TIME) ){
           fConfig[START_TIME] = config.get(START_TIME);
       }
       if(config.containsKey(DESTINATION) ){
           fConfig[DESTINATION] = config.get(DESTINATION);
       }
       if(config.containsKey(END_TIME) ){
           fConfig[END_TIME] = config.get(END_TIME);
       }
       if(config.containsKey(MAX) ){
          fConfig[MAX] = config.get(MAX);
       }
       if(config.containsKey(MIN) ){
          fConfig[MIN] = config.get(MIN);
       }
       if(config.containsKey(HEIGHT) ){
          fConfig[HEIGHT] = config.get(HEIGHT);
       }
       if(config.containsKey(WIDTH) ){
          fConfig[WIDTH] = config.get(WIDTH);
       }
       if(config.containsKey(TITLE) ){
          fConfig[TITLE] = config.get(TITLE);
       }
       if(config.containsKey(VERTICAL_LABEL) ){
          fConfig[VERTICAL_LABEL] = config.get(VERTICAL_LABEL);
       }
       return fConfig;
    }
    private static Map getGeneralSettingsMapWithTemplate(Map config){
       Map fConfig = [:];

       def template = RrdGraphTemplate.get(name:config.get(GRAPH_TEMPLATE));

       if(template.max != Double.NaN )
          fConfig[MAX] = template.max;

       if(template.min != Double.NaN )
          fConfig[MIN] = template.min;

       fConfig[HEIGHT] = (int)template.height;
       fConfig[WIDTH] = (int)template.width;

       if(template.title.length()>0 )
          fConfig[TITLE] = template.title;

       if(template.verticalLabel.length()>0 )
          fConfig[VERTICAL_LABEL] = template.verticalLabel ;

       //note that they are not fConfig
       if(template.description.length()>0 )
          config[DESCRIPTION] = template.description ;

       if(template.color.length()>0 )
          config[COLOR] = template.color ;

       if(template.type.length()>0 )
          config[TYPE] = template.type ;

       return fConfig;
    }

    def graphLastHour(Map config) {
        config[RrdUtils.RRD_VARIABLE] = name
        config[START_TIME] = currentTime() - ONE_HOUR
        config[END_TIME] =  currentTime()
        return RrdUtils.graph(config)
    }

    def graphLastDay(Map config) {
        config[RrdUtils.RRD_VARIABLE] = name
        config[START_TIME] = currentTime() - ONE_DAY
        config[END_TIME] =  currentTime()
        return RrdUtils.graph(config)
    }
    def fetchData(){
        String dbname = fileSource();
        return RrdUtils.fetchData (dbname, name);
    }
    def fetchData(String function, long startTime, long endTime){
        String dbname = fileSource();
        return RrdUtils.fetchData (dbname, name, function, startTime, endTime);
    }
    def fetchDataAsMap(){
        String dbname = fileSource();
        return RrdUtils.fetchDataAsMap (dbname, name);
    }
    def fetchDataAsMap(String function, long startTime, long endTime){
        String dbname = fileSource();
        return RrdUtils.fetchDataAsMap (dbname, name, function, startTime, endTime);
    }
    static def fetchData(String[] datasources, String function, long startTime, long endTime){
        String[] filesources = new String[datasources.length];
        for(int i=0; i<datasources.length; i++){
            filesources[i] = RrdVariable.get(name:datasources[i]).fileSource();
        }
        RrdUtils.fetchData (filesources, datasources, function, startTime, endTime);
    }
    static def fetchData(String[] datasources){
        String[] filesources = new String[datasources.length];
        for(int i=0; i<datasources.length; i++){
            filesources[i] = RrdVariable.get(name:datasources[i]).fileSource();
        }
        RrdUtils.fetchData (filesources, datasources);
    }
    static def fetchDataAsMap(String[] datasources, long[] startTime, long[] endTime){
        String[] filesources = new String[datasources.length];
        String function = RrdVariable.get(name:datasources[0]).archives[0].function ;
        for(int i=0; i<datasources.length; i++){
            filesources[i] = RrdVariable.get(name:datasources[i]).fileSource();
        }
        RrdUtils.fetchDataAsMap(filesources, datasources, function, startTime, endTime);
    }

    static def fetchDataAsMap(String[] datasources){
        String[] filesources = new String[datasources.length];
        for(int i=0; i<datasources.length; i++){
            filesources[i] = RrdVariable.get(name:datasources[i]).fileSource();
        }
        RrdUtils.fetchDataAsMap (filesources, datasources);
    }

    private def currentTime() {
        System.currentTimeMillis();
    }

    public def addDefaultArchive(long duration,long stepDuration){
        Map archiveConf = [:];
        long volume = duration/frequency;
        int arcStep;
        int arcRows;
        if(volume<1) return;
        if(stepDuration<frequency){
            arcStep = 1;
            arcRows = volume + ((duration%frequency==0)?0:1);
        }
        else{
             arcStep = stepDuration/frequency + ((stepDuration%frequency==0)?0:1);
             arcRows = volume/arcStep + ((volume%arcStep==0)?0:1);
        }
        archiveConf[FUNCTION] = "AVERAGE";
        archiveConf[XFF] = 0.5;
        archiveConf[STEP] = arcStep;
        archiveConf[NUMBER_OF_DATAPOINTS] = arcRows;
        addArchive(archiveConf);
//        def archive = RrdArchive.add(function:"AVERAGE", xff:0.5, step:arcStep, numberOfDatapoints:arcRows);
//        addRelation(archives:archive);
        return arcStep;

    }

    private def createDefaultArchives(){
        if(archives.size()>0) return;

        long oneMinute = 60L
        long oneHour = oneMinute*60L;
        long oneDay = oneHour*24L;
        long oneWeek = oneDay*7L;
        long oneMonth = oneDay*30L;
        long oneYear = oneDay*365L;
        int numberOfRows;

        if (addDefaultArchive(oneYear,oneDay) == 1) return;
        if (addDefaultArchive(oneMonth,oneHour*2) == 1) return;
        if (addDefaultArchive(oneWeek,oneMinute*30) == 1) return;
        if (addDefaultArchive(oneDay,oneMinute*4) == 1) return;
        if (addDefaultArchive(oneHour,12) == 1) return;
    }

    private def createDBConfig() {
        def dbConfig = [:]

        //there could be change in file name
        dbConfig[DATABASE_NAME] = fileSource()

        dbConfig[START_TIME] = startTime
        dbConfig[STEP] = frequency

        def datapoint = [:]

        datapoint[NAME] = name
        datapoint[TYPE] = type
        datapoint[HEARTBEAT] = heartbeat
        datapoint[MAX] = max
        datapoint[MIN] = min

        dbConfig[DATASOURCE] = [datapoint]

        def archiveList = []

        archives.each
        {
            def archive = [:]
            archive[FUNCTION] = it.function
            archive[XFF] = it.xff
            archive[STEPS] = it.step
            archive[ROWS] = it.numberOfDatapoints

            archiveList.add(archive)
        }

        dbConfig[ARCHIVE] = archiveList

        return dbConfig
    }

    public def addArchive(Map config){
//        if there is an other archive with same step edit existing archive
//        if(isStepExists(config)){ return; }
        
        Map archiveMap = [:];
        if(config.containsKey(FUNCTION))
            archiveMap[FUNCTION] = config.get(FUNCTION);
        if(config.containsKey(XFF))
            archiveMap[XFF] = config.get(XFF);
        if(config.containsKey(STEP))
            archiveMap[STEP] = config.get(STEP);
        if(config.containsKey(NUMBER_OF_DATAPOINTS))
            archiveMap[NUMBER_OF_DATAPOINTS] = config.get(NUMBER_OF_DATAPOINTS);
        def archive = RrdArchive.add(archiveMap);
        addRelation(archives:archive);
    }
    private boolean isStepExists(Map config){
        boolean exist = false;
        if (config.containsKey(STEP) && config.containsKey(NUMBER_OF_DATAPOINTS)){
            archives.each{
                if(it.step == config.get(STEP)){
                    if(config.get(NUMBER_OF_DATAPOINTS)>it.numberOfDatapoints){
                        it.numberOfDatapoints = config.get(NUMBER_OF_DATAPOINTS);
                    }
                    exist = true;
                }
            }
        }else if(config.containsKey(STEP) ){
            archives.each{
                if(it.step == config.get(STEP)){
                    exist = true;
                }
            }
        }
        return exist;
    }

    def fileSource() {
        return name + ".rrd";
    }

}
