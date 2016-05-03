package com.minhdtb.storm.core.data;

import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.lib.j60870.*;
import com.minhdtb.storm.entities.Channel;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class StormChannelIECServer extends StormChannelIEC {

    private ServerSap serverSap;

    private List<Connection> connections = new ArrayList<>();

    public StormChannelIECServer() {
        super();
        getRaw().setType(Channel.ChannelType.CT_IEC_SERVER);
    }

    public StormChannelIECServer(Channel channel) {
        super(channel);
    }

    @Override
    public void start() {
        try {
            serverSap = new ServerSap(getPort(), 0, InetAddress.getByName(getHost()),
                    ServerSocketFactory.getDefault(), new ServerListener());
            try {
                serverSap.startListening();
            } catch (IOException e) {
                Utils.writeLog(e.getMessage());
            }
        } catch (UnknownHostException e) {
            Utils.writeLog(e.getMessage());
        }
    }

    @Override
    public void stop() {
        serverSap.stop();
    }

    private final class ServerListener implements ServerSapListener {

        @Override
        public void connectionIndication(Connection connection) {
            try {
                connections.add(connection);
                connection.waitForStartDT(new ConnectionListener(), 5000);
            } catch (IOException | TimeoutException e) {
                connections.remove(connection);
                Utils.writeLog(e.getMessage());
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

    @Override
    public void send(ASdu aSdu) {
        connections.stream().forEach(connection -> {
            try {
                connection.send(aSdu);
            } catch (IOException e) {
                Utils.writeLog(e.getMessage());
            }
        });
    }
}
