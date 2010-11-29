package com.ifountain.es.mapping;

import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 1:27:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EsMappingProvider {
    public Map<String, TypeMapping> constructMappings ()throws MappingException;
    public Map<String, TypeMapping> reload()throws MappingException;
}
