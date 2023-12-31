package ru.practicum.shareit.item.entity;

import lombok.*;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Table(name = "items")
@Accessors(chain = true)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    @ToString.Exclude
    private List<Booking> bookings;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    @ToString.Exclude
    private List<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request;
}