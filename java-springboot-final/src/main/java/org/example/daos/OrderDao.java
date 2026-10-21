package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.Order;
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
 * Data access object for orders.
 */
@Component
public class OrderDao {
    /**
     * The JDBC template for querying the database.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates a new order data access object.
     *
     * @param dataSource The data source for the DAO.
     */
    public OrderDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all orders.
     *
     * @return List of Order
     */
    public List<Order> getAll() {
        return jdbcTemplate.query("SELECT * FROM orders ORDER BY id;", this::mapToOrder);
    }

    /**
     * Gets all orders belonging to a user.
     *
     * @param username The username to filter by.
     * @return List of Order
     */
    public List<Order> getByUsername(String username) {
        return jdbcTemplate.query("SELECT * FROM orders WHERE username = ? ORDER BY id;", this::mapToOrder, username);
    }

    /**
     * Gets an order by id.
     *
     * @param id The id of the order.
     * @return Order, or null if not found.
     */
    public Order getById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM orders WHERE id = ?;", this::mapToOrder, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new order.
     *
     * @param order The order to create.
     * @return Order The created order.
     */
    public Order create(Order order) {
        String sql = "INSERT INTO orders (username) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, order.getUsername());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new DaoException("Failed to create order.");
        }
        return getById(key.intValue());
    }

    /**
     * Updates an order.
     *
     * @param order The order to update.
     * @return Order The updated order.
     */
    public Order update(Order order) {
        String sql = "UPDATE orders SET username = ? WHERE id = ?;";
        int rowsAffected = jdbcTemplate.update(sql, order.getUsername(), order.getId());
        if (rowsAffected == 0) {
            throw new DaoException("Zero rows affected, expected at least one.");
        }
        return getById(order.getId());
    }

    /**
     * Deletes an order.
     *
     * @param id The id of the order.
     * @return The number of rows affected.
     */
    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM orders WHERE id = ?;", id);
    }

    /**
     * Maps a row in the ResultSet to an Order object.
     *
     * @param resultSet The result set to map.
     * @param rowNumber The row number.
     * @return Order The order object.
     * @throws SQLException If an error occurs while mapping the result set.
     */
    private Order mapToOrder(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Order(
                resultSet.getInt("id"),
                resultSet.getString("username")
        );
    }
}
