package com.epam.parking.model;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "queue_priorities", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"office_id", "criteria_id"}),
        @UniqueConstraint(columnNames = {"office_id", "priority"})
})
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class QueuePriority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "criteria_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_queue_criterias_criteria_id"))
    private QueueCriteria criteria;

    @NonNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "office_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_offices_office_id"))
    private Office office;

    @NonNull
    private int priority;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", columnDefinition = "timestamp DEFAULT current_timestamp")
    private Date updatedAt;
}
