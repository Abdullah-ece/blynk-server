package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.web.product.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class ProductDao {

    private static final Logger log = LogManager.getLogger(ProductDao.class);

    private final AtomicInteger sequence;
    private final ConcurrentMap<Integer, Product> products = new ConcurrentHashMap<>();

    public ProductDao(ConcurrentMap<UserKey, User> users) {
        int latestSequenceNumber = 0;
        for (User user : users.values()) {
            for (Product product : user.organization.products) {
                products.put(product.id, product);
                latestSequenceNumber = Math.max(latestSequenceNumber, product.id);
            }
        }
        this.sequence = new AtomicInteger(latestSequenceNumber);
        log.debug("Product sequence number is {}", latestSequenceNumber);
    }

    public Product createProduct(Product product) {
        product.id = sequence.incrementAndGet();
        products.put(product.id, product);
        return product;
    }

    public Product getById(int id) {
        return products.get(id);
    }
}