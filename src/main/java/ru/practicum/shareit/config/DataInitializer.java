package ru.practicum.shareit.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        log.info("Initializing test data...");

        User owner = createUser("Item Owner", "owner@example.com");
        User booker = createUser("Booker User", "booker@example.com");
        User wrongUser = createUser("Wrong User", "wrong@example.com");

        log.info("Created users: ownerId={}, bookerId={}, wrongUserId={}",
                owner.getId(), booker.getId(), wrongUser.getId());

        Item item = createItem("Test Item", "Test Description", true, owner);
        log.info("Created item: id={}, ownerId={}", item.getId(), item.getOwner().getId());

        Booking booking = createBooking(item, booker, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        log.info("Created booking: id={}, itemId={}, bookerId={}",
                booking.getId(), booking.getItem().getId(), booking.getBooker().getId());

        log.info("Test data initialized successfully");
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private Booking createBooking(Item item, User booker, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }
}