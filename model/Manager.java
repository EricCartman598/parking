package com.epam.parking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "managers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Manager extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Permission> permissions;
}
