/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 4, 2008
 * Time: 11:30:34 AM
 * To change this template use File | Settings | File Templates.
 */
class NetcoolJournal {

    //AUTO_GENERATED_CODE
    static searchable = true;
    static datasources = ["RCMDB":["keys":["serverserial":["nameInDs":"serverserial"], "servername":["nameInDs":"servername"]]]]
    NetcoolEvent event;
    String servername;
    String keyfield;
    String text;
    Long chrono;
    static hasMany = [:]

    static constraints={
        servername(key:["keyfield"], nullable:false, blank:false)
        keyfield(nullable:false, blank:false)
        text(nullable:false, blank:true)
        chrono(nullable:false)
    }
    //AUTO_GENERATED_CODE
}