package com.ifountain.rcmdb.aol;

import javax.net.SocketFactory;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jun 15, 2009
 * Time: 6:19:57 PM
 */
public class AolSocketFactory extends SocketFactory {
    private Long timeout;

    public AolSocketFactory(Long t) {
        timeout = t;
    }

    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        Socket socket = new Socket(host, port);
        socket.setSoTimeout(timeout.intValue());
        return socket;

    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        Socket socket = new Socket(host, port, localHost, localPort);
        socket.setSoTimeout(timeout.intValue());
        return socket;
    }

    public Socket createSocket(InetAddress host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        socket.setSoTimeout(timeout.intValue());
        return socket;
    }

    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        Socket socket = new Socket(address, port, localAddress, localPort);
        socket.setSoTimeout(timeout.intValue());
        return socket;
    }
}
