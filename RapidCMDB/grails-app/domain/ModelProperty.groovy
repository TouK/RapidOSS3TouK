class ModelProperty {
    String name;
    String type;
    boolean blank = true;
    String defaultValue;
    String modelDatasourceId;
    ModelProperty propertySpecifyingDatasource;
    String nameInDatasource;
    Model model;
    boolean lazy = true;

    static belongsTo = Model;

    static constraints = {
        name(blank:false, unique:'model');
        modelDatasourceId(blank:true,nullable:true);
        propertySpecifyingDatasource(nullable:true);
        type(inList:["string", "number", "date"]);
    }

    static optionals = ["datasourceName", "propertySpecifyingDatasource"]

    String toString(){
        return "$name";
    }
}
