package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Shipment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public final class OTADao {

    private static final Logger log = LogManager.getLogger(OTADao.class);

    private final AtomicInteger shipmentSuquence;

    public OTADao(Collection<Organization> orgs) {
        int maxShipmentId = 0;
        for (Organization org : orgs) {
            for (Shipment shipment : org.shipments) {
                maxShipmentId = Math.max(shipment.id, maxShipmentId);
            }
        }

        this.shipmentSuquence = new AtomicInteger(maxShipmentId);
        log.info("Shipment sequence is {}",  shipmentSuquence.get());
    }

}
