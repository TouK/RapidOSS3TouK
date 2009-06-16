package com.ifountain.rcmdb.aol;

import org.apache.log4j.Logger;
import com.ifountain.rcmdb.aol.security.SecureSession;
import net.kano.joscar.snac.SnacRequest;
import net.kano.joscar.ByteBlock;

/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jun 11, 2009
 * Time: 2:20:57 PM
 */
public interface IAolConnection {
    public Logger getLogger();

    public void registerSnacFamilies(BasicConn conn);

    public void unRegisterSnacFamilies(BasicConn conn);

    public SecureSession getSecureSession();

    public void sendIM(String target, String text) throws InterruptedException;

    public void messageRecieved(String target, String text);

    public void clientReady();

    public void handleRequest(SnacRequest request) throws InterruptedException;

    public void disconnectDetected(String reason);

    public void messageSentSuccessfully();

    public void messageFailed(String reason);

    public String getScreenname();

    public String getPassword();

    public void setScreenname(String screenname);

    public void startBosConn(String server, int port, ByteBlock cookie);

    public void serverAlive();
}
