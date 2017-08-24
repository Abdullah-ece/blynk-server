package cc.blynk.server.http.web.model;

import cc.blynk.server.core.model.device.ConnectionType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.web.product.EventType;
import cc.blynk.server.core.model.web.product.MetaField;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.db.model.LogEventCountKey;

import java.util.Map;

/**
 * Device field + some additional just for UI.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.07.17.
 */
public class WebDevice {

    public int id;

    public int productId;

    public String name;

    public String boardType;

    public String token;

    public ConnectionType connectionType;

    public Status status;

    public long createdAt;

    public long activatedAt;

    public String activatedBy;

    public long disconnectTime;

    public String lastLoggedIP;

    public long dataReceivedAt;

    public long metadataUpdatedAt;

    public String metadataUpdatedBy;

    public MetaField[] metaFields;

    public Widget[] widgets;

    public String productName;

    public String productLogoUrl;

    public String orgName;

    public Integer criticalSinceLastView;

    public Integer warningSinceLastView;

    public WebDevice() {
    }

    private WebDevice(Device device, Product product) {
        this.id = device.id;
        this.productId = device.productId;
        this.name = device.name;
        this.boardType = device.boardType;
        this.token = device.token;
        this.connectionType = device.connectionType;
        this.status = device.status;
        this.createdAt = device.createdAt;
        this.disconnectTime = device.disconnectTime;
        this.lastLoggedIP = device.lastLoggedIP;
        this.dataReceivedAt = device.dataReceivedAt;
        this.metaFields = device.metaFields;
        this.widgets = device.widgets;
        this.activatedAt = device.activatedAt;
        this.activatedBy = device.activatedBy;
        this.metadataUpdatedAt = device.metadataUpdatedAt;
        this.metadataUpdatedBy = device.metadataUpdatedBy;

        if (product != null) {
            this.productName = product.name;
            this.productLogoUrl = product.logoUrl;
        }
    }

    public WebDevice(Device device, Product product, Map<LogEventCountKey, Integer> counters) {
        this(device, product);

        this.criticalSinceLastView = counters.get(new LogEventCountKey(device.id, EventType.CRITICAL, false));
        this.warningSinceLastView = counters.get(new LogEventCountKey(device.id, EventType.WARNING, false));
    }

    public WebDevice(Device device, Product product, String orgName) {
        this(device, product);
        this.orgName = orgName;
    }

}
