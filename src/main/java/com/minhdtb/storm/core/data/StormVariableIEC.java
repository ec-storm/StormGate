package com.minhdtb.storm.core.data;


import com.minhdtb.storm.common.Utils;
import com.minhdtb.storm.core.lib.j60870.ASdu;
import com.minhdtb.storm.core.lib.j60870.CauseOfTransmission;
import com.minhdtb.storm.core.lib.j60870.TypeId;
import com.minhdtb.storm.entities.Variable;

public class StormVariableIEC extends StormVariable {

    public static final String SECTOR_ADDRESS = "sectorAddress";
    public static final String INFORMATION_OBJECT_ADDRESS = "informationObjectAddress";
    public static final String DATA_TYPE = "dataType";

    public StormVariableIEC() {
        super();
    }

    StormVariableIEC(Variable variable) {
        super(variable);
    }

    int getSectorAddress() {
        return Integer.parseInt(getAttribute(SECTOR_ADDRESS));
    }

    public void setSectorAddress(int sectorAddress) {
        setAttribute(SECTOR_ADDRESS, String.valueOf(sectorAddress));
    }

    int getInformationObjectAddress() {
        return Integer.parseInt(getAttribute(INFORMATION_OBJECT_ADDRESS));
    }

    public void setInformationObjectAddress(int informationObjectAddress) {
        setAttribute(INFORMATION_OBJECT_ADDRESS, String.valueOf(informationObjectAddress));
    }

    private int getDataType() {
        return Integer.parseInt(getAttribute(DATA_TYPE));
    }

    public void setDataType(int dataType) {
        setAttribute(DATA_TYPE, String.valueOf(dataType));
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
