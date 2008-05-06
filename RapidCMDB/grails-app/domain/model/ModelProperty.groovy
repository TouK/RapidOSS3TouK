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
    boolean lazy = true;

    static belongsTo = Model;

    static constraints = {
        name(blank:false, unique:'model', validator:{val, obj ->
            def firstChar = val.charAt(0);
            if(!(firstChar >= 'a' && firstChar <= 'z')){
                return ['modelproperty.name.uppercased', obj.model.name];
            }
        });
        nameInDatasource(nullable:true);
        propertyDatasource(nullable:true);
        defaultValue(nullable:true);
        propertySpecifyingDatasource(nullable:true);
        type(inList:[stringType, numberType, dateType]);
        lazy(validator:{val, obj ->
            if(val && obj.propertyDatasource != null && obj.propertyDatasource.master){
                return ["model.invalid.lazy"]       
            }
        })

        blank(validator:{val, obj ->
             if(val){
                 def isValid = true;
                 if(ModelProperty.findByNameAndModel(obj.name, obj.model))
                 {
                     ModelDatasourceKeyMapping.findAllByProperty(obj).each{
                         if(it.datasource.master){
                             isValid = false;
                         }
                     }
                 }
                 if(!isValid){
                     return ['model.keymapping.masterproperty.notblank']
                 }
             }
        })
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
    
    def xml(){
       	def property = {
        		property{
    			name(name)
    			type(type)
    			blank(blank)
    			defaultValue(defaultValue)
    			def datasourceName = null;
    			if(propertyDatasource != null) datasourceName = propertyDatasource.datasource.name;
    			datasource(datasourceName)			
    			def propertyNameSpecifyingDatasource = null;
    			if(propertySpecifyingDatasource != null) propertyNameSpecifyingDatasource = propertySpecifyingDatasource.name;
    			propertySpecifyingDatasource(propertyNameSpecifyingDatasource)
    			nameInDatasource(nameInDatasource)
    			lazy(lazy)
    		}
    	}
    	
    	return property;
    }    
}
