package jpa.jpashop.api;

import jpa.jpashop.domain.Address;
import jpa.jpashop.domain.Order;
import jpa.jpashop.domain.OrderItem;
import jpa.jpashop.domain.OrderSearch;
import jpa.jpashop.domain.OrderStatus;
import jpa.jpashop.repository.OrderRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    /**
     * V1. 엔티티 직접 노출
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());

        for(Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }

        return all;
    }

    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;    //주문시간
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;  //Dto 변환 필수!!

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();

            // 프록시 초기화
            orderItems = order.getOrderItems().stream()
                .map(orderItem -> new OrderItemDto(orderItem))
                .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto {
        private String itemName;    //상품명
        private int orderPrice; //주문 가격
        private int count;  //주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

    /**
     * V2. 엔티티를 DTO로 변환
     * - 지연 로딩으로 쿼리가 너무 많이 실행됨
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        List<OrderDto> result = orders.stream()
            .map(o -> new OrderDto(o))
            .collect(toList());

        return result;
    }

    /**
     * V3. 페치 조인 최적화
     * - RDS에서 조인할 때 행이 많은 쪽에 맞춰 결과 출력 -> 중복 데이터가 발생
     * - distinct 키워드를 사용해서 중복 데이터를 제거
     * - 페치 조인으로 쿼리는 한 번만 실행
     *
     * - 컬렉션 페치 조인을 사용하면 페이징이 불가능
     * - 데이터가 커지면 out of memory 예외가 발생할 수 있어 매우 치명적
     * - 컬렉션 페치 조인은 1개만 사용 가능
     * */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
            .map(o -> new OrderDto(o))
            .collect(toList());

        return result;
    }


    /**
     * V3-1.
     *
     * 대부분의 페이징 + 컬렉션 엔티티 조회 문제는 이 방법으로 해결 가능
     * - xxxToOne(OneToOne, ManyToOne) 관계를 모두 페치조인
     * - 컬렉션은 지연 로딩으로 조회
     * - 지연 로딩 최적화를 위한 옵션 적용
     *   - hibernate.default_batch_fetch_size, 글로벌 설정
     *   - @BatchSize, 개별 최적화
     *   - 컬렉션이나 프록시 객체를 설정한 size 만큼 IN 쿼리로 조회
     * */
    @GetMapping("/api/v3-1/orders")
    public List<OrderDto> ordersV3_page(
        @RequestParam(value = "offset", defaultValue = "0") int offset,
        @RequestParam(value = "limit", defaultValue = "100") int limit)
    {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> result = orders.stream()
            .map(o -> new OrderDto(o))
            .collect(toList());

        return result;
    }
}
