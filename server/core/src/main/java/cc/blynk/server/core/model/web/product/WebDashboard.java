package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.widgets.CopyObject;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.utils.ArrayUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.08.17.
 */
public class WebDashboard implements CopyObject<WebDashboard> {

    public volatile Widget[] widgets = ArrayUtil.EMPTY_WIDGETS;

    public WebDashboard() {
        this.widgets = ArrayUtil.EMPTY_WIDGETS;
    }

    @JsonCreator
    public WebDashboard(@JsonProperty("widgets") Widget[] widgets) {
        this.widgets = widgets;
    }

    @Override
    public WebDashboard copy() {
        return new WebDashboard(ArrayUtil.copy(this.widgets, Widget.class));
    }

    //todo update without erasing value field?
    public void update(WebDashboard updatedDashboard) {
        if (updatedDashboard.widgets != null) {
            this.widgets = updatedDashboard.widgets;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebDashboard)) return false;

        WebDashboard that = (WebDashboard) o;

        return Arrays.equals(widgets, that.widgets);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(widgets);
    }
}
