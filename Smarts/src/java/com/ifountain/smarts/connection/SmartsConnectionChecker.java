package com.ifountain.smarts.connection;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Nov 18, 2008
 * Time: 3:48:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmartsConnectionChecker extends Thread{
    private SmartsConnectionImpl connection;
    private boolean stopChecker=false;
    private String logPrefix="[SmartsConnectionChecker] : ";
    private Logger logger;
    public SmartsConnectionChecker(SmartsConnectionImpl con,Logger logger)
    {
        connection=con;
        this.logger=logger;
    }
    public void run()
    {
         logger.debug(logPrefix+"starting SmartsConnectionChecker");
         while(connection.checkConnection() && !stopChecker)
         {
            logger.debug(logPrefix+"Checked in SmartsConnectionChecker");
            try{                
                Thread.sleep(1000);
            }
            catch (InterruptedException exceptionWillBeIgnored) {
            }
         }
         logger.debug(logPrefix+"Loop ended in SmartsConnectionChecker");
         if(!stopChecker)
         {
             try {
                 logger.info(logPrefix+"Gonna disconnect connection");
                 connection._disconnect();
             }
             catch (RuntimeException exceptionWillBeIgnored) {
             }
         }

    }
    public void stopChecker()
    {
       stopChecker=true;
    }
    
}
