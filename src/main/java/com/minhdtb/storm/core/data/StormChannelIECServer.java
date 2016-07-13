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
import java.util.Optional;
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
                Utils.error(e);
            }
        } catch (UnknownHostException e) {
            Utils.error(e);
        }
    }

    @Override
    public void stop() {
        connections.forEach(Connection::close);
        connections.clear();

        serverSap.stop();
    }

    private final class ServerListener implements ServerSapListener {

        @Override
        public void connectionIndication(Connection connection) {
            try {
                connections.add(connection);
                connection.waitForStartDT(new ConnectionListener(), TIMEOUT);
            } catch (IOException | TimeoutException e) {
                connections.remove(connection);
                Utils.error(e);
            }
        }

        @Override
        public void serverStoppedListeningIndication(IOException e) {
            Utils.error(e);
        }

        @Override
        public void connectionAttemptFailed(IOException e) {
            Utils.error(e);
        }
    }

    private final class ConnectionListener implements ConnectionEventListener {

        @Override
        public void newASdu(ASdu aSdu) {
            Optional<IStormVariable> found = getVariables().stream().filter(variable -> variable instanceof StormVariableIEC &&
                    (((StormVariableIEC) variable).getSectorAddress() == aSdu.getCommonAddress() &&
                            ((StormVariableIEC) variable).getInformationObjectAddress() ==
                                    aSdu.getInformationObjects()[0].getInformationObjectAddress())).findFirst();

            if (found.isPresent()) {
                Object value = Utils.ASduToObject(aSdu);
                if (value != null) {
                    found.get().setValue(value);
                }
            }
        }

        @Override
        public void connectionClosed(IOException e) {
            Utils.error(e);
        }
    }

    @Override
    public void send(ASdu aSdu) {
        connections.forEach(connection -> {
            try {
                if (connection.isConnected()) {
                    connection.send(aSdu);
                } else {
                    connections.remove(connection);
                }
            } catch (IOException e) {
                connections.remove(connection);
                Utils.error(e);
            }
        });
    }
}
