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

public class SendEmailAction implements Action {

    private Logger logger;
    private int type;
    private Map params;

    public SendEmailAction(Logger logger,  Map params) {
        this.logger = logger;
        this.params = params;
    }

    public void execute(IConnection conn) throws Exception {

        logger.debug("Sending email with params:\n" + params);

        SMTPTransport emailConnection=conn.getEmailConnection();
        
        Message m = new MimeMessage((Session)conn.getEmailSession());

        String from=params.from;
        m.setFrom(new InternetAddress(from,from));

        m.setSubject(params.subject);
        m.addRecipient(Message.RecipientType.TO, new InternetAddress(params.to));
        m.setContent(params.body, "text/plain");
        m.setSentDate(new Date());
        m.saveChanges();

        emailConnection.sendMessage(m, m.getAllRecipients());
            
    }


}

