package com.ifountain.session;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 15, 2009
 * Time: 12:11:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class Session extends HashMap
{
    private boolean isDestroyed = false;
    public boolean isDestroyed()
    {
        return isDestroyed;        
    }
    public void destroy()
    {
        isDestroyed = true;
    }
}