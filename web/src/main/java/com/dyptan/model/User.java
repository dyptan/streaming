package com.dyptan.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;
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
    private Set<Role> roles;

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
    public void deleteFilter(int id) {
        filters.remove(id);
    }

}
