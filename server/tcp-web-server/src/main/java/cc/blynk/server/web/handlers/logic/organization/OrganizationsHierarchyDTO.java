package cc.blynk.server.web.handlers.logic.organization;

import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.web.Organization;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.TreeSet;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 16.11.18.
 */
public final class OrganizationsHierarchyDTO implements Comparable<OrganizationsHierarchyDTO> {

    public final int id;

    public final String name;

    public final TreeSet<OrganizationsHierarchyDTO> childs;

    public OrganizationsHierarchyDTO(Organization org, TreeSet<OrganizationsHierarchyDTO> childs) {
        this(org.id, org.name, childs);
    }

    @JsonCreator
    public OrganizationsHierarchyDTO(@JsonProperty("id") int id,
                                     @JsonProperty("name") String name,
                                     @JsonProperty("childs") TreeSet<OrganizationsHierarchyDTO> childs) {
        this.id = id;
        this.name = name;
        this.childs = childs;
    }

    @Override
    public int compareTo(OrganizationsHierarchyDTO o) {
        return name == null ? -1 : name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
