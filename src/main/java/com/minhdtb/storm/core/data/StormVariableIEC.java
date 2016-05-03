package com.minhdtb.storm.core.data;


import com.minhdtb.storm.core.lib.j60870.*;
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
    public void writeValue(Object value) {
        IStormChannel channel = getChannel();
        if (channel instanceof StormChannelIEC) {
            ASdu aSdu = null;
            switch (getDataType()) {
                case 0: {
                    aSdu = new ASdu(TypeId.M_ME_NA_1, false, CauseOfTransmission.SPONTANEOUS, false, false, 1, getSectorAddress(),
                            new InformationObject[]{
                                    new InformationObject(getInformationObjectAddress(), new InformationElement[][]{
                                            {new IeNormalizedValue((int) value)}
                                    })
                            });

                    break;
                }
                case 1: {
                    aSdu = new ASdu(TypeId.M_ME_NC_1, false, CauseOfTransmission.SPONTANEOUS, false, false, 1, getSectorAddress(),
                            new InformationObject[]{
                                    new InformationObject(getInformationObjectAddress(), new InformationElement[][]{
                                            {new IeShortFloat((float) value)}
                                    })
                            });

                    break;
                }
                case 2: {
                    aSdu = new ASdu(TypeId.C_SC_NA_1, false, CauseOfTransmission.SPONTANEOUS, false, false, 1, getSectorAddress(),
                            new InformationObject[]{
                                    new InformationObject(getInformationObjectAddress(), new InformationElement[][]{
                                            {new IeSingleCommand(true, 0, true)}
                                    })
                            });

                    break;
                }
                case 3: {
                    aSdu = new ASdu(TypeId.C_DC_NA_1, false, CauseOfTransmission.SPONTANEOUS, false, false, 1, getSectorAddress(),
                            new InformationObject[]{
                                    new InformationObject(getInformationObjectAddress(), new InformationElement[][]{
                                            {new IeDoubleCommand(IeDoubleCommand.DoubleCommandState.ON, 0, true)}
                                    })
                            });

                    break;
                }
            }

            if (aSdu != null) {
                ((StormChannelIEC) channel).send(aSdu);
                super.writeValue(value);
            }
        }
    }
}
