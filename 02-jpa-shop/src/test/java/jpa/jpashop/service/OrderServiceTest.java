package jpa.jpashop.service;

import jakarta.persistence.EntityManager;
import jpa.jpashop.domain.Address;
import jpa.jpashop.domain.Member;
import jpa.jpashop.domain.Order;
import jpa.jpashop.domain.OrderStatus;
import jpa.jpashop.domain.item.Book;
import jpa.jpashop.domain.item.Item;
import jpa.jpashop.exception.NotEnoughStockException;
import jpa.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 상품주문() throws Exception {
        // given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);
        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findOne(orderId);
        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(getOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(getOrder.getTotalPrice()).isEqualTo(10000 * orderCount);
        assertThat(book.getStockQuantity()).isEqualTo(8);
    }


    @Test
    public void 상품주문_재고수량초과() throws Exception {
        // given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 11;

        // when
        assertThatThrownBy(() -> orderService.order(member.getId(), item.getId(), orderCount))
            .isInstanceOf(NotEnoughStockException.class);
    }

    @Test
    public void 주문_취소() throws Exception{
        // given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);
        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(item.getStockQuantity()).isEqualTo(10);
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가" ,"123-123"));
        em.persist(member);
        return member;
    }

}