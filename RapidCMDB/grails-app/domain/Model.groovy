class Model {
    String name;
    Model parentModel;
    static transients = ['generated','modelFile','controllerFile','controllerGenerated']
    static hasMany = [modelProperties:ModelProperty, datasources:ModelDatasource, relations:ModelRelation];
    static mappedBy = [relations:'fromModel']
    static constraints = {
        name(blank:false, unique:true);
        parentModel(nullable:true);
    }


    public boolean isGenerated()
    {
        return getModelFile().exists();
    }
    public boolean isControllerGenerated()
    {
        return getControllerFile().exists();
    }

    def getModelFile()
    {
        return new File("grails-app/domain/${name}.groovy");
    }
    def getControllerFile()
    {
        return new File("grails-app/controllers/${name}Controller.groovy");
    }
        
    String toString(){
        return "$name";
    }

}
