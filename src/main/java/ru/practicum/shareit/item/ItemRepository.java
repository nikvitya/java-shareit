package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select new ru.practicum.shareit.item.dto.ItemResponse(i.id, i.name, i.description, " +
            "i.available, i.request.id) " +
            "from Item as i " +
            "where i.available = true " +
            "and (lower(i.name) like lower(concat('%', :text, '%')) " +
            "or lower(i.description) like lower(concat('%', :text, '%')))")
    Page<ItemResponse> searchAvailableItemsByText(String text, Pageable page);

    @Query(value = "select i " +
            "from Item as i " +
            "join fetch i.owner as ow " +
            "left join fetch i.comments " +
            "where ow.id=:ownerId",
             countQuery = "select count(i) from Item as i where i.owner.id=:ownerId")
    Page<Item> findByOwnerId(@Param("ownerId") long ownerId, Pageable page);

}
