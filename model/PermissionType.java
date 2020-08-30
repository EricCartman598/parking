package com.epam.parking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "permission_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionType extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String title;

    @OneToMany(mappedBy = "permissionType", fetch = FetchType.EAGER)
    private List<Permission> permissions;
}
