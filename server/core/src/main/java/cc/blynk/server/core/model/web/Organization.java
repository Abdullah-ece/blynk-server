package cc.blynk.server.core.model.web;

import cc.blynk.server.core.model.exceptions.ProductNotFoundException;
import cc.blynk.server.core.model.permissions.Role;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.product.Product;
import cc.blynk.utils.ArrayUtil;

import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_INTS;
import static cc.blynk.server.internal.EmptyArraysUtil.EMPTY_PRODUCTS;
import static cc.blynk.utils.ArrayUtil.remove;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class Organization {

    public static final int SUPER_ORG_PARENT_ID = -1;

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

    public volatile int parentId = SUPER_ORG_PARENT_ID;

    public Role[] roles;

    public Organization() {
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public Organization(String name, String tzName, String logoUrl,
                        boolean canCreateOrgs, int parentId, Role... roles) {
        this();
        this.name = name;
        this.tzName = tzName;
        this.logoUrl = logoUrl;
        this.canCreateOrgs = canCreateOrgs;
        this.parentId = parentId;
        //todo, org should always have at least 1 role
        this.roles = roles == null ? new Role[0] : roles;
    }

    public void update(Organization updatedOrganization) {
        this.name = updatedOrganization.name;
        this.description = updatedOrganization.description;
        this.tzName = updatedOrganization.tzName;
        this.logoUrl = updatedOrganization.logoUrl;
        this.primaryColor = updatedOrganization.primaryColor;
        this.secondaryColor = updatedOrganization.secondaryColor;
        this.products = updatedOrganization.products;
        this.isActive = updatedOrganization.isActive;
        this.canCreateOrgs = updatedOrganization.canCreateOrgs;
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public void addProduct(Product product) {
        this.products = ArrayUtil.add(products, product, Product.class);
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public boolean deleteProduct(int productId) {
        for (int i = 0; i < products.length; i++) {
            if (products[i].id == productId) {
                products = remove(products, i, Product.class);
                lastModifiedTs = System.currentTimeMillis();
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
                    + " not found.");
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

    public boolean isUpdated(long lastStart) {
        return lastStart <= lastModifiedTs || productUpdated(lastStart);
    }

    private boolean productUpdated(long lastStart) {
        for (Product product : products) {
            if (lastStart <= product.lastModifiedTs) {
                return true;
            }
        }
        return false;
    }

    public boolean hasParentOrg() {
        return parentId != SUPER_ORG_PARENT_ID;
    }

    public boolean isEmptyName() {
        return name == null || name.isEmpty();
    }

    public Role getRoleById(int id) {
        for (Role role : roles) {
            if (role.id == id) {
                return role;
            }
        }
        throw new RuntimeException("Role with passed id not found.");
    }

    //todo fix it. for now taking last one
    public int getDefaultRoleId() {
        Role lastRole = roles[roles.length - 1];
        return lastRole == null ? 1 : lastRole.id;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
