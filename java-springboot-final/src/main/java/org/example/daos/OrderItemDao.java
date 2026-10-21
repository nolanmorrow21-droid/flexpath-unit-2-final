package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.OrderItem;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Data access object for order items.
 */
@Component
public class OrderItemDao {
    /**
     * The JDBC template for querying the database.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates a new order item data access object.
     *
     * @param dataSource The data source for the DAO.
     */
    public OrderItemDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all order items.
     *
     * @return List of OrderItem
     */
    public List<OrderItem> getAll() {
        return jdbcTemplate.query("SELECT * FROM order_items ORDER BY id;", this::mapToOrderItem);
    }

    /**
     * Gets all order items belonging to an order.
     *
     * @param orderId The order id to filter by.
     * @return List of OrderItem
     */
    public List<OrderItem> getByOrderId(int orderId) {
        return jdbcTemplate.query("SELECT * FROM order_items WHERE order_id = ? ORDER BY id;", this::mapToOrderItem, orderId);
    }

    /**
     * Gets an order item by id.
     *
     * @param id The id of the order item.
     * @return OrderItem, or null if not found.
     */
    public OrderItem getById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM order_items WHERE id = ?;", this::mapToOrderItem, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new order item.
     *
     * @param orderItem The order item to create.
     * @return OrderItem The created order item.
     */
    public OrderItem create(OrderItem orderItem) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, orderItem.getOrderId());
            ps.setInt(2, orderItem.getProductId());
            ps.setInt(3, orderItem.getQuantity());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new DaoException("Failed to create order item.");
        }
        return getById(key.intValue());
    }

    /**
     * Updates an order item.
     *
     * @param orderItem The order item to update.
     * @return OrderItem The updated order item.
     */
    public OrderItem update(OrderItem orderItem) {
        String sql = "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ? WHERE id = ?;";
        int rowsAffected = jdbcTemplate.update(sql,
                orderItem.getOrderId(),
                orderItem.getProductId(),
                orderItem.getQuantity(),
                orderItem.getId());
        if (rowsAffected == 0) {
            throw new DaoException("Zero rows affected, expected at least one.");
        }
        return getById(orderItem.getId());
    }

    /**
     * Deletes an order item.
     *
     * @param id The id of the order item.
     * @return The number of rows affected.
     */
    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM order_items WHERE id = ?;", id);
    }

    /**
     * Maps a row in the ResultSet to an OrderItem object.
     *
     * @param resultSet The result set to map.
     * @param rowNumber The row number.
     * @return OrderItem The order item object.
     * @throws SQLException If an error occurs while mapping the result set.
     */
    private OrderItem mapToOrderItem(ResultSet resultSet, int rowNumber) throws SQLException {
        return new OrderItem(
                resultSet.getInt("id"),
                resultSet.getInt("order_id"),
                resultSet.getInt("product_id"),
                resultSet.getInt("quantity")
        );
    }
}
