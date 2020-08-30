package com.epam.parking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "permissions", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_permissions_manager_office",
                columnNames = {"manager_id", "office_id"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "permission_type_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_permissions_permission_type_id"))
    private PermissionType permissionType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "manager_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_permissions_manager_id"))
    private Manager manager;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_permissions_office_id"))
    private Office office;
}
