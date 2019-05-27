package com.dyptan.model;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Table;

@Data
@Embeddable
@Table(name = "Roles")
public class Role {

    private Roles role;

    public Role() {
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
