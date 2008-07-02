package model

import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.ifountain.rcmdb.util.RapidCMDBConstants

class ModelProperty {
    static searchable = true;
    def static final String stringType = "string";
    def static final String numberType = "number";
    def static final String dateType = "date";
    def static final String floatType = "float";
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
    static hasMany = [mappedKeys:ModelDatasourceKeyMapping]
    static mappedBy = [model:'modelProperties', mappedKeys:"property"]
    static constraints = {
        name(blank:false, key:['model'], validator:{val, obj ->
            if(!val.matches(ConfigurationHolder.config.toProperties()["rapidcmdb.property.validname"])){
                return ['modelproperty.name.not.match', obj.model.name];
            }
            def invalidNames = ConfigurationHolder.config.flatten().get("rapidcmdb.invalid.names")
            if(invalidNames.contains(val.toLowerCase()))
            {
                return ['modelproperty.name.invalid'];
            }
        });
        nameInDatasource(nullable:true);
        propertyDatasource(nullable:true);
        defaultValue(nullable:true, validator:{val, obj ->
            if(val)
            {
                if(obj.type == numberType)
                {
                    def converter = RapidConvertUtils.getInstance().lookup (Long.class);
                    try
                    {
                        converter.convert(Long.class, val);
                    }
                    catch(org.apache.commons.beanutils.ConversionException e)
                    {
                        return ['modelproperty.defaultvalue.invalidnumber']
                    }
                }
                if(obj.type == floatType)
                {
                    def converter = RapidConvertUtils.getInstance().lookup (Double.class);
                    try
                    {
                        converter.convert(Double.class, val);
                    }
                    catch(org.apache.commons.beanutils.ConversionException e)
                    {
                        return ['modelproperty.defaultvalue.invalidfloat']
                    }
                }
                else if(obj.type == dateType)
                {
                    def converter = RapidConvertUtils.getInstance().lookup (Date.class);
                    try
                    {
                        converter.convert(Date.class, val);
                    }
                    catch(org.apache.commons.beanutils.ConversionException e)
                    {
                        return ['modelproperty.defaultvalue.invaliddate', converter.format]
                    }
                } 
            }

        });
        propertySpecifyingDatasource(nullable:true);
        type(inList:[stringType, numberType, dateType, floatType]);
        lazy(validator:{val, obj ->
            if(val && obj.propertyDatasource != null && obj.propertyDatasource.datasource.name == RapidCMDBConstants.RCMDB){
                return ["model.invalid.lazy"]       
            }
        })

        blank(validator:{val, obj ->
             if(val){
                 def isValid = true;
                 def props = ModelProperty.findByName(obj.name);
                 ModelProperty existingProp = null;
                 props.each{
                     if(it.model.id == obj.model.id)
                     {
                        existingProp = it;
                         return;
                     }
                 }
                 if(existingProp)
                 {
                     existingProp.mappedKeys.each{
                         if(it.datasource.datasource.name == RapidCMDBConstants.RCMDB){
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
        else if(type == floatType)
        {
            return "Double";
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
