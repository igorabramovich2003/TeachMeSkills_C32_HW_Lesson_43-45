package com.tms.repository;

import com.tms.config.DatabaseService;
import com.tms.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.tms.config.SQLQuery.CREATE_PRODUCT;
import static com.tms.config.SQLQuery.DELETE_PRODUCT;
import static com.tms.config.SQLQuery.GET_ALL_PRODUCTS;
import static com.tms.config.SQLQuery.GET_PRODUCT_BY_ID;
import static com.tms.config.SQLQuery.UPDATE_PRODUCT;

@Repository
public class ProductRepository {

    private final DatabaseService databaseService;

    @Autowired
    public ProductRepository(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public Optional<Product> getProductById(Integer id) {
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_PRODUCT_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Product product = parseProduct(resultSet);
                return Optional.of(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean createProduct(Product product) {
        String[] generatedColumns = {"id", "created", "updated"};
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_PRODUCT, generatedColumns)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            Timestamp now = new Timestamp(System.currentTimeMillis());
            preparedStatement.setTimestamp(3, now);
            preparedStatement.setTimestamp(4, now);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setId(generatedKeys.getLong(1));
                        product.setCreated(generatedKeys.getTimestamp(2));
                        product.setUpdated(generatedKeys.getTimestamp(3));
                    }
                }
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        System.out.println("Preparing to update product in database. Product: " + product);
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PRODUCT)) {
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            statement.setLong(4, product.getId());
            int rowsUpdated = statement.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteProduct(Integer id) {
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PRODUCT)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = databaseService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_PRODUCTS);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Product product = parseProduct(resultSet);
                products.add(product);
                System.out.println("Product fetched: " + product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Total products fetched: " + products.size());
        return products;
    }
    private Product parseProduct(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getDouble("price"),
                resultSet.getTimestamp("created"),
                resultSet.getTimestamp("updated")
        );
    }
}
