package cc.blynk.server.hardware.handlers.hardware;

import cc.blynk.server.Holder;
import cc.blynk.server.hardware.handlers.hardware.logic.BlynkInternalLogic;
import cc.blynk.server.hardware.handlers.hardware.logic.HardwareLogEventLogic;
import cc.blynk.server.hardware.handlers.hardware.logic.SetWidgetPropertyLogic;

public final class HardwareLogicHolder {

    final HardwareLogEventLogic hardwareLogEventLogic;
    final BlynkInternalLogic blynkInternalLogic;
    final SetWidgetPropertyLogic setWidgetPropertyLogic;

    public HardwareLogicHolder(Holder holder) {
        this.hardwareLogEventLogic = new HardwareLogEventLogic(holder);
        this.blynkInternalLogic = new BlynkInternalLogic(holder);
        this.setWidgetPropertyLogic = new SetWidgetPropertyLogic(holder);
    }
}
