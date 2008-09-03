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
    Long objectId;
    Long reverseObjectId;
    String name;
    String reverseName;
    static mappedBy = [:]
    static constraints = {
        objectId(key:["reverseObjectId", "name", "reverseName"]);
        reverseName(nullable:true);
    }

    public String toString() {
        return "objId:${objectId} reverserObjectId:${reverseObjectId} name:${name} reverseName:${reverseName}"
    }


}