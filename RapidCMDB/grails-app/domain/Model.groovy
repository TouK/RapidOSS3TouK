class Model {
    String name;
    Model parentModel;
    static hasMany = [modelProperties:ModelProperty, datasources:ModelDatasource];

    static constraints = {
        name(blank:false, unique:true);
        parentModel(nullable:true);
    }             
        
    String toString(){
        return "$name";
    }
}
