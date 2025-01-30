//package com.saysimple.products.client;
//
//import com.saysimple.users.error.FeignErrorDecoder;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.util.List;
//
//@FeignClient(name = "orders", configuration = FeignErrorDecoder.class)
//public interface OrderServiceClient {
//
//    @GetMapping("/orders/{userId}/orders")
////    @GetMapping("/orders/{userId}/orders_wrong")
////    ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable String userId);
//    List<ResponseOrder> getOrders(@PathVariable String userId);
//}
