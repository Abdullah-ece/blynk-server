package cc.blynk.server.core.model.web;

import cc.blynk.server.core.model.web.product.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 04.04.17.
 */
public class Organization {

    public int id;

    public String name;

    public String tzName;

    public String logoUrl;

    public int color;

    public List<Product> products = new ArrayList<>();

    public Organization() {
    }

    public Organization(String name, String tzName) {
        this.name = name;
        this.tzName = tzName;
    }
}
