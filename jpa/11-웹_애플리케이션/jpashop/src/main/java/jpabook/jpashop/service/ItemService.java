package jpabook.jpashop.service;

import jakarta.transaction.Transactional;
import jpabook.jpashop.entity.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ItemService {
    @Autowired
    ItemRepository itemRepository;

    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findOne(Long id) {
        return itemRepository.findOne(id);
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
