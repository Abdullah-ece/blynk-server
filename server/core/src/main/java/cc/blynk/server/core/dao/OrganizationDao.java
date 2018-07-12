package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.exceptions.ForbiddenWebException;
import cc.blynk.server.core.model.exceptions.OrgNotFoundException;
import cc.blynk.server.core.model.exceptions.ProductNotFoundException;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.utils.ArrayUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final UserDao userDao;

    public OrganizationDao(FileManager fileManager, DeviceDao deviceDao, UserDao userDao) {
        this.fileManager = fileManager;
        this.organizations = fileManager.deserializeOrganizations();

        int largestOrgSequenceNumber = DEFAULT_ORGANIZATION_ID;
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
        this.userDao = userDao;
        log.info("Organization sequence number is {}", largestOrgSequenceNumber);
    }

    public Organization create(Organization organization) {
        checkNameExists(-1, organization.name);
        organization.id = orgSequence.incrementAndGet();
        organizations.putIfAbsent(organization.id, organization);
        return organization;
    }

    //only for tests
    public Organization createWithPresetId(int newOrgId, Organization organization) {
        checkNameExists(-1, organization.name);
        organization.id = newOrgId;
        if (organizations.get(newOrgId) != null) {
            throw new RuntimeException("Error creating org with predefiend id.");
        }
        organizations.putIfAbsent(newOrgId, organization);
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
        Organization org = getOrgByIdOrThrow(orgId);
        for (Product product : org.products) {
            if (product.id == productId) {
                return product;
            }
        }
        log.error("Product with passed id {} not found in organization with id {}.", productId, orgId);
        throw new ProductNotFoundException("Product with passed id " + productId
                + " not found in organization with id " + orgId);
    }

    public Collection<Organization> getAll(User user) {
        if (user.isSuperAdmin()) {
            return organizations.values();
        } else {
            return getOrgsByParentId(user.orgId);
        }
    }

    public void calcDeviceCount(List<Organization> orgs) {
        for (Organization org : orgs) {
            calcDeviceCount(org);
        }
    }

    public void calcDeviceCount(Organization org) {
        Map<Integer, Integer> productIdCount = productDeviceCount();
        productIdCount = attachChildCounter(productIdCount);
        for (Product product : org.products) {
            product.deviceCount = productIdCount.getOrDefault(product.id, 0);
        }
    }

    private Map<Integer, Integer> productDeviceCount() {
        Map<Integer, Integer> productIdCount =  new HashMap<>();
        for (Device device : deviceDao.getAll()) {
            Integer count = productIdCount.getOrDefault(device.productId, 0);
            productIdCount.put(device.productId, count + 1);
        }
        return productIdCount;
    }

    /*
     This is special case. Some products may have child products and
     thus we need to add child counters to such products.
     */
    private Map<Integer, Integer> attachChildCounter(Map<Integer, Integer> productIdCount) {
        Map<Integer, Integer> result = new HashMap<>(productIdCount);
        for (Map.Entry<Integer, Integer> entries : productIdCount.entrySet()) {
            Integer childProductId = entries.getKey();
            Product childProduct = getProductById(childProductId);
            if (childProduct != null) {
                Integer parentCounter = result.getOrDefault(childProduct.parentId, 0);
                result.put(childProduct.parentId, parentCounter + entries.getValue());
            }
        }
        return result;
    }

    public Collection<Device> getAllDevicesByOrgId(int orgId) {
        List<Device> result = new ArrayList<>();
        //getting org and all it child orgs
        int[] orgIds = orgListToIdList(getOrgsByParentId(orgId));
        for (Map.Entry<DeviceKey, Device> entry : deviceDao.devices.entrySet()) {
            DeviceKey key = entry.getKey();
            if (ArrayUtil.contains(orgIds, key.orgId)) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    private int[] orgListToIdList(List<Organization> orgs) {
        int[] ar = new int[orgs.size()];
        int i = 0;
        for (Organization org : orgs) {
            ar[i++] = org.id;
        }
        return ar;
    }

    private List<Organization> getOrgsByParentId(int parentId) {
        List<Organization> orgs = new ArrayList<>();
        Organization org = organizations.get(parentId);
        if (org != null) {
            orgs.add(org);
            getOrgsByParentId(orgs, parentId, 1);
        }
        return orgs;
    }

    private void getOrgsByParentId(List<Organization> orgs, int parentId, int invocationCounter) {
        if (invocationCounter == 50) {
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
        if (user.orgId == orgId) {
            return true;
        }
        if (user.isAdmin()) {
            //user is admin of parent org, so he can perform admin action on child org
            List<Organization> childOrgs = getOrgsByParentId(user.orgId);
            Organization org = getOrgById(childOrgs, orgId);
            return org != null;
        }
        return false;
    }

    public Collection<Organization> getAll() {
        return organizations.values();
    }

    private static Organization getOrgById(List<Organization> orgs, int id) {
        for (Organization org : orgs) {
            if (org.id == id) {
                return org;
            }
        }
        return null;
    }

    public Organization getOrgById(int id) {
        return organizations.get(id);
    }

    public Organization getOrgByIdOrThrow(int id) {
        Organization org = getOrgById(id);
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

    public void deleteProduct(Organization org, int productId) {
        if (deviceDao.productHasDevices(productId)) {
            log.error("You are not allowed to remove product with devices.");
            throw new ForbiddenWebException("You are not allowed to remove product with devices.");
        }
        org.deleteProduct(productId);
    }

    public boolean delete(int id) {
        Organization org = organizations.remove(id);
        if (org != null) {
            List<User> users = userDao.getAllUsersByOrgId(id);
            for (User user : users) {
                UserKey userKey = new UserKey(user.email, user.appName);
                userDao.delete(userKey);
                fileManager.delete(userKey);
            }
            fileManager.deleteOrg(id);
            return true;
        }
        return false;
    }

    public Product createProduct(int orgId, Product product) {
        Organization organization = getOrgByIdOrThrow(orgId);
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

    public Product getProductByIdOrThrow(int productId) {
        Product product = getProductById(productId);
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

    public Product getProductById(int productId) {
        for (Organization org : organizations.values()) {
            for (Product product : org.products) {
                if (product.id == productId) {
                    return product;
                }
            }
        }
        return null;
    }

    private Product getProductByParentIdOrNull(int newOrgId, int parentProductId) {
        for (Organization org : organizations.values()) {
            for (Product product : org.products) {
                if (org.id == newOrgId && product.parentId == parentProductId) {
                    return product;
                }
            }
        }
        return null;
    }

    public void verifyUserAccessToDevice(User user, Device device) {
        int orgId = getOrganizationIdByProductId(device.productId);

        if (!user.hasAccess(orgId)) {
            log.error("User {} tries to access device he has no access.", user.email);
            throw new ForbiddenWebException("You have no access to this device.");
        }
    }

}
