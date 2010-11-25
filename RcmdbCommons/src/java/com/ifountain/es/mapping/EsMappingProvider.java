package com.ifountain.es.mapping;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 25, 2010
 * Time: 1:27:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EsMappingProvider {
    public List<TypeMapping> constructMappings();
    public void reload();
}
