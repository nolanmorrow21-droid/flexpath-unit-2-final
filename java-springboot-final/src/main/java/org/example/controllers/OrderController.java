package org.example.controllers;

import org.example.daos.OrderDao;
import org.example.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for orders.
 * This class is responsible for handling all HTTP requests related to orders.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/orders")
@PreAuthorize("isAuthenticated()")
public class OrderController {
    /**
     * The order data access object.
     */
    @Autowired
    private OrderDao orderDao;

    /**
     * Gets all orders, optionally filtered by username.
     *
     * @param username Optional username to filter by.
     * @return A list of orders.
     */
    @GetMapping
    public List<Order> getAll(@RequestParam(required = false) String username) {
        if (username != null) {
            return orderDao.getByUsername(username);
        }
        return orderDao.getAll();
    }

    /**
     * Gets an order by id.
     *
     * @param id The id of the order.
     * @return The order with the given id.
     */
    @GetMapping(path = "/{id}")
    public Order get(@PathVariable int id) {
        Order order = orderDao.getById(id);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        return order;
    }

    /**
     * Creates a new order.
     *
     * @param order The order to create.
     * @return The created order.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Order create(@RequestBody Order order) {
        return orderDao.create(order);
    }

    /**
     * Updates an order.
     *
     * @param id The id of the order.
     * @param order The new order data.
     * @return The updated order.
     */
    @PutMapping(path = "/{id}")
    public Order update(@PathVariable int id, @RequestBody Order order) {
        if (orderDao.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        order.setId(id);
        return orderDao.update(order);
    }

    /**
     * Deletes an order.
     *
     * @param id The id of the order.
     * @return The number of rows affected.
     */
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable int id) {
        int rowsAffected = orderDao.delete(id);
        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        return rowsAffected;
    }
}
