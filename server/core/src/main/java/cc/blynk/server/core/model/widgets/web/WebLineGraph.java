package cc.blynk.server.core.model.widgets.web;

import cc.blynk.server.core.model.enums.PinMode;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.08.17.
 */

public class WebLineGraph extends WebWidget {

    public boolean isShowTitleEnabled;

    public boolean isShowLegendEnabled;

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
        if (this == o) return true;
        if (!(o instanceof WebLineGraph)) return false;
        if (!super.equals(o)) return false;

        WebLineGraph that = (WebLineGraph) o;

        if (isShowTitleEnabled != that.isShowTitleEnabled) return false;
        return isShowLegendEnabled == that.isShowLegendEnabled;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isShowTitleEnabled ? 1 : 0);
        result = 31 * result + (isShowLegendEnabled ? 1 : 0);
        return result;
    }
}
