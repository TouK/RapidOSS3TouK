package snmp

import org.snmp4j.smi.OID
import org.snmp4j.mp.SnmpConstants

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Apr 1, 2009
* Time: 3:27:08 PM
*/
class SnmpUtils {
    public static final String DEFAULT_COMMUNITY = "public";
    public static final int DEFAULT_TIMEOUT = 1000;
    public static final int DEFAULT_RETRIES = 1;
    public static final int DEFAULT_MAX_REPETITIONS = 10;
    public static final int DEFAULT_NON_REPEATERS = 0;
    public static final int DEFAULT_MAX_SIZE_RESPONSE_PDU = 65535;
    public static final OID DEFULT_SNMP_TRAP_OID = SnmpConstants.coldStart;
    public static final int VERSION_1 = SnmpConstants.version1;
    public static final int VERSION_2c = SnmpConstants.version2c;

    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = ["RCMDB": ["keys": ["id": ["nameInDs": "id"]]]]
    Long rsInsertedAt =0;
    Long rsUpdatedAt =0;
    String rsOwner = "p"
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __dynamic_property_storage__;

    static constraints = {
        __operation_class__(nullable: true)
        __dynamic_property_storage__(nullable: true)
        errors(nullable: true)
    };

    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
}