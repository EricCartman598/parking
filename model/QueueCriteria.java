package com.epam.parking.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "queue_criterias")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class QueueCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    String name;

    @NonNull
    String displayName;
}
