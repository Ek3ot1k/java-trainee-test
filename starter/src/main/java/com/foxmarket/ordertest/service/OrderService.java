package com.foxmarket.ordertest.service;

import com.foxmarket.ordertest.dto.CreateOrderItemRequest;
import com.foxmarket.ordertest.dto.CreateOrderRequest;
import com.foxmarket.ordertest.dto.OrderItemResponse;
import com.foxmarket.ordertest.dto.OrderResponse;
import com.foxmarket.ordertest.entity.Order;
import com.foxmarket.ordertest.entity.OrderItem;
import com.foxmarket.ordertest.entity.Product;
import com.foxmarket.ordertest.exception.*;
import com.foxmarket.ordertest.repository.OrderRepository;
import com.foxmarket.ordertest.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request){
        validateDuplicates(request);

        Order order=new Order(BigDecimal.ZERO);
        BigDecimal total=BigDecimal.ZERO;

        for(CreateOrderItemRequest itemRequest:request.items()){
            Product product=productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ProductNotFoundException(itemRequest.productId()));

            if(!Boolean.TRUE.equals(product.getAvailable())){
                throw new ProductUnavailableException(product.getId());
            }

            if(product.getStock()< itemRequest.quantity()){
                throw new InsufficientStockException(product.getId());
            }

            BigDecimal price=product.getPrice();
            BigDecimal subtotal=price.multiply(BigDecimal.valueOf(itemRequest.quantity()));

            product.decreaseStock(itemRequest.quantity());

            OrderItem orderItem=new OrderItem(
                    product,
                    itemRequest.quantity(),
                    price
            );

            order.addItem(orderItem);
            total=total.add(subtotal);
        }

        order.updateTotal(total);

        Order savedOrder = orderRepository.save(order);

        return toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId){
        Order order=orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        return toResponse(order);
    }

    private void validateDuplicates(CreateOrderRequest request) {
        Set<Long> productIds = new HashSet<>();

        for (CreateOrderItemRequest item : request.items()) {
            if (!productIds.add(item.productId())) {
                throw new DuplicateProductException(item.productId());
            }
        }
    }

    private OrderResponse toResponse(Order order) {
        var items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                items,
                order.getTotal()
        );
    }
}
