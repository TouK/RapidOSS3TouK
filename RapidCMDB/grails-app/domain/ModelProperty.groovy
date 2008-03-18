class ModelProperty {
    String name;
    String type;
    boolean blank = true;
    String defaultValue;
    String datasourceName;
    String nameInDatasource;
    Model model;
    boolean lazy = true;

    static belongsTo = Model;

    static constraints = {
        name(blank:false);
        type(inList:["string", "number", "date"]);        
    }

    String toString(){
        return "$name";
    }
}
