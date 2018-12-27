package cc.blynk.server.core.processors.rules;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 27.12.18.
 */
public class RuleGroup {

    private static final Rule[] EMPTY_RULES = {};

    public final Rule[] rules;

    @JsonCreator
    public RuleGroup(@JsonProperty("rules") Rule[] rules) {
        this.rules = rules == null ?  EMPTY_RULES : rules;
    }
}
