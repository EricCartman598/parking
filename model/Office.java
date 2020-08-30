package com.epam.parking.model;

import lombok.*;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "offices")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Office extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String title;

    @OneToMany(mappedBy = "office", cascade = CascadeType.ALL)
    private List<Permission> permissions;

    @OneToMany(mappedBy = "office", cascade = CascadeType.ALL)
    private List<Location> locations;

    @OneToMany(mappedBy = "office", cascade = CascadeType.ALL)
    private List<QueuePriority> queuePriorities;
}
