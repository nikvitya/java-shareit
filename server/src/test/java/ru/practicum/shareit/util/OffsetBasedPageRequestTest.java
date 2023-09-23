package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

class OffsetBasedPageRequestTest {

    @Test
    void getPageNumber() {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(15, 5);
        int pageNumber = offsetBasedPageRequest.getPageNumber();

        assertEquals(3, pageNumber, "Номер страницы не совпадает.");
    }

    @Test
    void getPageSize() {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(15, 5);
        int pageSize = offsetBasedPageRequest.getPageSize();

        assertEquals(5, pageSize);
    }

    @Test
    void getOffset() {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(15, 5);
        long offset = offsetBasedPageRequest.getOffset();

        assertEquals(15, offset);
    }

    @Test
    void getSort() {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(15, 5);
        Sort sort = offsetBasedPageRequest.getSort();

        assertEquals(Sort.unsorted(), sort);
    }

    @Test
    void next() {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(15, 5);
        Pageable next = offsetBasedPageRequest.next();

        assertEquals(new OffsetBasedPageRequest(20, 5), next);
    }

    @Test
    void previous() {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(15, 5);
        Pageable previous = offsetBasedPageRequest.previous();

        assertEquals(new OffsetBasedPageRequest(10, 5), previous);
    }

    @Test
    void previousOrFirst() {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(15, 5);
        Pageable previous = offsetBasedPageRequest.previousOrFirst();
        Pageable first = new OffsetBasedPageRequest(2, 5).previousOrFirst();

        assertEquals(new OffsetBasedPageRequest(10, 5), previous);
        assertEquals(new OffsetBasedPageRequest(0, 5), first);
    }

    @Test
    void first() {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(15, 5);
        Pageable first = offsetBasedPageRequest.first();

        assertEquals(new OffsetBasedPageRequest(0, 5), first);
    }

    @Test
    void hasPrevious() {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(15, 5);
        boolean hasPrevious = offsetBasedPageRequest.hasPrevious();

        assertEquals(true, hasPrevious);
    }

    @Test
    void testEquals() {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(15, 5);
        OffsetBasedPageRequest offsetBasedPageRequest2 = new OffsetBasedPageRequest(15, 5);
        assertEquals(offsetBasedPageRequest, offsetBasedPageRequest2);
        assertTrue(offsetBasedPageRequest instanceof OffsetBasedPageRequest);
        assertTrue(offsetBasedPageRequest2 instanceof OffsetBasedPageRequest);
        assertEquals(offsetBasedPageRequest.getPageSize(), offsetBasedPageRequest2.getPageSize());
        assertEquals(offsetBasedPageRequest.getOffset(), offsetBasedPageRequest2.getOffset());
        assertEquals(offsetBasedPageRequest.getSort(), offsetBasedPageRequest2.getSort());
    }

    @Test
    void testHashCode() {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(10, 5,
                Sort.by(Sort.Order.asc("name")));
        OffsetBasedPageRequest offsetBasedPageRequest2 = new OffsetBasedPageRequest(10, 5,
                Sort.by(Sort.Order.asc("name")));
        OffsetBasedPageRequest offsetBasedPageRequest3 = new OffsetBasedPageRequest(5, 5,
                Sort.by(Sort.Order.asc("name")));
        OffsetBasedPageRequest offsetBasedPageRequest4 = new OffsetBasedPageRequest(10, 10,
                Sort.by(Sort.Order.asc("name")));
        OffsetBasedPageRequest offsetBasedPageRequest5 = new OffsetBasedPageRequest(10, 5,
                Sort.by(Sort.Order.asc("description")));

        assertEquals(offsetBasedPageRequest.hashCode(), offsetBasedPageRequest2.hashCode());
        assertNotEquals(offsetBasedPageRequest.hashCode(), offsetBasedPageRequest3.hashCode());
        assertNotEquals(offsetBasedPageRequest.hashCode(), offsetBasedPageRequest4.hashCode());
        assertNotEquals(offsetBasedPageRequest.hashCode(), offsetBasedPageRequest5.hashCode());
    }

    @Test
    void testToString() {
        OffsetBasedPageRequest offsetBasedPageRequest = new OffsetBasedPageRequest(15, 5);
        String toString = offsetBasedPageRequest.toString();

        assertTrue(toString.endsWith("[limit=5,offset=15,sort=UNSORTED]"));
    }

    @Test
    void withPage() {
        Pageable withPage = new OffsetBasedPageRequest(0, 5).withPage(3);

        assertEquals(new OffsetBasedPageRequest(3 * 5, 5), withPage);
    }
}