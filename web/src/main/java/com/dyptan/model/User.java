package com.dyptan.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Component
@Table(name = "Users")
public class User {
    @Id
    private String username;
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Filter> filters = new ArrayList<>();
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role.Roles> roles = new HashSet();

    public User() {
    }

    public User(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.roles = user.getRoles();
        this.filters = user.getFilters();
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public void addRole(Role.Roles role) {
        roles.add(role);
    }
    public void deleteFilter(int id) {
        filters.remove(id);
    }

}
