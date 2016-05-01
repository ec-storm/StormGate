package com.minhdtb.storm.core.data;


public class StormVariableIEC extends StormVariable {

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
}
