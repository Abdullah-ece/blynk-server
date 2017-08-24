package cc.blynk.server.core.model.web.product;

import cc.blynk.server.core.model.widgets.CopyObject;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.utils.ArrayUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.08.17.
 */
public class WebDashboard implements CopyObject<WebDashboard> {

    public volatile Widget[] widgets = ArrayUtil.EMPTY_WIDGETS;

    @JsonCreator
    public WebDashboard(@JsonProperty("widgets") Widget[] widgets) {
        this.widgets = widgets;
    }

    @Override
    public WebDashboard copy() {
        return new WebDashboard(ArrayUtil.copy(this.widgets, Widget.class));
    }

}
