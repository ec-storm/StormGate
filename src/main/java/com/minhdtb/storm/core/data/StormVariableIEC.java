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
        setAttribute("sectorAddress", sectorAddress);
    }

    int getInformationObjectAddress() {
        return Integer.parseInt(getAttribute("informationObjectAddress"));
    }

    public void setInformationObjectAddress(int informationObjectAddress) {
        setAttribute("informationObjectAddress", informationObjectAddress);
    }

    private int getDataType() {
        return Integer.parseInt(getAttribute("dataType"));
    }

    public void setDataType(int dataType) {
        setAttribute("dataType", dataType);
    }

    @Override
    public void write(Object value) {
        IStormChannel channel = getChannel();
        if (channel instanceof StormChannelIEC) {
            ASdu aSdu = null;

            switch (TypeId.getInstance(getDataType())) {
                case M_ME_NA_1: {
                    aSdu = Utils.ObjectToASdu(TypeId.M_ME_NA_1, CauseOfTransmission.SPONTANEOUS, 0, getSectorAddress(),
                            getInformationObjectAddress(), value);
                    break;
                }
                case M_ME_NC_1: {
                    aSdu = Utils.ObjectToASdu(TypeId.M_ME_NC_1, CauseOfTransmission.SPONTANEOUS, 0, getSectorAddress(),
                            getInformationObjectAddress(), value);

                    break;
                }
                case C_SC_NA_1: {
                    aSdu = Utils.ObjectToASdu(TypeId.C_SC_NA_1, CauseOfTransmission.SPONTANEOUS, 0, getSectorAddress(),
                            getInformationObjectAddress(), value);

                    break;
                }
                case C_DC_NA_1: {
                    aSdu = Utils.ObjectToASdu(TypeId.C_DC_NA_1, CauseOfTransmission.SPONTANEOUS, 0, getSectorAddress(),
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
