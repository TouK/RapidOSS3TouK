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
    private static Map uncommittedTransactions = new HashMap();
    private static Timer timer = new Timer();
    private static boolean timeout = false;

    static TransactionListener listenerInstance = new TransactionListener()
    {
        public void transactionStarted(RapidCompassTransaction tr) {
            if(maxNumberOfTrs > 0)
            {
                startTimer();
                uncommittedTransactions.put(tr.id, tr);
                numberOfExecutedTrs++;
            }
        }

        public void transactionCommitted(RapidCompassTransaction tr)
        {
            if(maxNumberOfTrs > 0)
            {
                System.out.println(tr.id);
                uncommittedTransactions.remove(tr.id);
                System.out.println(uncommittedTransactions.isEmpty());
                System.out.println(uncommittedTransactions);
            }
            SingleCompassSessionManager.closeSession(tr.getSession());
        }

        public void transactionRolledback(RapidCompassTransaction tr)
        {
            if(maxNumberOfTrs > 0)
            {
                uncommittedTransactions.remove(tr.id);
            }
            SingleCompassSessionManager.closeSession(tr.getSession());
        }
    };
    public static void initialize(Compass compass)
    {
        SingleCompassSessionManager.initialize(compass, 0, 0);
    }
    public static void initialize(Compass compass, int maxNumberOfTrs, long maxWaitTime)
    {
        compassInstance = compass;
        compassSessionInstance = compassInstance.openSession();
        SingleCompassSessionManager.maxNumberOfTrs = maxNumberOfTrs;
        SingleCompassSessionManager.maxWaitTime = maxWaitTime;
    }

    private static void startTimer()
    {
        if(timer != null)
        {
            timer.purge();
        }
        timer = new Timer();
        TimerTask closeTask = new TimerTask()
        {
            public void run() {
                timeout = true;
                System.out.println("timeout");
                SingleCompassSessionManager.closeSession(SingleCompassSessionManager.compassSessionInstance);
                timeout = false;
            }
        };
        timer.schedule(closeTask, maxWaitTime);
    }

    private static synchronized void closeSession(CompassSession session)
    {
        if(maxNumberOfTrs <= 0 || maxNumberOfTrs >0 && numberOfExecutedTrs >= maxNumberOfTrs && uncommittedTransactions.isEmpty() || timeout && uncommittedTransactions.isEmpty())
        {
            System.out.println("asdasd");
            session.close();
        }
        else if(timeout && !uncommittedTransactions.isEmpty())
        {
            startTimer();
        }
    }

    public static synchronized CompassTransaction beginTransaction()
    {
        if(maxNumberOfTrs > 0)
        {
            if(compassSessionInstance.isClosed())
            {
                compassSessionInstance =  compassInstance.openSession();
            }
            return new RapidCompassTransaction(compassSessionInstance.beginTransaction(), listenerInstance);
        }
        else
        {
            return new RapidCompassTransaction(compassInstance.openSession().beginTransaction(), listenerInstance);
        }

    }

    public static List getUncommittedTransactions()
    {
        return new ArrayList(uncommittedTransactions.values());        
    }

    public static void destroy()
    {
        uncommittedTransactions.clear();       
    }
}
