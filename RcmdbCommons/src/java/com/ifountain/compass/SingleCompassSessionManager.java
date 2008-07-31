package com.ifountain.compass;

import org.compass.core.Compass;
import org.compass.core.CompassTransaction;
import org.compass.core.CompassSession;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 25, 2008
 * Time: 5:48:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleCompassSessionManager {
    private static Compass compassInstance;
    private static int maxNumberOfTrs;
    private static long maxWaitTime;
    private static int numberOfExecutedTrs;
    private static CompassSession compassSessionInstance;
    private static CompassTransaction compassTransactionInstance;
    private static Map uncommittedTransactions = new HashMap();
    private static Timer timer = new Timer();
    private static boolean timeout = false;
    private static boolean isDestroyed = true;
    private static Object sessionLock = new Object();

    static TransactionListener listenerInstance = new TransactionListener()
    {
        public void transactionStarted(RapidCompassTransaction tr) {
            if(isBatchInsertMode())
            {
                synchronized (sessionLock)
                {
                    uncommittedTransactions.put(tr.id, tr);
                    numberOfExecutedTrs++;
                }
            }
        }

        public void transactionCommitted(RapidCompassTransaction tr)
        {
            if(isBatchInsertMode())
            {
                synchronized (sessionLock)
                {
                    uncommittedTransactions.remove(tr.id);
                    SingleCompassSessionManager.closeSession(tr.getSession());
                }
            }
            else
            {
                tr.transaction.commit();
                tr.getSession().close();
            }

        }

        public void transactionRolledback(RapidCompassTransaction tr)
        {
            if(isBatchInsertMode())
            {
                synchronized (sessionLock)
                {
                    uncommittedTransactions.remove(tr.id);
                    SingleCompassSessionManager.closeSession(tr.getSession());
                }
            }
            else
            {
                tr.transaction.rollback();
                tr.getSession().close();
            }

        }
    };

    private static boolean isBatchInsertMode()
    {
        return maxNumberOfTrs > 0 || maxWaitTime > 0;
    }
    public static void initialize(Compass compass)
    {
        SingleCompassSessionManager.initialize(compass, 0, 0);
    }
    public static void initialize(Compass compass, int maxNumberOfTrs, long maxWaitTime)
    {
        isDestroyed = false;
        uncommittedTransactions.clear();
        SingleCompassSessionManager.maxNumberOfTrs = maxNumberOfTrs;
        SingleCompassSessionManager.maxWaitTime = maxWaitTime;
        compassInstance = compass;
        if(isBatchInsertMode()){
            synchronized (sessionLock)
            {
                compassSessionInstance = compassInstance.openSession();
                compassTransactionInstance = compassSessionInstance.beginTransaction();
            }
            startTimer();
        }
    }

    private static void startTimer()
    {
        if(maxWaitTime <= 0) return;
        timer = new Timer();
        TimerTask closeTask = new TimerTask()
        {
            public void run() {
                synchronized (sessionLock)
                {
                    timeout = true;
                    SingleCompassSessionManager.closeSession(SingleCompassSessionManager.compassSessionInstance);
                    timeout = false;
                }
            }
        };
        timer.schedule(closeTask, maxWaitTime, maxWaitTime);
    }

    private static void closeSession(CompassSession session)
    {
        synchronized (sessionLock)
        {
            if(maxNumberOfTrs > 0 && numberOfExecutedTrs >= maxNumberOfTrs && uncommittedTransactions.isEmpty() || numberOfExecutedTrs > 0 && timeout && uncommittedTransactions.isEmpty() || isDestroyed && !timeout && uncommittedTransactions.isEmpty())
            {
                compassTransactionInstance.commit();
                session.close();
                numberOfExecutedTrs = 0;
            }
        }
    }

    public static CompassTransaction beginTransaction()
    {
        if(isDestroyed) throw new UnInitializedSessionManagerException();
        synchronized (sessionLock)
        {
            if(isBatchInsertMode())
            {
                if(compassSessionInstance.isClosed())
                {
                    compassSessionInstance =  compassInstance.openSession();
                    compassTransactionInstance = compassSessionInstance.beginTransaction();
                }
                return new RapidCompassTransaction(compassTransactionInstance, listenerInstance);
            }
            else
            {
                return new RapidCompassTransaction(compassInstance.openSession().beginTransaction(), listenerInstance);
            }
        }

    }

    public static List getUncommittedTransactions()
    {
        return new ArrayList(uncommittedTransactions.values());        
    }

    public static void destroy()
    {
        synchronized (sessionLock)
        {
            if(timer != null)
            {
                timer.purge();
            }
            isDestroyed = true;
        }
    }
    public static boolean isClosedLastSession()
    {
        synchronized (sessionLock)
        {
            return compassSessionInstance.isClosed();
        }
    }
}
