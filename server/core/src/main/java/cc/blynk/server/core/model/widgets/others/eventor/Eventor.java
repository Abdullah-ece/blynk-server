package cc.blynk.server.core.model.widgets.others.eventor;

import cc.blynk.server.core.model.enums.PinMode;
import cc.blynk.server.core.model.widgets.NoPinWidget;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.BaseAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.SetPinAction;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 01.08.16.
 */
public class Eventor extends NoPinWidget {

    public EventorRule[] rules;

    public int deviceId;

    public Eventor() {
        this.width = 2;
        this.height = 1;
    }

    public Eventor(EventorRule[] eventorRules) {
        this();
        this.rules = eventorRules;
    }

    @Override
    public PinMode getModeType() {
        return PinMode.out;
    }

    @Override
    public void append(StringBuilder sb, int deviceId) {
        if (rules != null && this.deviceId == deviceId) {
            for (EventorRule eventorRule : rules) {
                if (eventorRule.actions != null) {
                    for (BaseAction action : eventorRule.actions) {
                        if (action instanceof SetPinAction) {
                            SetPinAction setPinActionAction = (SetPinAction) action;
                            if (setPinActionAction.dataStream != null) {
                                append(sb, setPinActionAction.dataStream.pin,
                                        setPinActionAction.dataStream.pinType);
                            }
                        }
                    }
                }
            }
        }
    }

}
