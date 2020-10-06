package com.dyptan.model;

import javax.persistence.Embeddable;
import javax.persistence.Table;

@Embeddable
@Table(name = "Roles")
public class Role {

    private Roles role;

    public Role() {
    }

    public Roles getRole() {
        return this.role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Role)) return false;
        final Role other = (Role) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$role = this.getRole();
        final Object other$role = other.getRole();
        if (this$role == null ? other$role != null : !this$role.equals(other$role)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Role;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $role = this.getRole();
        result = result * PRIME + ($role == null ? 43 : $role.hashCode());
        return result;
    }

    public String toString() {
        return "Role(role=" + this.getRole() + ")";
    }

    public enum Roles {
        ADMIN("ADMIN"),
        USER("USER");

        private final String value;

        Roles(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
