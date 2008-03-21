class ModelProperty {
    String name;
    String type;
    boolean blank = true;
    String defaultValue;
    String datasourceName;
    ModelProperty propertySpecifyingDatasource;
    String nameInDatasource;
    Model model;
    boolean lazy = true;

    static belongsTo = Model;

    static constraints = {
        name(blank:false);
        datasourceName(blank:true,nullable:true);
        propertySpecifyingDatasource(nullable:true);
        type(inList:["string", "number", "date"]);
    }

    String toString(){
        return "$name";
    }
}
