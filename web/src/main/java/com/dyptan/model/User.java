package com.dyptan.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public List<Filter> getFilters() {
        return this.filters;
    }

    public Set<Role.Roles> getRoles() {
        return this.roles;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public void setRoles(Set<Role.Roles> roles) {
        this.roles = roles;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof User)) return false;
        final User other = (User) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$username = this.getUsername();
        final Object other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) return false;
        final Object this$password = this.getPassword();
        final Object other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) return false;
        final Object this$filters = this.getFilters();
        final Object other$filters = other.getFilters();
        if (this$filters == null ? other$filters != null : !this$filters.equals(other$filters)) return false;
        final Object this$roles = this.getRoles();
        final Object other$roles = other.getRoles();
        if (this$roles == null ? other$roles != null : !this$roles.equals(other$roles)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof User;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $username = this.getUsername();
        result = result * PRIME + ($username == null ? 43 : $username.hashCode());
        final Object $password = this.getPassword();
        result = result * PRIME + ($password == null ? 43 : $password.hashCode());
        final Object $filters = this.getFilters();
        result = result * PRIME + ($filters == null ? 43 : $filters.hashCode());
        final Object $roles = this.getRoles();
        result = result * PRIME + ($roles == null ? 43 : $roles.hashCode());
        return result;
    }

    public String toString() {
        return "User(username=" + this.getUsername() + ", password=" + this.getPassword() + ", filters=" + this.getFilters() + ", roles=" + this.getRoles() + ")";
    }

    @Transient
    public static class AuthDetails extends User implements UserDetails {

        public AuthDetails(User user) {
            super(user);
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getValue()))
                    .collect(Collectors.toList());
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }


}
