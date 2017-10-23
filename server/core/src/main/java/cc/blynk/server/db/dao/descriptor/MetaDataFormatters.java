package cc.blynk.server.db.dao.descriptor;

import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 18.10.17.
 */
public enum MetaDataFormatters {

    MM_DD_YY("MM/dd/yy"),
    HH_MM_SS("HH:mm:ss"),
    M_DD_YYYY_HH_MM_SS("M/dd/yyyy HH:mm:ss");

    MetaDataFormatters(String formatString) {
        this.formatter = ofPattern(formatString);
    }

    public final DateTimeFormatter formatter;

}
