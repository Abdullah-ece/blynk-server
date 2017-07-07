package cc.blynk.server.core.model.device;

import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.widgets.Target;
import cc.blynk.utils.JsonParser;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 16.11.16.
 */
public class Device implements Target {

    public int id;

    public volatile int productId = -1;

    public volatile String name;

    public volatile String boardType;

    public volatile String token;

    public volatile ConnectionType connectionType;

    public volatile Status status = Status.OFFLINE;

    public long createdAt;

    public volatile long disconnectTime;

    public volatile String lastLoggedIP;

    public volatile long dataReceivedAt;

    public volatile MetaField[] metaFields;

    public boolean isNotValid() {
        return boardType == null || boardType.isEmpty() || boardType.length() > 50 || (name != null && name.length() > 50);
    }

    public Device() {
        this.createdAt = System.currentTimeMillis();
    }

    public Device(String name, String boardType, String token, int productId, ConnectionType connectionType) {
        this();
        this.name = name;
        this.boardType = boardType;
        this.token = token;
        this.productId = productId;
        this.connectionType = connectionType;
    }

    public Device(int id, String name, String boardType) {
        this();
        this.id = id;
        this.name = name;
        this.boardType = boardType;
    }

    public MetaField findMetaFieldById(int id) {
        for (MetaField metaField : metaFields) {
            if (metaField.id == id) {
                return metaField;
            }
        }
        return null;
    }

    @Override
    public int[] getDeviceIds() {
        return new int[] {id};
    }

    @Override
    public int getDeviceId() {
        return id;
    }

    public void update(Device newDevice) {
        this.productId = newDevice.productId;
        this.name = newDevice.name;
        this.boardType = newDevice.boardType;
        this.connectionType = newDevice.connectionType;
        this.metaFields = newDevice.metaFields;
    }

    public void disconnected() {
        this.status = Status.OFFLINE;
        this.disconnectTime = System.currentTimeMillis();
    }

    public void erase() {
        this.token = null;
        this.disconnectTime = 0;
        this.lastLoggedIP = null;
        this.status = Status.OFFLINE;
    }

    public void connected() {
        this.status = Status.ONLINE;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
