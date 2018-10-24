package cc.blynk.server.core.model;

import cc.blynk.server.core.model.serialization.JsonParser;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.04.17.
 */
public class ExportAppProfileDTO {

    public final DashBoard[] dashBoards;

    public ExportAppProfileDTO(DashBoard[] dashBoards) {
        this.dashBoards = dashBoards;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

}
