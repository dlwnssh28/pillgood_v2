package com.pillgood.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "billings")
@Getter
@Setter
@NoArgsConstructor
public class Billing {

    @Id
    @Column(name = "billing_key", length = 32)
    private String billingKey;

    @Column(name = "member_unique_id", length = 32)
    private String memberUniqueId;

    @ManyToOne
    @JoinColumn(name = "member_unique_id", referencedColumnName = "member_unique_id", insertable = false, updatable = false)
    private Member member;
}
