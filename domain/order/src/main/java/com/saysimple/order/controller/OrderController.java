package com.saysimple.order.controller;

import com.saysimple.order.dto.OrderDto;
import com.saysimple.order.jpa.OrderEntity;
import com.saysimple.order.messagequeue.KafkaProducer;
import com.saysimple.order.messagequeue.OrderProducer;
import com.saysimple.order.service.OrderService;
import com.saysimple.order.vo.RequestOrder;
import com.saysimple.order.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.saysimple.util.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {
    Environment env;
    OrderService orderService;
    KafkaProducer kafkaProducer;

    OrderProducer orderProducer;

    @Autowired
    public OrderController(Environment env, OrderService orderService,
                           KafkaProducer kafkaProducer, OrderProducer orderProducer) {
        this.env = env;
        this.orderService = orderService;
        this.kafkaProducer = kafkaProducer;
        this.orderProducer = orderProducer;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on LOCAL PORT %s (SERVER PORT %s)",
                env.getProperty("local.server.port"),
                env.getProperty("server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        OrderDto orderDto = ModelUtils.strictMap(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);

        orderProducer.send("orders", orderDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ModelUtils.strictMap(orderDetails, ResponseOrder.class));
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) throws Exception {
        log.info("Before retrieve orders data");
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        try {
            Random rnd = new Random();
            int value = rnd.nextInt(5);
            if (value % 2 == 0) {
                Thread.sleep(10000);
                throw new Exception("장애 발생");
            }
        } catch (InterruptedException ex) {
            log.warn(ex.getMessage());
        }

        log.info("Add retrieved orders data");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
