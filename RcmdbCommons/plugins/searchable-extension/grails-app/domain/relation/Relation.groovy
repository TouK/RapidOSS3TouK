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
    Long reverseObjectId;
    String name = "";
    String reverseName = "";
    static relations = [:]
    static constraints = {
        objectId(key:["name", "reverseObjectId", "reverseName"]);
    }

    public String toString() {
        return "objectId:${objectId} reverseObjectId:${reverseObjectId} name:${name} reverseName:${reverseName}";
    }

}