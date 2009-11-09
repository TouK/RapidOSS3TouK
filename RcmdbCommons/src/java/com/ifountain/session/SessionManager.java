package com.ifountain.session;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 15, 2009
 * Time: 10:05:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class SessionManager
{
    public static final String SESSION_USERNAME_KEY = "username";
    private SessionStorage<Session> storage = new SessionStorage<Session>();
    private List<SessionListener> listeners = new ArrayList<SessionListener>();
    private static SessionManager manager;
    public synchronized static SessionManager getInstance()
    {
        if(manager == null)
        {
            manager = new SessionManager();
        }
        return manager;
    }

    public static void destroyInstance()
    {
        manager = null;
    }

    public synchronized void addSessionListener(SessionListener listener)
    {
        if(!listeners.contains(listener))
        {
            listeners.add(listener);
        }
    }

    public synchronized void removeSessionListener(SessionListener listener)
    {
        listeners.remove(listener);
    }

    public synchronized void removeAllListeners()
    {
        listeners.clear();
    }

    

    public Session getSession()
    {
        Session session = storage.get();
        if(session == null)
        {
            session = startSession(null);

        }
        return session;
    }
    
    public Session startSession(String username)
    {
        Session session = storage.get();
        if(session != null)
        {
            if(String.valueOf(session.get(SESSION_USERNAME_KEY)).equals(String.valueOf(username)))
            {
                return session;                
            }else{
                endSession();
            }
        }
        session = new Session();
        session.put(SESSION_USERNAME_KEY, username);
        storage.set(session);
        for(Iterator<SessionListener> listenerIterator = listeners.iterator();listenerIterator.hasNext();)
        {
            SessionListener listener = listenerIterator.next();
            listener.sessionStarted(session);
        }
        return session;
    }

    public void endSession()
    {
        Session currentSession = storage.get();
        if(currentSession != null)
        {
            currentSession.destroy();
            storage.set(null);
            for(Iterator<SessionListener> listenerIterator = listeners.iterator();listenerIterator.hasNext();)
            {
                SessionListener listener = listenerIterator.next();
                listener.sessionEnded(currentSession);
            }
        }
    }
}

class SessionStorage<T> extends InheritableThreadLocal<T>
{
}


