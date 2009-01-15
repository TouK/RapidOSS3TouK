package com.ifountain.session;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 15, 2009
 * Time: 2:08:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SessionListener {
    public void sessionStarted(Session session);
    public void sessionEnded(Session session);
}
