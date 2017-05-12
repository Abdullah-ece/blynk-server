package cc.blynk.server.core.model.web.product;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 5/12/17.
 */
public class EventReceiver {

    public int id;

    public String type;

    public String value;

    public EventReceiver() {
    }

    public EventReceiver(int id, String type, String value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }
}
