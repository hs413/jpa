package jpa.jpashop.service;

import jpa.jpashop.domain.item.Book;
import jpa.jpashop.domain.item.Item;
import jpa.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)  // 조회 시 성능 최적화
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItem() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    /**
     * == 변경 감지를 사용한 업데이트 ==
     * 영속성 엔티티로 조회한 뒤 변경 감지 기능을 사용 한다.
     * 변경 감지는 커밋 시점 실행 된다.
    */
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);

//        findItem.setName(name);
//        findItem.setPrice(price);
//        findItem.setStockQuantity(stockQuantity);

        /**
         * change 메서드를 사용 하도록 refactoring
         * setter는 가급적 사용 X
         * */
        findItem.change(name, price, stockQuantity);
    }
}
