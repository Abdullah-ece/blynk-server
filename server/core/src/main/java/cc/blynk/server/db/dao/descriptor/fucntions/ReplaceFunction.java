package cc.blynk.server.db.dao.descriptor.fucntions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.function.Function;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 10.10.17.
 */
public class ReplaceFunction implements Function<String, String> {

    public final String replaceFrom;
    public final String replaceTo;

    @JsonCreator
    public ReplaceFunction(@JsonProperty("replaceFrom") String replaceFrom,
                           @JsonProperty("replaceTo") String replaceTo) {
        this.replaceFrom = replaceFrom;
        this.replaceTo = replaceTo == null ? "" : null;
    }

    public ReplaceFunction(String replaceFrom) {
        this(replaceFrom, null);
    }

    @Override
    public String apply(String s) {
        return s.replace(replaceFrom, replaceTo);
    }
}
