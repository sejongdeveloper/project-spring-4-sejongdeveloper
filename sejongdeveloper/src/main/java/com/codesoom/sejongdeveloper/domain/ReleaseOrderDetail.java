package com.codesoom.sejongdeveloper.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "release_order_detail")   //출고 상세
public class ReleaseOrderDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "release_order_detail_id")
    private Long id;    //출고 상세 일련번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_order_id")
    private ReleaseOrder releaseOrder;  //출고

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obtain_order_detail_id")
    private ObtainOrderDetail obtainOrderDetail;    //수주 상세

    private BigDecimal quantity;    //출고수량

}