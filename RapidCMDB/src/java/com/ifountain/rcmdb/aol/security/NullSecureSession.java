package com.ifountain.rcmdb.aol.security;

import net.kano.joscar.ByteBlock;
import net.kano.joscar.snaccmd.FullRoomInfo;

import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jun 11, 2009
 * Time: 2:30:55 PM
 */
public class NullSecureSession extends SecureSession {
    NullSecureSession() {
    }

    public X509Certificate getMyCertificate() {
        return null;
    }

    public void setCert(String sn, X509Certificate cert) {
    }

    public X509Certificate getCert(String sn) {
        return null;
    }

    public boolean hasCert(String sn) {
        return false;
    }

    public void setChatKey(String roomName, SecretKey chatKey) {
    }

    public SecretKey getChatKey(String chat) {
        return null;
    }

    public ByteBlock genChatSecurityInfo(FullRoomInfo chatInfo, String sn)
            throws SecureSessionException {
        return null;
    }

    public ByteBlock encryptIM(String sn, String msg)
            throws SecureSessionException {
        return null;
    }

    public String parseChatMessage(String chat, String sn, ByteBlock data)
            throws SecureSessionException {
        return null;
    }

    public SecretKey extractChatKey(String sn, ByteBlock data)
            throws SecureSessionException {
        return null;
    }

    public String decodeEncryptedIM(String sn, ByteBlock encData)
            throws SecureSessionException {
        return null;
    }

    public byte[] encryptChatMsg(String chat, String msg)
            throws SecureSessionException {
        return new byte[0];
    }

    public ServerSocket createSSLServerSocket(String sn)
            throws SecureSessionException {
        return null;
    }

    public Socket createSecureSocket(InetAddress address, int port)
            throws SecureSessionException {
        return null;
    }

    public void generateKey(String chat) throws SecureSessionException {
    }

}
