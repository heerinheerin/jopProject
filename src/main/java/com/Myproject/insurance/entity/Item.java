package com.Myproject.insurance.entity;

import com.Myproject.insurance.constant.Category;
import com.Myproject.insurance.constant.ItemSellStatus;
import com.Myproject.insurance.constant.Nature;
import com.Myproject.insurance.constant.Region;
import com.Myproject.insurance.dto.ItemFormDto;
import com.Myproject.insurance.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // 상품코드

    @Column(nullable = false,length = 50)
    private String itemNm; // 패키지명

    @Column(name = "price", nullable = false)
    private int price; // 가격

    @Column(nullable = false)
    private int stockNumber; // 인원

    @Lob
    @Column(nullable = false)
    private String itemDetail; // 상품상세설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; // 상품판매 상태

    @Column(nullable = false)
    private LocalDate startDate; // 출발 날짜

    @Column(nullable = false)
    private LocalDate endDate; // 도착 날짜

    @Enumerated(EnumType.STRING)
    private Nature nature; // 국내 해외

    @Enumerated(EnumType.STRING)
    private Category category; // 지역

    @Enumerated(EnumType.STRING)
    private Region region; // 지역


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "member_item",
            joinColumns =  @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Member> member;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemImg> itemImgs; // itemImg 엔티티와의 일대다 관계


    @Column(columnDefinition = "TEXT")
    private String itemLink;

    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        this.category= itemFormDto.getCategory();
        this.nature = itemFormDto.getNature();
        this.region = itemFormDto.getRegion();
    }

    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber; // 10, 5 / 10, 20
        if (restStock<0) {
            throw new OutOfStockException("현재 선택하신 상품의 가용인원이 부족합니다.(현재 상품 구매 가능인원: "+this.stockNumber+")");
        }
        this.stockNumber = restStock; // 5
    }


    public void addStock(int stockNumber) {this.stockNumber += stockNumber;}

}
