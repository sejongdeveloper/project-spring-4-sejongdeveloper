package com.codesoom.sejongdeveloper.domain;

import lombok.AccessLevel;
import lombok.Builder;
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
@Table(name = "obtain_order_detail")    //수주 상세
public class ObtainOrderDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "obtain_order_detail_id")
    private Long id;    //수주 상세 일련번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obtain_order_id")
    private ObtainOrder obtainOrder;    //수주

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;  //상품

    private BigDecimal quantity;    //수주 수량

    @Builder
    public ObtainOrderDetail(Long id, ObtainOrder obtainOrder, Item item, BigDecimal quantity) {
        this.id = id;
        this.obtainOrder = obtainOrder;
        this.item = item;
        this.quantity = quantity;
    }

    public void update(Item item, BigDecimal quantity) {
        this.item = item;
        this.quantity = quantity;
    }
}
