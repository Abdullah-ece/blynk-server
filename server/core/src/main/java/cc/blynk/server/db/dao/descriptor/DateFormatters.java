package cc.blynk.server.db.dao.descriptor;

import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 18.10.17.
 */
public enum DateFormatters {

    MM_DD_YY(ofPattern("MM/dd/yy")),
    HH_MM_SS(ofPattern("HH:mm:ss"));

    DateFormatters(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public final DateTimeFormatter formatter;

}
