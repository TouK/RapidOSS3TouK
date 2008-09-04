
import com.ifountain.core.domain.annotations.*;

class NetcoolEvent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__"];
    };
    static datasources = ["RCMDB":["keys":["servername":["nameInDs":"servername"], "serverserial":["nameInDs":"serverserial"]]]]


    String identifier ="";

    Long serial =0;

    String node ="";

    String nodealias ="";

    String manager ="";

    String agent ="";

    String alertgroup ="";

    String alertkey ="";

    Long severity =0;

    String summary ="";

    Long statechange =0;

    Long firstoccurrence =0;

    Long lastoccurrence =0;

    Long internallast =0;

    Long poll =0;

    Long nctype =0;

    Long tally =0;

    String ncclass ="";

    Long grade =0;

    String location ="";

    String owneruid ="";

    String ownergid ="";

    String acknowledged ="";

    Long flash =0;

    String eventid ="";

    Long expiretime =0;

    Long processreq =0;

    Long suppressescl =0;

    String customer ="";

    String service ="";

    Long physicalslot =0;

    Long physicalport =0;

    String physicalcard ="";

    Long tasklist =0;

    String nmosserial ="";

    Long nmosobjinst =0;

    Long nmoscausetype =0;

    String localnodealias ="";

    String localpriobj ="";

    String localsecobj ="";

    String localrootobj ="";

    String remotenodealias ="";

    String remotepriobj ="";

    String remotesecobj ="";

    String remoterootobj ="";

    Long x733eventtype =0;

    Long x733probablecause =0;

    String x733specificprob ="";

    String x733corrnotif ="";

    String servername ="";

    Long serverserial =0;

    String url ="";

    String connectorname ="";

    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;


    static constraints={
    identifier(blank:true,nullable:true)

     serial(nullable:true)

     node(blank:true,nullable:true)

     nodealias(blank:true,nullable:true)

     manager(blank:true,nullable:true)

     agent(blank:true,nullable:true)

     alertgroup(blank:true,nullable:true)

     alertkey(blank:true,nullable:true)

     severity(nullable:true)

     summary(blank:true,nullable:true)

     statechange(nullable:true)

     firstoccurrence(nullable:true)

     lastoccurrence(nullable:true)

     internallast(nullable:true)

     poll(nullable:true)

     nctype(nullable:true)

     tally(nullable:true)

     ncclass(blank:true,nullable:true)

     grade(nullable:true)

     location(blank:true,nullable:true)

     owneruid(blank:true,nullable:true)

     ownergid(blank:true,nullable:true)

     acknowledged(blank:true,nullable:true)

     flash(nullable:true)

     eventid(blank:true,nullable:true)

     expiretime(nullable:true)

     processreq(nullable:true)

     suppressescl(nullable:true)

     customer(blank:true,nullable:true)

     service(blank:true,nullable:true)

     physicalslot(nullable:true)

     physicalport(nullable:true)

     physicalcard(blank:true,nullable:true)

     tasklist(nullable:true)

     nmosserial(blank:true,nullable:true)

     nmosobjinst(nullable:true)

     nmoscausetype(nullable:true)

     localnodealias(blank:true,nullable:true)

     localpriobj(blank:true,nullable:true)

     localsecobj(blank:true,nullable:true)

     localrootobj(blank:true,nullable:true)

     remotenodealias(blank:true,nullable:true)

     remotepriobj(blank:true,nullable:true)

     remotesecobj(blank:true,nullable:true)

     remoterootobj(blank:true,nullable:true)

     x733eventtype(nullable:true)

     x733probablecause(nullable:true)

     x733specificprob(blank:true,nullable:true)

     x733corrnotif(blank:true,nullable:true)

     servername(blank:false,nullable:false)

     serverserial(nullable:false,key:["servername"])

     url(blank:true,nullable:true)

     connectorname(blank:true,nullable:true)

     __operation_class__(nullable:true)

     errors(nullable:true)


    }
    static relations = [:]
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__"];

    public String toString()
    {
    	return "${getClass().getName()}[servername:$servername, serverserial:$serverserial]";
    }

    //AUTO_GENERATED_CODE




}
