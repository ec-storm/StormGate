package com.minhdtb.storm.common;

import com.minhdtb.storm.base.AbstractView;
import com.minhdtb.storm.core.lib.j60870.*;
import com.minhdtb.storm.services.Publisher;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Utils {

    private static Publisher<String> publisher;

    @Autowired
    public Utils(Publisher<String> publisher) {
        Utils.publisher = publisher;
    }

    public static void showError(AbstractView owner, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "");
        alert.initModality(Modality.APPLICATION_MODAL);

        if (owner != null) {
            alert.initOwner(owner.getWindow());
        }

        alert.setHeaderText(message);
        alert.setContentText(null);
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK);
    }

    public static void showConfirm(AbstractView owner, String message, EventHandler<Event> Ok) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);

        if (owner != null) {
            alert.initOwner(owner.getWindow());
        }

        alert.setHeaderText(message);
        alert.setContentText(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Ok.handle(null);
        } else {
            alert.close();
        }
    }

    public static EventHandler<KeyEvent> numericValidation(final Integer maxLength) {
        return e -> {
            TextField textField = (TextField) e.getSource();
            if (textField.getText().length() >= maxLength) {
                e.consume();
            }

            if (e.getCharacter().matches("[0-9.]")) {
                if (textField.getText().contains(".") && e.getCharacter().matches("[.]")) {
                    e.consume();
                } else if (textField.getText().length() == 0 && e.getCharacter().matches("[.]")) {
                    e.consume();
                }
            } else {
                e.consume();
            }
        };
    }

    public static void writeLog(String message) {
        if (message != null) {
            publisher.publish("application:log", message + "\n");
        }
    }

    public static Object ASduToObject(ASdu aSdu) {
        Object value = null;

        TypeId typeId = aSdu.getTypeIdentification();
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
                    value = doubleCommand.getCommandState().getId();
                }

                break;
            }
        }

        return value;
    }

    public static ASdu ObjectToASdu(TypeId typeId, CauseOfTransmission causeOfTransmission, int originatorAddress,
                                    int sectorAddress, int informationObjectAddress, Object object) {
        switch (typeId) {
            case M_ME_NA_1: {
                return new ASdu(typeId, false, causeOfTransmission, false, false, originatorAddress, sectorAddress,
                        new InformationObject[]{
                                new InformationObject(informationObjectAddress, new InformationElement[][]{
                                        {new IeNormalizedValue((int) object)}
                                })
                        });
            }
            case M_ME_NC_1: {
                return new ASdu(typeId, false, causeOfTransmission, false, false, originatorAddress, sectorAddress,
                        new InformationObject[]{
                                new InformationObject(informationObjectAddress, new InformationElement[][]{
                                        {new IeShortFloat((float) object)}
                                })
                        });
            }
            case C_SC_NA_1: {
                return new ASdu(typeId, false, causeOfTransmission, false, false, originatorAddress, sectorAddress,
                        new InformationObject[]{
                                new InformationObject(informationObjectAddress, new InformationElement[][]{
                                        {new IeSingleCommand((boolean) object, 0, false)}
                                })
                        });
            }
            case C_DC_NA_1: {
                return new ASdu(typeId, false, causeOfTransmission, false, false, originatorAddress, sectorAddress,
                        new InformationObject[]{
                                new InformationObject(informationObjectAddress, new InformationElement[][]{
                                        {new IeDoubleCommand(IeDoubleCommand.DoubleCommandState.getInstance((int) object), 0, false)}
                                })
                        });
            }
        }

        return null;
    }
}
