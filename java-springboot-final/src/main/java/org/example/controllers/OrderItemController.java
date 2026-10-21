package org.example.controllers;

import org.example.daos.OrderItemDao;
import org.example.models.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for order items.
 * This class is responsible for handling all HTTP requests related to order items.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/order-items")
@PreAuthorize("isAuthenticated()")
public class OrderItemController {
    /**
     * The order item data access object.
     */
    @Autowired
    private OrderItemDao orderItemDao;

    /**
     * Gets all order items, optionally filtered by order id.
     *
     * @param orderId Optional order id to filter by.
     * @return A list of order items.
     */
    @GetMapping
    public List<OrderItem> getAll(@RequestParam(required = false) Integer orderId) {
        if (orderId != null) {
            return orderItemDao.getByOrderId(orderId);
        }
        return orderItemDao.getAll();
    }

    /**
     * Gets an order item by id.
     *
     * @param id The id of the order item.
     * @return The order item with the given id.
     */
    @GetMapping(path = "/{id}")
    public OrderItem get(@PathVariable int id) {
        OrderItem orderItem = orderItemDao.getById(id);
        if (orderItem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found");
        }
        return orderItem;
    }

    /**
     * Creates a new order item.
     *
     * @param orderItem The order item to create.
     * @return The created order item.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderItem create(@RequestBody OrderItem orderItem) {
        return orderItemDao.create(orderItem);
    }

    /**
     * Updates an order item.
     *
     * @param id The id of the order item.
     * @param orderItem The new order item data.
     * @return The updated order item.
     */
    @PutMapping(path = "/{id}")
    public OrderItem update(@PathVariable int id, @RequestBody OrderItem orderItem) {
        if (orderItemDao.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found");
        }
        orderItem.setId(id);
        return orderItemDao.update(orderItem);
    }

    /**
     * Deletes an order item.
     *
     * @param id The id of the order item.
     * @return The number of rows affected.
     */
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable int id) {
        int rowsAffected = orderItemDao.delete(id);
        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found");
        }
        return rowsAffected;
    }
}
