package connection

import com.ifountain.core.connection.BaseConnection
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException


import javax.mail.Session;
import com.sun.mail.smtp.SMTPTransport;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 23, 2008
 * Time: 5:03:56 PM
 * To change this template use File | Settings | File Templates.
 */
class EmailConnectionImpl extends BaseConnection{

    public static final String SMTPHOST = "SmtpHost";
    public static final String SMTPPORT = "SmtpPort";
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    public static final String PROTOCOL = "Protocol";

    private String smtpHost;
    private int smtpPort ;
    private String username ;
    private String password ;
    private String protocol ;

    private Session session;
    private SMTPTransport transport;
    public EmailConnectionImpl()
    {

    }

    public boolean isConnectionException(Throwable t)
    {
        return false;
    }
    

    protected void connect() throws Exception {

        String smtpProtocol = protocol;
        Properties props = new Properties();
        props.put("mail."+smtpProtocol+".user", username);
        props.put("mail."+smtpProtocol+".host", smtpHost);
        props.put("mail."+smtpProtocol+".port", "" + smtpPort);
        props.put("mail." + smtpProtocol + ".timeout", "30000");
        props.put("mail." + smtpProtocol + ".auth", "true");
        session = Session.getInstance(props);
        transport = (SMTPTransport) session.getTransport(smtpProtocol);
        transport.setStartTLS(true);
        transport.connect(smtpHost, username, password);

    }

    protected void disconnect() {
        if(transport!=null)
        {
            transport.close();
        }
        transport=null;
    }
    public void init(ConnectionParam param) throws Exception{
        super.init(param)
        this.smtpHost = checkParam(SMTPHOST);
        this.smtpPort = Integer.valueOf(checkParam(SMTPPORT));
        this.username = checkParam(USERNAME);
        this.password = checkParam(PASSWORD);
        this.protocol = checkParam(PROTOCOL).toLowerCase();
    }

    public boolean checkConnection() {
        boolean result=false;
        if(transport != null)
        {
            try
            {
               if(transport.isConnected())
               {
                   if(transport.ehlo())
                   {
                     result=true;
                   }
               }
            }
            catch (javax.mail.MessagingException e)
            {
                Logger errorLogger=Logger.getRootLogger();
                if(errorLogger.isDebugEnabled())
                {
                    errorLogger.debug("[EmailConnectionImpl]: Disconnect detected during checkConnection. Reason :"+e.toString());
                }

                result=false;
            }
        }
        return result;

        
    }
    
     protected String checkParam(String parameterName) throws UndefinedConnectionParameterException {
        if(!params.getOtherParams().containsKey(parameterName)){
            throw new UndefinedConnectionParameterException(parameterName);
        }
        return (String) params.getOtherParams().get(parameterName);
    }

    public SMTPTransport getEmailConnection(){
        return transport;
    }
    public Session getEmailSession(){
        return session;
    }
    //for testing
    protected int getSmtpPort(){
        return smtpPort;
    }
    
}
