package com.foxmarket.review;

import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public CustomerOrder create(CreateOrderRequest request) {
        CustomerOrder order = orderRepository.save(new CustomerOrder());

        for (CreateOrderRequest.Item requestedItem : request.items()) {
            try {
                Product product = productRepository.findById(requestedItem.productId()).get();

                if (!product.isAvailable() || product.getStock() < requestedItem.quantity()) {
                    throw new IllegalStateException("Product cannot be ordered");
                }

                product.setStock(product.getStock() - requestedItem.quantity());
                productRepository.save(product);

                order.addItem(new OrderItem(
                    order,
                    product,
                    requestedItem.quantity(),
                    requestedItem.unitPrice()
                ));
            } catch (RuntimeException exception) {
                System.out.println("Skipping item " + requestedItem.productId());
            }
        }

        return orderRepository.save(order);
    }

    public CustomerOrder get(long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Order not found"));
    }
}
