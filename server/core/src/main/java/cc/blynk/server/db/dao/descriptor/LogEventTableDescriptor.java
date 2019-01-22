package cc.blynk.server.db.dao.descriptor;

import org.jooq.Field;

import java.sql.Timestamp;

import static org.jooq.impl.DSL.field;

/**
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 11.01.19.
 */
public final class LogEventTableDescriptor {

    public static final String NAME = "reporting_events";

    public static final Field<Integer>   DEVICE_ID   = field("device_id",   Integer.class);
    public static final Field<Integer>   TYPE        = field("type",        Integer.class);
    public static final Field<Timestamp> TS          = field("ts",          Timestamp.class);
    public static final Field<Boolean>   IS_RESOLVED = field("is_resolved", Boolean.class);
    public static final Field<Timestamp> RESOLVED_AT = field("resolved_at", Timestamp.class);

    private LogEventTableDescriptor() {
    }

}
