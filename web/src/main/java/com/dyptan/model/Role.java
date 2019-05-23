package com.dyptan.model;

import lombok.Data;

import javax.persistence.*;

enum ROLES {
    ADMIN,
    USER
}

@Data
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private int roleId;

    @Column(name = "role")
    private String role;

    public Role() {
    }

}
