package com.minhdtb.storm.core.data;


import com.minhdtb.storm.entities.Variable;

public class StormVariableIEC extends StormVariable {

    private StormChannelIEC channelIEC;

    public StormVariableIEC() {
        super();
    }

    StormVariableIEC(Variable variable) {
        super(variable);
    }

    public int getSectorAddress() {
        return Integer.parseInt(getAttribute("sectorAddress"));
    }

    public void setSectorAddress(int sectorAddress) {
        setAttribute("sectorAddress", String.valueOf(sectorAddress));
    }

    public int getInformationObjectAddress() {
        return Integer.parseInt(getAttribute("informationObjectAddress"));
    }

    public void setInformationObjectAddress(int informationObjectAddress) {
        setAttribute("informationObjectAddress", String.valueOf(informationObjectAddress));
    }

    public int getDataType() {
        return Integer.parseInt(getAttribute("dataType"));
    }

    public void setDataType(int dataType) {
        setAttribute("dataType", String.valueOf(dataType));
    }

    @Override
    public void write(Object value) {
        IStormChannel channel = getChannel();
        if (channel instanceof StormChannelIEC) {
            channelIEC = (StormChannelIEC) channel;

            System.out.println("xxx = " + value);
            super.write(value);
        }
    }
}
