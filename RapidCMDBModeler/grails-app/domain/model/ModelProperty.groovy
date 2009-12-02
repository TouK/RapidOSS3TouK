/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package model

import com.ifountain.rcmdb.converter.RapidConvertUtils
import com.ifountain.rcmdb.util.RapidCMDBConstants
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ModelProperty {
    static searchable = {
        except=["model", "mappedKeys", "propertyDatasource", "propertySpecifyingDatasource", "errors", "__operation_class__", "__dynamic_property_storage__"]
    };
    def static final String stringType = "string";
    def static final String numberType = "number";
    def static final String dateType = "date";
    def static final String floatType = "float";
    def static final String booleanType = "boolean";
    String name;
    String rsOwner = "p"
    String type;
    Long rsInsertedAt =0;
    Long rsUpdatedAt =0;
    boolean blank = true;
    String defaultValue;
    ModelDatasource propertyDatasource;
    ModelProperty propertySpecifyingDatasource;
    String nameInDatasource;
    Model model;
    List mappedKeys = [];
    boolean lazy = true;
    org.springframework.validation.Errors errors ;
    Object __operation_class__;
    Object __dynamic_property_storage__;
    static relations = [
            propertySpecifyingDatasource:[type:ModelProperty,  isMany:false],
            propertyDatasource:[type:ModelDatasource, isMany:false],
            model:[type:Model, reverseName:"modelProperties", isMany:false],
            mappedKeys:[type:ModelDatasourceKeyMapping, reverseName:"property", isMany:true],
    ]
    static transients = ["errors", "__operation_class__","__dynamic_property_storage__"]
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
                else if(obj.type == booleanType)
                {
                    def converter = RapidConvertUtils.getInstance().lookup (Boolean.class);
                    try
                    {
                        converter.convert(Boolean.class, val);
                    }
                    catch(org.apache.commons.beanutils.ConversionException e)
                    {
                        return ['modelproperty.defaultvalue.invalidboolean']
                    }
                }
            }

        });
        errors(nullable:true)
        __operation_class__(nullable:true)
        __dynamic_property_storage__(nullable:true)
        propertySpecifyingDatasource(nullable:true);
        type(inList:[stringType, numberType, dateType, floatType, booleanType]);
        lazy(validator:{val, obj ->
            if(val && obj.propertyDatasource != null && obj.propertyDatasource.datasource.name == RapidCMDBConstants.RCMDB){
                return ["model.invalid.lazy"]       
            }
        })

        blank(validator:{val, obj ->
             if(val){
                 def isValid = true;
                 def props = ModelProperty.search("name:${obj.name.exactQuery()}").results[0];
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
    String toString(){
        return "$name";
    }
}
