package cc.blynk.server.core.model.widgets.web.label;

import cc.blynk.server.core.model.enums.PinMode;
import cc.blynk.server.core.model.widgets.outputs.TextAlignment;
import cc.blynk.server.core.model.widgets.web.WebWidget;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.08.17.
 */

public class WebLabel extends WebWidget {

    public DataType dataType;

    public String decimalFormat;

    public String valueSuffix;

    public TextAlignment alignment;

    public boolean isColorSetEnabled;

    public ColorSet[] colorsSet;

    public String backgroundColor;

    public String textColor;

    public boolean isShowLevelEnabled;

    public Level level;

    @Override
    public PinMode getModeType() {
        return PinMode.in;
    }

    @Override
    public int getPrice() {
        return 0;
    }
}
