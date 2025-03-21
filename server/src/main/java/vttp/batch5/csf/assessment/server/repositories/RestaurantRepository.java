package vttp.batch5.csf.assessment.server.repositories;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

// Use the following class for MySQL database
@Repository
public class RestaurantRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    String validateRequest = "SELECT COUNT(*) FROM customers WHERE username = ? AND password = ?";

    String insertOrder = "INSERT INTO place_orders (order_id, payment_id, order_date, total, username) VALUES (?,?,?,?,?)";
    
    public boolean validateCredentials(String username, String password) {
        Integer count = jdbcTemplate.queryForObject(validateRequest, Integer.class, username, password);
        return count != null && count > 0;
    }


    public boolean add(String order_id, String payment_id, Date order_date, double total, String username){
        int added = jdbcTemplate.update(insertOrder, order_id, payment_id, order_date, total, username);

        return added > 0;
    }
}
