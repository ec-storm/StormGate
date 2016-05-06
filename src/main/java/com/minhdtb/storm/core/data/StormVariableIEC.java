package com.minhdtb.storm.core.data;


import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.lib.j60870.ASdu;
import com.minhdtb.storm.core.lib.j60870.CauseOfTransmission;
import com.minhdtb.storm.core.lib.j60870.TypeId;
import com.minhdtb.storm.entities.Variable;

public class StormVariableIEC extends StormVariable {

    public StormVariableIEC() {
        super();
    }

    StormVariableIEC(Variable variable) {
        super(variable);
    }

    int getSectorAddress() {
        return Integer.parseInt(getAttribute("sectorAddress"));
    }

    public void setSectorAddress(int sectorAddress) {
        setAttribute("sectorAddress", String.valueOf(sectorAddress));
    }

    int getInformationObjectAddress() {
        return Integer.parseInt(getAttribute("informationObjectAddress"));
    }

    public void setInformationObjectAddress(int informationObjectAddress) {
        setAttribute("informationObjectAddress", String.valueOf(informationObjectAddress));
    }

    private int getDataType() {
        return Integer.parseInt(getAttribute("dataType"));
    }

    public void setDataType(int dataType) {
        setAttribute("dataType", String.valueOf(dataType));
    }

    @Override
    public void write(Object value) {
        IStormChannel channel = getChannel();
        if (channel instanceof StormChannelIEC) {
            ASdu aSdu = null;

            switch (getDataType()) {
                case 0: {
                    aSdu = Utils.ObjectToASdu(TypeId.M_ME_NA_1, CauseOfTransmission.SPONTANEOUS, 1, getSectorAddress(),
                            getInformationObjectAddress(), value);
                    break;
                }
                case 1: {
                    aSdu = Utils.ObjectToASdu(TypeId.M_ME_NC_1, CauseOfTransmission.SPONTANEOUS, 1, getSectorAddress(),
                            getInformationObjectAddress(), value);

                    break;
                }
                case 2: {
                    aSdu = Utils.ObjectToASdu(TypeId.C_SC_NA_1, CauseOfTransmission.SPONTANEOUS, 1, getSectorAddress(),
                            getInformationObjectAddress(), value);

                    break;
                }
                case 3: {
                    aSdu = Utils.ObjectToASdu(TypeId.C_DC_NA_1, CauseOfTransmission.SPONTANEOUS, 1, getSectorAddress(),
                            getInformationObjectAddress(), value);

                    break;
                }
            }

            if (aSdu != null) {
                ((StormChannelIEC) channel).send(aSdu);
                super.write(value);
            }
        }
    }
}
