package model

class Model {
    String name;
    Boolean generateAll = Boolean.FALSE;
    Model parentModel;
    static transients = ['generated','modelFile','controllerFile','controllerGenerated']
    static hasMany = [modelProperties:ModelProperty, datasources:ModelDatasource, fromRelations:ModelRelation, toRelations:ModelRelation];
    static mappedBy = [fromRelations:'firstModel', toRelations:'secondModel']     
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
        return new File(System.getProperty("base.dir", ".")+"/grails-app/domain/${name}.groovy");
    }
    def getControllerFile()
    {
        return new File(System.getProperty("base.dir", ".")+"/grails-app/controllers/${name}Controller.groovy");
    }
        
    String toString(){
        return "$name";
    }

}
