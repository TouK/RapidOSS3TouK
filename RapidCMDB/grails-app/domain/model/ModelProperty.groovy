package model

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.apache.commons.beanutils.ConvertUtils
import com.ifountain.rcmdb.domain.converter.DateConverter;
class ModelProperty {
    def static final String stringType = "string";
    def static final String numberType = "number";
    def static final String dateType = "date";
    String name;
    String type;
    boolean blank = true;
    String defaultValue = "";
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
            def invalidNames = ConfigurationHolder.config.flatten().get("rapidcmdb.invalid.names")
            if(invalidNames.contains(val.toLowerCase()))
            {
                return ['modelproperty.name.invalid'];
            }
        });
        nameInDatasource(nullable:true);
        propertyDatasource(nullable:true);
        defaultValue(validator:{val, obj ->
            if(val)
            {
                if(obj.type == numberType)
                {
                    def converter = ConvertUtils.lookup (Long.class);
                    try
                    {
                        converter.convert(Long.class, val);
                    }
                    catch(org.apache.commons.beanutils.ConversionException e)
                    {
                        return ['modelproperty.defaultvalue.invalidnumber']
                    }
                }
                else if(obj.type == dateType)
                {
                    def converter = ConvertUtils.lookup (Date.class);
                    try
                    {
                        converter.convert(Date.class, val);
                    }
                    catch(org.apache.commons.beanutils.ConversionException e)
                    {
                        return ['modelproperty.defaultvalue.invaliddate', ((DateConverter)ConvertUtils.lookup(Date.class)).format]
                    }
                } 
            }

        });
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
