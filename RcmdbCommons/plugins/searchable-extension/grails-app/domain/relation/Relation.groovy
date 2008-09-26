package relation
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 1, 2008
 * Time: 2:07:08 PM
 * To change this template use File | Settings | File Templates.
 */
class Relation {
    static searchable = true;
    Long id;
    Long version;
    String rsOwner = "p"
    Long objectId;
    String name;
    String className;
    Map relatedObjectIds = [:]
    static relations = [:]
    static constraints = {
        objectId(key:["name"]);
    }

    public String toString() {
        return "objId:${objectId} name:${name}"
    }

    public static String getRelKey(id)
    {
        return "relatedObjId${id}".toString();   
    }


}