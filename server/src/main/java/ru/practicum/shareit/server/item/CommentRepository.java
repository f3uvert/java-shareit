package ru.practicum.shareit.server.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;


import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemIdOrderByCreatedDesc(Long itemId);

    List<Comment> findByItemInOrderByCreatedDesc(List<Item> items);

    List<Comment> findByItemIdInOrderByCreatedDesc(List<Long> itemIds);
}