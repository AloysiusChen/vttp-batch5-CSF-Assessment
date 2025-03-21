package vttp.batch5.csf.assessment.server.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import vttp.batch5.csf.assessment.server.repositories.OrdersRepository;
import vttp.batch5.csf.assessment.server.repositories.RestaurantRepository;

@Service
public class RestaurantService {

  @Autowired
  private OrdersRepository ordersRepository;

  @Autowired
  private RestaurantRepository restaurantRepository;

  // TODO: Task 2.2
  // You may change the method's signature
  public JsonArray getMenu() {
    JsonArrayBuilder arrBuilder = Json.createArrayBuilder();

    ordersRepository.getMenu().stream()
        .map(doc -> {
          return Json.createObjectBuilder()
              .add("id", doc.getString("_id"))
              .add("name", doc.getString("name"))
              .add("description", doc.getString("description"))
              .add("price", doc.getDouble("price"))
              .build();
        })
        .forEach(arrBuilder::add);

    return arrBuilder.build();
  }

  // TODO: Task 4
  public boolean validateCredentials(String username, String password) {
    return restaurantRepository.validateCredentials(username, password);
  }

  public boolean add(String order_id, String payment_id, Date order_date, double total, String username) {
    return restaurantRepository.add(order_id, payment_id, order_date, total, username);
  }

}
