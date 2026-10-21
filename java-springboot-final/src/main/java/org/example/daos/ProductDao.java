package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.Product;
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
 * Data access object for products.
 */
@Component
public class ProductDao {
    /**
     * The JDBC template for querying the database.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates a new product data access object.
     *
     * @param dataSource The data source for the DAO.
     */
    public ProductDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all products.
     *
     * @return List of Product
     */
    public List<Product> getAll() {
        return jdbcTemplate.query("SELECT * FROM products ORDER BY id;", this::mapToProduct);
    }

    /**
     * Gets a product by id.
     *
     * @param id The id of the product.
     * @return Product, or null if not found.
     */
    public Product getById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM products WHERE id = ?;", this::mapToProduct, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new product.
     *
     * @param product The product to create.
     * @return Product The created product.
     */
    public Product create(Product product) {
        String sql = "INSERT INTO products (name, price) VALUES (?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getName());
            ps.setBigDecimal(2, product.getPrice());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new DaoException("Failed to create product.");
        }
        return getById(key.intValue());
    }

    /**
     * Updates a product.
     *
     * @param product The product to update.
     * @return Product The updated product.
     */
    public Product update(Product product) {
        String sql = "UPDATE products SET name = ?, price = ? WHERE id = ?;";
        int rowsAffected = jdbcTemplate.update(sql, product.getName(), product.getPrice(), product.getId());
        if (rowsAffected == 0) {
            throw new DaoException("Zero rows affected, expected at least one.");
        }
        return getById(product.getId());
    }

    /**
     * Deletes a product.
     *
     * @param id The id of the product.
     * @return The number of rows affected.
     */
    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM products WHERE id = ?;", id);
    }

    /**
     * Maps a row in the ResultSet to a Product object.
     *
     * @param resultSet The result set to map.
     * @param rowNumber The row number.
     * @return Product The product object.
     * @throws SQLException If an error occurs while mapping the result set.
     */
    private Product mapToProduct(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Product(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getBigDecimal("price")
        );
    }
}
