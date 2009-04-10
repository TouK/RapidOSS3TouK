package datasource
/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
 * noted in a separate copyright notice. All rights reserved.
 * This file is part of RapidCMDB.
 *
 * RapidCMDB is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */
/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 24, 2008
 * Time: 4:56:37 PM
 * To change this template use File | Settings | File Templates.
 */

import com.ifountain.core.connection.IConnection
import com.ifountain.core.datasource.Action
import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.internet.InternetAddress
import com.sun.mail.smtp.SMTPTransport;

import com.ifountain.comp.exception.RapidMissingParameterException


public class SendEmailAction implements Action {
    public static final String FROM_PARAM_NAME = "from";
    public static final String TO_PARAM_NAME = "to";
    public static final String SUBJECT_PARAM_NAME = "subject";
    public static final String BODY_PARAM_NAME = "body";
    public static final String CONTENT_TYPE_PARAM_NAME = "contentType";
    public static final List PARAMS_TO_BE_CHECKED = [FROM_PARAM_NAME, TO_PARAM_NAME, SUBJECT_PARAM_NAME, BODY_PARAM_NAME];
    private Logger logger;
    private int type;
    private Map params;


    public SendEmailAction(Logger logger,  Map params) {
        this.logger = logger;
        this.params = params;
        checkParams(PARAMS_TO_BE_CHECKED);
        if(!params.containsKey(CONTENT_TYPE_PARAM_NAME))
        {
            params[CONTENT_TYPE_PARAM_NAME]=EmailAdapter.PLAIN;
        }
    }

    public void execute(IConnection conn) throws Exception {
        if(logger.isDebugEnabled())
        {
            logger.debug("Sending email with params:\n" + params);
        }

        SMTPTransport emailConnection=conn.getEmailConnection();
        
        Message m = new MimeMessage((Session)conn.getEmailSession());

        String from=params[FROM_PARAM_NAME];
        m.setFrom(new InternetAddress(from,from));

        m.setSubject(params[SUBJECT_PARAM_NAME]);
        m.addRecipient(Message.RecipientType.TO, new InternetAddress(params[TO_PARAM_NAME]));
        m.setContent(params[BODY_PARAM_NAME], params[CONTENT_TYPE_PARAM_NAME]);
        m.setSentDate(new Date());
        m.saveChanges();

        emailConnection.sendMessage(m, m.getAllRecipients());
            
    }

     protected String checkParams(parameterNames) throws RapidMissingParameterException {
        for(parameterName in parameterNames)
        {
            if(!params.containsKey(parameterName)){
                throw new RapidMissingParameterException("SendEmailAction.params."+parameterName);
            }            
        }
    }

    //for testing
    public Map getParams()
    {
        return params;
    }
    public Logger getLogger()
    {
        return logger;
    }

}

