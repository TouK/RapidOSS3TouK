package model;
class ModelProperty {
    def static final String stringType = "string";
    def static final String numberType = "number";
    def static final String dateType = "date";
    String name;
    String type;
    boolean blank = true;
    String defaultValue;
    ModelDatasource propertyDatasource;
    ModelProperty propertySpecifyingDatasource;
    String nameInDatasource;
    Model model;
    Model relatedClass;
    boolean lazy = true;

    static belongsTo = Model;

    static constraints = {
        name(blank:false, unique:'model');
        propertyDatasource(nullable:true);
        propertySpecifyingDatasource(nullable:true);
        relatedClass(nullable:true);
        type(inList:[stringType, numberType, dateType]);
    }

    def setName(String nameP)
    {
        def firstChar = nameP.substring (0,1)
        def remaining = nameP.substring (1);
        name = firstChar.toLowerCase()+remaining; 
    }


    def convertToRealType()
    {
        if(type == stringType)
        {
            return "String";
        }
        else if(type == numberType)
        {
            return "Long"
        }
        else if(type == dateType)
        {
            return "Date";
        }
        else
        {
            return "Object";
        }
    }
    String toString(){
        return "$name";
    }
}
