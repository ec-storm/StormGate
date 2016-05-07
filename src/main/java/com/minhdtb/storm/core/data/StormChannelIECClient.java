package com.minhdtb.storm.core.data;

import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.lib.j60870.ASdu;
import com.minhdtb.storm.core.lib.j60870.ClientSap;
import com.minhdtb.storm.core.lib.j60870.Connection;
import com.minhdtb.storm.core.lib.j60870.ConnectionEventListener;
import com.minhdtb.storm.entities.Channel;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

public class StormChannelIECClient extends StormChannelIEC {

    private ClientSap clientSap = new ClientSap();
    private boolean stopped = false;
    private Connection connection;

    private CountDownLatch countDownLatch;

    public StormChannelIECClient() {
        super();
        getRaw().setType(Channel.ChannelType.CT_IEC_CLIENT);
    }

    public StormChannelIECClient(Channel channel) {
        super(channel);
    }

    @Override
    public void send(ASdu aSdu) {
        try {
            connection.send(aSdu);
        } catch (IOException e) {
            Utils.error(e);
        }
    }

    @Override
    public void start() {
        ClientThread thread = new ClientThread();
        thread.start();
    }

    @Override
    public void stop() {
        stopped = true;
        if (connection != null) {
            connection.close();
        }
    }

    private final class ClientThread extends Thread {

        @Override
        public void run() {
            while (!stopped) {
                try {
                    connection = clientSap.connect(InetAddress.getByName(getHost()), getPort());
                    connection.startDataTransfer(new ConnectionEventListener() {

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
                            countDownLatch.countDown();
                        }
                    }, TIMEOUT);

                    try {
                        countDownLatch = new CountDownLatch(1);
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        Utils.error(e);
                        Thread.currentThread().interrupt();
                    }

                } catch (IOException | TimeoutException e) {
                    Utils.error(e);
                    countDownLatch.countDown();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Utils.error(e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
