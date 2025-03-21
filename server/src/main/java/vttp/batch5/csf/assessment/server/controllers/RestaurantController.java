package vttp.batch5.csf.assessment.server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.batch5.csf.assessment.server.services.RestaurantService;

import java.io.StringReader;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;

@Controller
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantController {

  @Autowired
  private RestaurantService restaurantService;

  String paymentId = "";

  // TODO: Task 2.2
  // You may change the method's signature
  @GetMapping("/menu")
  public ResponseEntity<String> getMenus() {
    JsonArray result = restaurantService.getMenu();
    return ResponseEntity.ok(result.toString());
  }

  // TODO: Task 4
  // Do not change the method's signature
  @PostMapping("/food_order")
  public ResponseEntity<String> postFoodOrder(@RequestBody String payload) {
    JsonReader reader = Json.createReader(new StringReader(payload));
    JsonObject foodOrder = reader.readObject();
    reader.close();

    String username = foodOrder.getString("username", "");
    String password = foodOrder.getString("password", "");
    JsonArray items = foodOrder.getJsonArray("items");

    double totalAmount = 0.0;
    String orderId = UUID.randomUUID().toString().substring(0, 8);
    String paymentId = "";
    double roundedTotal = 0.0;
    java.util.Date date = null;

    for (int i = 0; i < items.size(); i++) {
      JsonObject item = items.getJsonObject(i);

      double price = item.getJsonNumber("price").doubleValue();
      int quantity = item.getInt("quantity");

      double itemTotal = price * quantity;
      totalAmount += itemTotal;
    }

    boolean validated = restaurantService.validateCredentials(username, password);

    if (!validated) {
      JsonObject errResp = Json
          .createObjectBuilder()
          .add("message", "Invalid username and/or password")
          .build();

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errResp.toString());
    }

    JsonObject paymentGateway = Json
        .createObjectBuilder()
        .add("order_id", orderId)
        .add("payer", username)
        .add("payee", "Chen Jinyang, Aloysius")
        .add("payment", totalAmount)
        .build();

    RequestEntity<String> req = RequestEntity
        .post("https://payment-service-production-a75a.up.railway.app/api/payment")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .header("X-Authenticate", username)
        .body(paymentGateway.toString(), String.class);

    RestTemplate template = new RestTemplate();

    try {
      ResponseEntity<String> resp = template.exchange(req, String.class);
      String response = resp.getBody();

      System.out.println("Payment Gateway Response: " + response);

      JsonReader responseReader = Json.createReader(new StringReader(response));
      JsonObject responseObj = responseReader.readObject();
      responseReader.close();

      paymentId = responseObj.getString("payment_id");
      long timestamp = responseObj.getJsonNumber("timestamp").longValue();
      double total = responseObj.getJsonNumber("total").doubleValue();
      roundedTotal = Math.round(total * 100.0) / 100.0;

      date = new java.util.Date(timestamp);

      restaurantService.add(orderId, paymentId, date, roundedTotal, username);

      JsonObject mongoObject = Json
          .createObjectBuilder()
          .add("_id", orderId)
          .add("order_id", orderId)
          .add("payment_id", paymentId)
          .add("username", username)
          .add("total", roundedTotal)
          .add("timestamp", date.toString())
          .add("items", items)
          .build();

    } catch (Exception ex) {
      ex.printStackTrace();
      String message = ex.getMessage();
      JsonObject errorResp = Json
          .createObjectBuilder()
          .add("message", message)
          .build();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResp.toString());
    }

    JsonObject successResp = Json
        .createObjectBuilder()
        .add("orderId", orderId)
        .add("paymentId", paymentId)
        .add("total", roundedTotal)
        .add("timestamp", date.toString()) 
        .build();

    return ResponseEntity.ok(successResp.toString());
  }
}