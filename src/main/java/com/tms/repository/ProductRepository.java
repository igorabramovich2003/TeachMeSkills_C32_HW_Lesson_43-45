package com.tms.repository;

import com.tms.config.DatabaseService;
import com.tms.config.SQLQuery;
import com.tms.model.Product;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;


@Repository
public class ProductRepository {

    private final DatabaseService databaseService;

    public ProductRepository(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public Optional<Product> getProductById(Long id) {
        Connection connection = databaseService.getConnection();

        try {
            PreparedStatement getProductStatement = connection.prepareStatement(SQLQuery.GET_PRODUCT_BY_ID);
            getProductStatement.setLong(1, id);
            ResultSet resultSet = getProductStatement.executeQuery();
            return parseProduct(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    public Boolean deleteProduct(Long id) {
        Connection connection = databaseService.getConnection();

        try {
            PreparedStatement deleteProductStatement = connection.prepareStatement(SQLQuery.DELETE_PRODUCT);
            deleteProductStatement.setLong(1, id);

            return deleteProductStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Optional<Long> createProduct(Product product) {
        Connection connection = databaseService.getConnection();
        Long productId = null;

        try{
            PreparedStatement createProductStatement = connection.prepareStatement(SQLQuery.CREATE_PRODUCT, Statement.RETURN_GENERATED_KEYS);
            createProductStatement.setString(1, product.getName());
            createProductStatement.setDouble(2, product.getPrice());
            createProductStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            createProductStatement.executeUpdate();

            ResultSet generatedKeys = createProductStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                productId = generatedKeys.getLong(1);
            }
            return Optional.of(productId);


        } catch (SQLException e){
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }
    public Boolean updateProduct(Product product) {
        Connection connection = databaseService.getConnection();

        try {
            PreparedStatement getProductStatement = connection.prepareStatement(SQLQuery.UPDATE_PRODUCT);

            getProductStatement.setString(1, product.getName());
            getProductStatement.setDouble(2, product.getPrice());
            getProductStatement.setLong(3, product.getId());
            return getProductStatement.executeUpdate() > 0;
        }catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    private Optional<Product> parseProduct(ResultSet resultSet) throws SQLException {
        if(resultSet.next()){
            Product product = new Product();

            product.setId(resultSet.getLong("id"));
            product.setName(resultSet.getString("name"));
            product.setPrice(resultSet.getDouble("price"));
            product.setCreated(resultSet.getTimestamp("created"));
            product.setUpdated(resultSet.getTimestamp("updated"));
            return Optional.of(product);
        }
        return Optional.empty();
    }

}
