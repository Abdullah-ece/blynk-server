package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.enums.PinMode;

import java.util.Objects;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.08.17.
 */

public abstract class BaseWebGraph extends WebWidget {

    public boolean isShowTitleEnabled;

    public boolean isShowLegendEnabled;

    public String decimalFormat;

    @Override
    public PinMode getModeType() {
        return PinMode.in;
    }

    @Override
    public int getPrice() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseWebGraph)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BaseWebGraph that = (BaseWebGraph) o;
        return isShowTitleEnabled == that.isShowTitleEnabled
                && isShowLegendEnabled == that.isShowLegendEnabled
                && Objects.equals(decimalFormat, that.decimalFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isShowTitleEnabled, isShowLegendEnabled, decimalFormat);
    }
}
