package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.exceptions.NotAllowedWebException;
import cc.blynk.server.core.model.exceptions.OrgNotFoundException;
import cc.blynk.server.core.model.exceptions.ProductNotFoundException;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class OrganizationDao {

    public static final int DEFAULT_ORGANIZATION_ID = 1;
    private static final Logger log = LogManager.getLogger(OrganizationDao.class);
    public final ConcurrentMap<Integer, Organization> organizations;
    private final AtomicInteger orgSequence;
    private final AtomicInteger productSequence;
    private final FileManager fileManager;

    public OrganizationDao(FileManager fileManager) {
        this.fileManager = fileManager;
        this.organizations = fileManager.deserializeOrganizations();

        int largestOrgSequenceNumber = 0;
        int largestProductSequenceNumber = 0;
        for (Organization organization : organizations.values()) {
            largestOrgSequenceNumber = Math.max(largestOrgSequenceNumber, organization.id);
            for (Product product : organization.products) {
                largestProductSequenceNumber = Math.max(largestProductSequenceNumber, product.id);
            }
        }
        this.orgSequence = new AtomicInteger(largestOrgSequenceNumber);
        this.productSequence = new AtomicInteger(largestProductSequenceNumber);
        log.info("Organization sequence number is {}", largestOrgSequenceNumber);
    }

    public Organization create(Organization organization) {
        organization.id = orgSequence.incrementAndGet();
        organizations.putIfAbsent(organization.id, organization);
        return organization;
    }

    public Product getProduct(int orgId, int productId) {
        Organization org = getOrgById(orgId);
        for (Product product : org.products) {
            if (product.id == productId) {
                return product;
            }
        }
        log.error("Product with passed id {} not found in organization with id {}.", productId, orgId);
        throw new ProductNotFoundException("Product with passed id " + productId + " not found in organization with id " + orgId);
    }

    public Organization getOrgById(int id) {
        Organization org = organizations.get(id);
        if (org == null) {
            log.error("Cannot find org with id {}.", id);
            throw new OrgNotFoundException("Cannot find organization with passed id.");
        }
        return org;
    }

    public boolean deleteProduct(User user, int productId) {
        for (Organization org : organizations.values()) {
            for (Product product : org.products) {
                if (product.id == productId) {
                    if (!user.hasAccess(org.id)) {
                        throw new NotAllowedWebException("User has no rights for product removal.");
                    }
                    org.deleteProduct(productId);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean delete(int id) {
        Organization org = organizations.remove(id);
        if (org != null) {
            fileManager.deleteOrg(id);
            return true;
        }
        return false;
    }

    public Product createProduct(int orgId, Product product) {
        Organization organization = getOrgById(orgId);
        product.id = productSequence.incrementAndGet();
        organization.addProduct(product);
        return product;
    }

    public int getOrganizationIdByProductId(int productId) {
        for (Organization org : organizations.values()) {
            for (Product product : org.products) {
                if (product.id == productId) {
                    return org.id;
                }
            }
        }
        return -1;
    }

    public String getOrganizationNameByProductId(int productId) {
        for (Organization org : organizations.values()) {
            for (Product product : org.products) {
                if (product.id == productId) {
                    return org.name;
                }
            }
        }
        return null;
    }

    public Product getProductById(int productId) {
        Product product = getProductByIdOrNull(productId);
        if (product == null) {
            log.error("Product with passed id {} not exists.", productId);
            throw new ProductNotFoundException("Product with passed id " + productId + " not found.");
        }
        return product;
    }

    public Product getProductByIdOrNull(int productId) {
        for (Organization org : organizations.values()) {
            for (Product product : org.products) {
                if (product.id == productId) {
                    return product;
                }
            }
        }
        return null;
    }

}
