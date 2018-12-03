package cc.blynk.server.core.dao;

import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.OrganizationDTO;
import cc.blynk.server.core.model.exceptions.ForbiddenWebException;
import cc.blynk.server.core.model.exceptions.OrgNotFoundException;
import cc.blynk.server.core.model.exceptions.ProductNotFoundException;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.web.Organization;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.utils.IntArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static cc.blynk.server.core.model.web.Organization.NO_PARENT_ID;

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
    private final UserDao userDao;

    public OrganizationDao(FileManager fileManager, UserDao userDao) {
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
        this.userDao = userDao;
        log.info("Organization sequence number is {}", largestOrgSequenceNumber);
    }

    public Role getRole(int orgId, int roleId) {
        Organization org = getOrgByIdOrThrow(orgId);
        return org.getRoleByIdOrThrow(roleId);
    }

    /**
     * Super org is initially created organization
     */
    public Organization getSuperOrgOrThrow() {
        for (Organization org : organizations.values()) {
            if (org.parentId == NO_PARENT_ID) {
                return org;
            }
        }
        throw new RuntimeException("Super organization is missing! Should never happen!");
    }

    public Organization create(Organization organization) {
        if (checkNameExists(-1, organization.name)) {
            throw new ForbiddenWebException("Organization with this name already exists.");
        }
        organization.id = orgSequence.incrementAndGet();
        organizations.putIfAbsent(organization.id, organization);
        return organization;
    }

    public boolean checkNameExists(int orgId, String name) {
        for (Organization org : organizations.values()) {
            if (org.id != orgId && name.equalsIgnoreCase(org.name)) {
                return true;
            }
        }
        return false;
    }

    public Product getProductOrThrow(int orgId, int productId) {
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

    public int[] subProductIds(int parentOrgId, int parentProductId) {
        IntArray subProductIds = new IntArray();
        for (Organization org : organizations.values()) {
            if (org.parentId == parentOrgId) {
                for (Product subProduct : org.products) {
                    if (subProduct.parentId == parentProductId) {
                        subProductIds.add(subProduct.id);
                    }
                }
            }
        }
        return subProductIds.toArray();
    }

    public Collection<Device> getAllDevicesByOrgId(int orgId) {
        List<Device> result = new ArrayList<>();
        //getting org and all it child orgs
        int[] orgIds = orgListToIdList(getOrgChilds(orgId));
        for (int tmpOrgId : orgIds) {
            Organization org = organizations.get(tmpOrgId);
            for (Product product : org.products) {
                result.addAll(Arrays.asList(product.devices));
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

    public List<OrganizationDTO> getFirstLevelChilds(int orgId) {
        List<OrganizationDTO> result = new ArrayList<>();
        for (Organization org : organizations.values()) {
            if (org.parentId == orgId) {
                result.add(new OrganizationDTO(org));
            }
        }
        return result;
    }

    public List<Organization> getOrgChilds(int orgId) {
        List<Organization> orgs = new ArrayList<>();
        Organization org = organizations.get(orgId);
        if (org != null) {
            orgs.add(org);
            getOrgChilds(orgs, orgId, 1);
        }
        return orgs;
    }

    private void getOrgChilds(List<Organization> orgs, int parentId, int invocationCounter) {
        if (invocationCounter == 1000) {
            throw new RuntimeException("Error finding organization.");
        }
        for (Organization org : organizations.values()) {
            if (org.parentId == parentId) {
                orgs.add(org);
                getOrgChilds(orgs, org.id, invocationCounter++);
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
        Organization userOrg = getOrgById(user.orgId);
        Role userRole = userOrg.getRoleById(user.roleId);
        if (userRole.canViewOrg()) {
            //user is admin of parent org, so he can perform admin action on child org
            List<Organization> childOrgs = getOrgChilds(user.orgId);
            Organization org = getOrgById(childOrgs, orgId);
            return org != null;
        }
        return false;
    }

    public Collection<Organization> getAll() {
        return organizations.values();
    }

    public Product assignToOrgAndAddDevice(int orgId, Device newDevice) {
        Organization org = getOrgByIdOrThrow(orgId);
        return assignToOrgAndAddDevice(org, newDevice);
    }

    public Product assignToOrgAndAddDevice(Organization org, Device newDevice) {
        //todo temp solution
        Product product;
        if (newDevice.productId == -1) {
            log.warn("Using random product for device {}.", newDevice.id);
            product = org.getFirstProduct();
            newDevice.productId = product.id;
        } else {
            product = org.getProduct(newDevice.productId);
        }

        if (product == null) {
            log.error("Product for new device with id {} not exists for orgId {}.", newDevice.productId, org.id);
            throw new ProductNotFoundException("Product with passed id not exists.");
        }

        newDevice.metaFields = product.copyMetaFields();
        newDevice.webDashboard = product.webDashboard.copy();
        product.addDevice(newDevice);
        return product;
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

    public boolean delete(int orgId) {
        List<User> users = userDao.getAllUsersByOrgId(orgId);
        for (User user : users) {
            userDao.delete(user.email);
            fileManager.delete(user.email);
        }
        Organization org = organizations.remove(orgId);
        if (org != null) {
            fileManager.deleteOrg(orgId);
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

    public String getParentOrgName(int parentOrgId) {
        if (parentOrgId != NO_PARENT_ID) {
            Organization parentOrg = getOrgById(parentOrgId);
            if (parentOrg != null && parentOrg.name != null) {
                return parentOrg.name;
            }
        }
        return null;
    }

}
