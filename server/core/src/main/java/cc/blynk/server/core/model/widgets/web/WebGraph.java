package cc.blynk.server.core.model.widgets.web;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.08.17.
 */

public class WebGraph extends WebWidget {

    public boolean isShowTitleEnabled;

    public boolean isShowLegendEnabled;

    @Override
    public String getModeType() {
        return "in";
    }

    @Override
    public int getPrice() {
        return 0;
    }
}
