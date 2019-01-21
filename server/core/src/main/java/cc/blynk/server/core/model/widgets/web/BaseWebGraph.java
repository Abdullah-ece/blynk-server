package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.enums.PinMode;

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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        BaseWebGraph that = (BaseWebGraph) o;

        if (isShowTitleEnabled != that.isShowTitleEnabled) {
            return false;
        }
        if (isShowLegendEnabled != that.isShowLegendEnabled) {
            return false;
        }
        return decimalFormat != null ? decimalFormat.equals(that.decimalFormat) : that.decimalFormat == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isShowTitleEnabled ? 1 : 0);
        result = 31 * result + (isShowLegendEnabled ? 1 : 0);
        result = 31 * result + (decimalFormat != null ? decimalFormat.hashCode() : 0);
        return result;
    }
}
