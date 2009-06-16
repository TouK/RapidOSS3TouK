package com.ifountain.rcmdb.aol;

/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jun 11, 2009
 * Time: 3:11:36 PM
 */

import net.kano.joscar.ByteBlock;
import net.kano.joscar.flap.ClientFlapConn;
import net.kano.joscar.flap.FlapPacketEvent;
import net.kano.joscar.flapcmd.LoginFlapCmd;
import net.kano.joscar.flapcmd.SnacCommand;
import net.kano.joscar.net.ClientConnEvent;
import net.kano.joscar.snac.SnacPacketEvent;
import net.kano.joscar.snac.SnacResponseEvent;
import net.kano.joscar.snaccmd.auth.AuthRequest;
import net.kano.joscar.snaccmd.auth.AuthResponse;
import net.kano.joscar.snaccmd.auth.ClientVersionInfo;
import net.kano.joscar.snaccmd.auth.KeyRequest;
import net.kano.joscar.snaccmd.auth.KeyResponse;

public class LoginConn extends AbstractFlapConn {
    protected boolean loggedin = false;

    public LoginConn(String host, int port, IAolConnection aolConn) {
        super(host, port, aolConn);
    }

    protected void handleStateChange(ClientConnEvent e) {
        aolConnection.getLogger().debug("login connection state is now " + e.getNewState()
                + ": " + e.getReason());
        if (e.getNewState() == ClientFlapConn.STATE_CONNECTED) {
            aolConnection.getLogger().debug("sending flap version and key request");
            getFlapProcessor().sendFlap(new LoginFlapCmd());
            request(new KeyRequest(aolConnection.getScreenname()));
        } else if (e.getNewState() == ClientFlapConn.STATE_FAILED) {
            aolConnection.disconnectDetected(String.valueOf(e.getReason()));
        }
//        else if (e.getNewState() == ClientFlapConn.STATE_NOT_CONNECTED) {
//            if (!(loggedin && ClientFlapConn.REASON_ON_PURPOSE.equals(e.getReason()))) {
//                aolConnection.disconnectDetected(String.valueOf(e.getReason()));
//            }
//        }
    }

    protected void handleFlapPacket(FlapPacketEvent e) {
    }

    protected void handleSnacPacket(SnacPacketEvent e) {
    }

    protected void handleSnacResponse(SnacResponseEvent e) {
        SnacCommand cmd = e.getSnacCommand();
        aolConnection.getLogger().debug("login conn got command "
                + Integer.toHexString(cmd.getFamily()) + "/"
                + Integer.toHexString(cmd.getCommand()) + ": " + cmd);

        if (cmd instanceof KeyResponse) {
            KeyResponse kr = (KeyResponse) cmd;

            ByteBlock authkey = kr.getKey();
            ClientVersionInfo version = new ClientVersionInfo(
                    "AOL Instant Messenger, version 5.2.3292/WIN32",
                    5, 1, 0, 3292, 238);

            request(new AuthRequest(
                    aolConnection.getScreenname(), aolConnection.getPassword(),
                    version, authkey));

        } else if (cmd instanceof AuthResponse) {
            AuthResponse ar = (AuthResponse) cmd;

            int error = ar.getErrorCode();
            if (error != -1) {
                aolConnection.getLogger().warn("connection error! code: " + error);
                if (ar.getErrorUrl() != null) {
                    aolConnection.getLogger().warn("Error URL: " + ar.getErrorUrl());
                }
                aolConnection.disconnectDetected("Login error. Code: " + error + ", URL: " + ar.getErrorUrl());
            } else {
                loggedin = true;
                aolConnection.setScreenname(ar.getScreenname());
                aolConnection.startBosConn(ar.getServer(), ar.getPort(), ar.getCookie());
                aolConnection.getLogger().info("Logged in to server " + ar.getServer() + ":" + ar.getPort());
            }

            disconnect();
        }
    }
}
