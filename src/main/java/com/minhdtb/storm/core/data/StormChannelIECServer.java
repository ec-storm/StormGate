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
        for (Connection myconnection : connections) {
            myconnection.close();
        }

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
            for (IStormVariable variable : getVariables()) {
                if (variable instanceof StormVariableIEC) {
                    if (((StormVariableIEC) variable).getSectorAddress() == aSdu.getCommonAddress() &&
                            ((StormVariableIEC) variable).getInformationObjectAddress() ==
                                    aSdu.getInformationObjects()[0].getInformationObjectAddress()) {
                        TypeId typeId = aSdu.getTypeIdentification();
                        Object value = null;
                        InformationElement element = aSdu.getInformationObjects()[0]
                                .getInformationElements()[0][0];

                        switch (typeId) {
                            case M_ME_NA_1: {
                                if (element instanceof IeNormalizedValue) {
                                    IeNormalizedValue normalizedValue = (IeNormalizedValue) element;
                                    value = normalizedValue.getValue();
                                }

                                break;
                            }
                            case M_ME_NC_1: {
                                if (element instanceof IeShortFloat) {
                                    IeShortFloat shortFloat = (IeShortFloat) element;
                                    value = shortFloat.getValue();
                                }

                                break;
                            }
                            case C_SC_NA_1: {
                                if (element instanceof IeSingleCommand) {
                                    IeSingleCommand singleCommand = (IeSingleCommand) element;
                                    value = singleCommand.isCommandStateOn();
                                }

                                break;
                            }
                            case C_DC_NA_1: {
                                if (element instanceof IeDoubleCommand) {
                                    IeDoubleCommand doubleCommand = (IeDoubleCommand) element;
                                    value = doubleCommand.getCommandState();
                                }

                                break;
                            }
                        }

                        if (value != null) {
                            variable.setValue(value);
                        }

                        break;
                    }
                }
            }
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
