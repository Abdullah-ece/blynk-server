package cc.blynk.server.core.stats.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The Blynk Project.
 * Created by Nikita Piashyntsev.
 * Created on 31.01.19.
 */
public final class AllCommandsStat {

    public final Map<Short, Integer> stats = new HashMap<>();

    AllCommandsStat() {
    }

    public void setCommandCounter(Short command, Integer counter) {
        stats.put(command, counter);
    }

    public int getCommandCounter(Short command) {
        Integer counter = stats.get(command);
        return counter == null ? 0 : counter;
    }
}
