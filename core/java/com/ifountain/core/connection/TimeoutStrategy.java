package com.ifountain.core.connection;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 1, 2008
 * Time: 8:58:31 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TimeoutStrategy
{
    abstract public boolean shouldRecalculate(List<IConnection> connections);
    abstract public long calculateNewTimeout(long oldTimeout, List<IConnection> connections);
}
