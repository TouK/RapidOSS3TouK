
    
    public class RrdArchiveOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
    {
        public static final String FUNCTION = "function";
        public static final String STEP = "step";
        public static final String NUMBER_OF_DATAPOINTS = "numberOfDatapoints";
        public static final String XFF = "xff"

        def getMap(){
            Map config= [:];
            config[FUNCTION] = function;
            config[STEP] = step;
            config[NUMBER_OF_DATAPOINTS] = numberOfDatapoints;
            config[XFF] = xff;
            return config;
        }
    }
    