package cc.blynk.server.core.model.web;

import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.dto.DeviceDTO;
import cc.blynk.server.core.model.dto.DeviceMobileDTO;
import cc.blynk.server.core.model.dto.OrganizationDTO;
import cc.blynk.server.core.model.exceptions.ProductNotFoundException;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.server.core.processors.rules.RuleGroup;
import cc.blynk.server.core.protocol.exceptions.JsonException;
import cc.blynk.utils.ArrayUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_INTS;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_PRODUCTS;
import static cc.blynk.utils.ArrayUtil.remove;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class Organization {

    private static final Logger log = LogManager.getLogger(Organization.class);

    public static final int NO_PARENT_ID = -1;

    public int id;

    public volatile String name;

    public volatile String description;

    public volatile boolean canCreateOrgs;

    public volatile boolean isActive;

    public volatile String tzName;

    public volatile String logoUrl;

    public volatile Unit unit;

    public volatile String primaryColor;

    public volatile String secondaryColor;

    public volatile long lastModifiedTs;

    public volatile Product[] products = EMPTY_PRODUCTS;

    public volatile int[] selectedProducts = EMPTY_INTS;

    public volatile int parentId = NO_PARENT_ID;

    public Role[] roles;

    public volatile RuleGroup ruleGroup;

    public Organization() {
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public Organization(String name, String tzName, String logoUrl,
                        boolean canCreateOrgs, int parentId, Role... roles) {
        this(name, tzName, logoUrl, canCreateOrgs, parentId, false, roles);
    }

    public Organization(String name, String tzName, String logoUrl,
                        boolean canCreateOrgs, int parentId, boolean isActive, Role... roles) {
        this();
        this.name = name;
        this.tzName = tzName;
        this.logoUrl = logoUrl;
        this.canCreateOrgs = canCreateOrgs;
        this.parentId = parentId;
        this.isActive = isActive;
        //todo, org should always have at least 1 role
        this.roles = roles == null ? new Role[0] : roles;
    }

    public String getLogoOrDefault() {
        if (this.logoUrl == null || this.logoUrl.isEmpty()) {
            return "/static/logo.png";
        }
        return this.logoUrl;
    }

    public void update(Organization updatedOrganization) {
        this.name = updatedOrganization.name;
        this.description = updatedOrganization.description;
        this.tzName = updatedOrganization.tzName;
        this.logoUrl = updatedOrganization.logoUrl;
        this.primaryColor = updatedOrganization.primaryColor;
        this.secondaryColor = updatedOrganization.secondaryColor;
        this.isActive = updatedOrganization.isActive;
        this.canCreateOrgs = updatedOrganization.canCreateOrgs;
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public void update(OrganizationDTO updatedOrganization) {
        this.name = updatedOrganization.name;
        this.description = updatedOrganization.description;
        this.tzName = updatedOrganization.tzName;
        this.logoUrl = updatedOrganization.logoUrl;
        this.primaryColor = updatedOrganization.primaryColor;
        this.secondaryColor = updatedOrganization.secondaryColor;
        this.isActive = updatedOrganization.isActive;
        this.canCreateOrgs = updatedOrganization.canCreateOrgs;
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public void addProduct(Product product) {
        this.products = ArrayUtil.add(products, product, Product.class);
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public boolean deleteProduct(int productId) {
        Product[] localProducts = this.products;
        for (int i = 0; i < localProducts.length; i++) {
            if (localProducts[i].id == productId) {
                this.products = remove(localProducts, i, Product.class);
                this.lastModifiedTs = System.currentTimeMillis();
                return true;
            }
        }
        return false;
    }

    public Product getFirstProduct() {
        if (products.length > 0) {
            return products[0];
        }
        throw new ProductNotFoundException("Organization is empty and without any product.");
    }

    public Product getProduct(int id)  {
        for (Product product : products) {
            if (product.id == id) {
                return product;
            }
        }
        return null;
    }

    public Product getProductOrThrow(int productId) {
        Product product = getProduct(productId);
        if (product == null) {
            throw new ProductNotFoundException("Product with passed id " + productId
                    + " not exists.");
        }
        return product;
    }

    public boolean isValidProductName(Product newProduct) {
        for (Product product : products) {
            if (product.id != newProduct.id && product.name.equalsIgnoreCase(newProduct.name)) {
                return false;
            }
        }
        return true;
    }

    public boolean isSubOrg() {
        return parentId > 0;
    }

    public boolean isChildOf(int parentId) {
        return this.parentId == parentId;
    }

    public boolean isUpdatedSince(long lastStart) {
        return lastStart <= this.lastModifiedTs || productsUpdated(lastStart);
    }

    private boolean productsUpdated(long lastStart) {
        for (Product product : products) {
            if (product.isUpdatedSince(lastStart)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmptyName() {
        return name == null || name.isEmpty();
    }

    public static Role[] createDefaultRoles(boolean withSuperAdmin) {
        if (withSuperAdmin) {
            return new Role[] {
                    new Role(Role.SUPER_ADMIN_ROLE_ID, "Super Admin", 0b11111111111111111111111111111111,
                            0b11111111111111111111111111111111),
                    new Role(1, "Admin", 0b11111111111111111111111111111111, 0),
                    new Role(2, "Staff", 0b11111111111111111111111111111111, 0),
                    new Role(3, "User", 0b11111111111111111111111111111111, 0)
            };
        } else {
            return new Role[] {
                    new Role(1, "Admin", 0b11111111111111111111111111111111, 0),
                    new Role(2, "Staff", 0b11111111111111111111111111111111, 0),
                    new Role(3, "User", 0b11111111111111111111111111111111, 0)
            };
        }
    }

    public Role getRoleById(int id) {
        for (Role role : roles) {
            if (role.id == id) {
                return role;
            }
        }
        return null;
    }

    //todo fix it. for now taking last one
    public int getDefaultRoleId() {
        Role lastRole = roles[roles.length - 1];
        return lastRole == null ? 1 : lastRole.id;
    }

    public Product getProductByTemplateId(String templateId) {
        for (Product product : this.products) {
            if (product.containsTemplateId(templateId)) {
                return product;
            }
        }
        return null;
    }

    public void eraseOwner(String oldOwner) {
        reassignOwner(oldOwner, null);
    }

    private void reassignOwner(String oldOwner, String newOwner) {
        for (Product product : this.products) {
            for (Device device : product.devices) {
                if (device.reassignOwner(oldOwner, newOwner)) {
                    log.trace("Device owner {} of device {} is reassigned to {}.", oldOwner, device.id, newOwner);
                }
            }
        }
    }

    public List<Device> getAllDevices() {
        var result = new ArrayList<Device>();
        for (Product product : products) {
            result.addAll(Arrays.asList(product.devices));
        }
        return result;
    }

    public List<Device> getDevicesByOwner(String ownerEmail) {
        var result = new ArrayList<Device>();
        for (Product product : products) {
            for (Device device : product.devices) {
                if (device.hasOwner(ownerEmail)) {
                    result.add(device);
                }
            }
        }
        return result;
    }

    public List<DeviceMobileDTO> getAllMobileDeviceDTOs() {
        var result = new ArrayList<DeviceMobileDTO>();
        for (Product product : products) {
            for (Device device : product.devices) {
                DeviceMobileDTO deviceDTO = new DeviceMobileDTO(device, product, null);
                result.add(deviceDTO);
            }
        }
        return result;
    }

    public List<DeviceDTO> getAllDeviceDTOs(boolean needMeta) {
        var result = new ArrayList<DeviceDTO>();
        for (Product product : products) {
            for (Device device : product.devices) {
                DeviceDTO deviceDTO;
                if (needMeta) {
                    deviceDTO = new DeviceDTO(device, product, name);
                } else {
                    deviceDTO = new DeviceDTO(device, product, name, null);
                }
                result.add(deviceDTO);
            }
        }
        return result;
    }

    public List<DeviceMobileDTO> getDevicesByOwnerMobileDTOs(String ownerEmail) {
        var result = new ArrayList<DeviceMobileDTO>();
        for (Product product : products) {
            for (Device device : product.devices) {
                if (device.hasOwner(ownerEmail)) {
                    DeviceMobileDTO deviceMobileDTO = new DeviceMobileDTO(device, product, null);
                    result.add(deviceMobileDTO);
                }
            }
        }
        return result;
    }

    public List<DeviceDTO> getDevicesByOwnerDTOs(String ownerEmail) {
        var result = new ArrayList<DeviceDTO>();
        for (Product product : products) {
            for (Device device : product.devices) {
                if (device.hasOwner(ownerEmail)) {
                    result.add(new DeviceDTO(device, product, name));
                }
            }
        }
        return result;
    }

    public Role getRoleByIdOrThrow(int id) {
        Role role = getRoleById(id);
        if (role == null) {
            throw new JsonException("Role with passed id not found.");
        }
        return role;
    }

    private int getRoleIndexOrThrow(int id) {
        Role[] roles = this.roles;
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].id == id) {
                return i;
            }
        }
        throw new JsonException("Cannot find role with passed id.");
    }

    public int getIdForRole() {
        int max = 0;
        for (Role role : roles) {
            max = Math.max(max, role.id);
        }
        return max + 1;
    }

    public void addRole(Role role) {
        this.roles = ArrayUtil.add(this.roles, role, Role.class);
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public void updateRole(Role updatedRole) {
        int index = getRoleIndexOrThrow(updatedRole.id);
        this.roles = ArrayUtil.copyAndReplace(this.roles, updatedRole, index);
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public void deleteRole(int roleId) {
        int index = getRoleIndexOrThrow(roleId);
        this.roles = ArrayUtil.remove(this.roles, index, Role.class);
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public Product getProductByName(String name) {
        for (Product product : products) {
            if (name.equals(product.name)) {
                return product;
            }
        }
        return null;
    }

    public int deviceCount() {
        int count = 0;
        for (Product product : products) {
            count += product.devices.length;
        }
        return count;
    }

    public Product getSubProductOf(int parentProductId) {
        for (Product product : products) {
            if (product.parentId == parentProductId) {
                return product;
            }
        }
        return null;
    }

    public void setRuleGroup(RuleGroup ruleGroup) {
        this.ruleGroup = ruleGroup;
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public Product findProductByIdOrParentId( int productId) {
        for (Product product : this.products) {
            if (product.id == productId || product.parentId == productId) {
                return product;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Organization that = (Organization) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
