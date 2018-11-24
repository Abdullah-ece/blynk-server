package cc.blynk.server.core.model;

import cc.blynk.server.core.model.serialization.JsonParser;

import java.util.List;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.04.17.
 */
public class ExportAppProfileDTO {

    public final List<DashBoard> dashBoards;

    public ExportAppProfileDTO(List<DashBoard> dashBoards) {
        this.dashBoards = dashBoards;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

}
