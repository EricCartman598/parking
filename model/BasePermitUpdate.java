package com.epam.parking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public abstract class BasePermitUpdate extends SoftDeleteEntity {

    @Column(name = "updated_at", columnDefinition = "timestamp DEFAULT current_timestamp")
    private Date updatedAt;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "permit_type_id", nullable = false)
    private PermitType permitType;

    @ManyToOne
    @JoinColumn(name = "leave_reason_id")
    private LeaveReason leaveReason;
}
