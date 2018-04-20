package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.web.BaseWebGraph;
import cc.blynk.utils.ArrayUtil;
import cc.blynk.utils.CopyObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_WIDGETS;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.08.17.
 */
public class WebDashboard implements CopyObject<WebDashboard> {

    public volatile Widget[] widgets;

    public WebDashboard() {
        this.widgets = EMPTY_WIDGETS;
    }

    @JsonCreator
    public WebDashboard(@JsonProperty("widgets") Widget[] widgets) {
        this.widgets = widgets == null ? EMPTY_WIDGETS : widgets;
    }

    @Override
    public WebDashboard copy() {
        return new WebDashboard(ArrayUtil.copy(this.widgets, Widget.class));
    }

    public void update(WebDashboard updatedDashboard) {
        this.widgets = DashBoard.copyWidgetsAndPreservePrevValues(widgets, updatedDashboard.widgets);
    }

    public boolean update(int deviceId, byte pin, PinType type, String value) {
        boolean hasWidget = false;
        for (Widget widget : widgets) {
            if (widget.updateIfSame(deviceId, pin, type, value)) {
                hasWidget = true;
            }
        }
        return hasWidget;
    }

    public Widget getWidgetById(long id) {
        for (Widget widget : widgets) {
            if (widget.id == id) {
                return widget;
            }
        }
        return null;
    }

    public boolean needRawDataForGraph(byte pin, PinType pinType) {
        for (Widget widget : widgets) {
            //realtime is needed only for webgraph widget
            if (widget instanceof BaseWebGraph) {
                //todo fix -1?
                if (widget.isSame(-1, pin, pinType)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WebDashboard)) {
            return false;
        }

        WebDashboard that = (WebDashboard) o;

        return Arrays.equals(widgets, that.widgets);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(widgets);
    }
}
