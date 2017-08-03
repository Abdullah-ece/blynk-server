package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.exceptions.ForbiddenWebException;
import cc.blynk.server.core.model.exceptions.OrgNotFoundException;
import cc.blynk.server.core.model.exceptions.ProductNotFoundException;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private final DeviceDao deviceDao;

    public OrganizationDao(FileManager fileManager, DeviceDao deviceDao) {
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
        this.deviceDao = deviceDao;
        log.info("Organization sequence number is {}", largestOrgSequenceNumber);
    }

    public Organization create(Organization organization) {
        checkNameExists(-1, organization.name);
        organization.id = orgSequence.incrementAndGet();
        organizations.putIfAbsent(organization.id, organization);
        return organization;
    }

    public void checkNameExists(int orgId, String name) {
        for (Organization org : organizations.values()) {
            if (org.id != orgId && name.equalsIgnoreCase(org.name)) {
                throw new ForbiddenWebException("Organization with this name already exists.");
            }
        }
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

    public Collection<Organization> getAll(User user) {
        if (user.isSuperAdmin()) {
            return organizations.values();
        } else {
            return getOrgsByParentId(user.orgId);
        }
    }

    private List<Organization> getOrgsByParentId(int parentId) {
        List<Organization> orgs = new ArrayList<>();
        Organization org = organizations.get(parentId);
        orgs.add(org);
        getOrgsByParentId(orgs, parentId, 1);
        return orgs;
    }

    private void getOrgsByParentId(List<Organization> orgs, int parentId, int invocationCounter) {
        if (invocationCounter == 100) {
            throw new RuntimeException("Error finding organization.");
        }
        for (Organization org : organizations.values()) {
            if (org.parentId == parentId) {
                orgs.add(org);
                getOrgsByParentId(orgs, org.id, invocationCounter++);
            }
        }
    }

    public boolean hasAccess(User user, int orgId) {
        if (user.isSuperAdmin()) {
            return true;
        }
        if (user.isAdmin()) {
            if (user.orgId == orgId) {
                return true;
            } else {
                //user is admin of parent org, so he can perform admin action on child org
                List<Organization> childOrgs = getOrgsByParentId(user.orgId);
                Organization org = getOrgById(childOrgs, orgId);
                if (org != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public Collection<Organization> getAll() {
        return organizations.values();
    }

    public static Organization getOrgById(List<Organization> orgs, int id) {
        for (Organization org : orgs) {
            if (org.id == id) {
                return org;
            }
        }
        return null;
    }

    public Organization getOrgById(int id) {
        Organization org = organizations.get(id);
        if (org == null) {
            log.error("Cannot find org with id {}.", id);
            throw new OrgNotFoundException("Cannot find organization with passed id.");
        }
        return org;
    }

    public boolean deleteProductByParentId(int productParentId) {
        for (Organization org : organizations.values()) {
            for (Product product : org.products) {
                if (product.parentId == productParentId) {
                    deleteProduct(org, product.id);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean deleteProduct(User user, int productId) {
        for (Organization org : organizations.values()) {
            for (Product product : org.products) {
                if (product.id == productId) {
                    if (!hasAccess(user, org.id)) {
                        throw new ForbiddenWebException("User has no rights for product removal.");
                    }
                    deleteProduct(org, product.id);
                    return true;
                }
            }
        }
        return false;
    }

    private void deleteProduct(Organization org, int productId) {
        if (deviceDao.productHasDevices(productId)) {
            log.error("You are not allowed to remove product with devices.");
            throw new ForbiddenWebException("You are not allowed to remove product with devices.");
        }
        org.deleteProduct(productId);
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

    public boolean hasNoProductWithParent(int newOrgId, int parentProductId) {
        Product product = getProductByParentIdOrNull(newOrgId, parentProductId);
        return product == null;
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

    public Product getProductByParentIdOrNull(int newOrgId, int parentProductId) {
        for (Organization org : organizations.values()) {
            for (Product product : org.products) {
                if (org.id == newOrgId && product.parentId == parentProductId) {
                    return product;
                }
            }
        }
        return null;
    }

}
