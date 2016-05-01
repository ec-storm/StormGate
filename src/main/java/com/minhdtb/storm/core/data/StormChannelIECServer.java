package com.minhdtb.storm.core.data;

import com.minhdtb.storm.entities.Channel;
import org.openmuc.j60870.*;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class StormChannelIECServer extends StormChannelIEC {

    public StormChannelIECServer() {
        super();
    }

    public StormChannelIECServer(Channel channel) {
        super(channel);
    }

    @Override
    public void start() {
        try {
            ServerSap serverSap = new ServerSap(getPort(), 0, InetAddress.getByName(getHost()),
                    ServerSocketFactory.getDefault(), new ServerListener());
            try {
                serverSap.startListening();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private final class ServerListener implements ServerSapListener {

        @Override
        public void connectionIndication(Connection connection) {
            try {
                connection.waitForStartDT(new ConnectionListener(), 5000);
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void serverStoppedListeningIndication(IOException e) {

        }

        @Override
        public void connectionAttemptFailed(IOException e) {

        }
    }

    private final class ConnectionListener implements ConnectionEventListener {

        @Override
        public void newASdu(ASdu aSdu) {

        }

        @Override
        public void connectionClosed(IOException e) {

        }
    }
}
