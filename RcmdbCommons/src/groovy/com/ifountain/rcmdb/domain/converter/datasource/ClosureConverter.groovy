package com.ifountain.rcmdb.domain.converter.datasource
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 25, 2008
 * Time: 1:30:13 PM
 * To change this template use File | Settings | File Templates.
 */
class ClosureConverter implements Converter
{
    private Closure closure;
    public ClosureConverter(Closure closure)
    {
        this.closure = closure;        
    }
    public Object convert(Object value) {
        return closure(value);  //To change body of implemented methods use File | Settings | File Templates.
    }

}