package com.ywl.study.orderquery;

import com.ywl.study.orderquery.query.OrderEntity;
import com.ywl.study.orderquery.query.OrderEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderEntityRepository orderEntityRepository;

    @GetMapping("")
    public List<OrderEntity> get(){
       return orderEntityRepository.findAll();
    }

    @GetMapping("/{orderId}")
    public OrderEntity get(@PathVariable String orderId){
       return orderEntityRepository.findOne(orderId);
    }



}
