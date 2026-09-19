package com.foxmarket.ordertest;

import com.foxmarket.ordertest.dto.CreateOrderItemRequest;
import com.foxmarket.ordertest.dto.CreateOrderRequest;
import com.foxmarket.ordertest.dto.OrderResponse;
import com.foxmarket.ordertest.entity.Product;
import com.foxmarket.ordertest.exception.InsufficientStockException;
import com.foxmarket.ordertest.exception.ProductNotFoundException;
import com.foxmarket.ordertest.repository.OrderRepository;
import com.foxmarket.ordertest.repository.ProductRepository;
import com.foxmarket.ordertest.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class OrderServiceIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres=
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("order_test")
                    .withUsername("order_test")
                    .withPassword("order_test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url",postgres::getJdbcUrl);
        registry.add("spring.datasource.username",postgres::getUsername);
        registry.add("spring.datasource.password",postgres::getPassword);
    }

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp(){
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateOrderAndDecreaseStock(){
        Product keyboard=productRepository.save(
                new Product("keyboard",new BigDecimal("79.99"),10,true)
        );

        Product mouse = productRepository.save(
                new Product("Mouse", new BigDecimal("39.50"), 5, true)
        );

        CreateOrderRequest request=new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(keyboard.getId(), 2),
                        new CreateOrderItemRequest(mouse.getId(), 1)
                )
        );

        OrderResponse response=orderService.createOrder(request);

        assertNotNull(response.id());
        assertEquals(new BigDecimal("199.48"),response.total());
        assertEquals(2,response.items().size());

        Product updatedKeyboard = productRepository.findById(keyboard.getId()).orElseThrow();
        Product updatedMouse = productRepository.findById(mouse.getId()).orElseThrow();

        assertEquals(8, updatedKeyboard.getStock());
        assertEquals(4, updatedMouse.getStock());

        assertEquals(1, orderRepository.count());
    }

    @Test
    void shouldNotCreateOrderWhenStockIsInsufficient() {
        Product product = productRepository.save(
                new Product(
                        "Monitor",
                        new BigDecimal("249.90"),
                        1,
                        true
                )
        );

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(product.getId(), 2)
                )
        );

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.createOrder(request)
        );

        Product unchangedProduct = productRepository.findById(product.getId()).orElseThrow();

        assertEquals(1, unchangedProduct.getStock());
        assertEquals(0, orderRepository.count());
    }

    @Test
    void shouldNotCreateOrderWhenProductDoesNotExist() {
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(999999L, 1)
                )
        );

        assertThrows(
                ProductNotFoundException.class,
                () -> orderService.createOrder(request)
        );

        assertEquals(0, orderRepository.count());
    }

    @Test
    void shouldRollbackStockChangesWhenLaterItemFails() {
        Product firstProduct = productRepository.save(
                new Product(
                        "Keyboard",
                        new BigDecimal("79.99"),
                        10,
                        true
                )
        );

        Product secondProduct = productRepository.save(
                new Product(
                        "Monitor",
                        new BigDecimal("249.90"),
                        0,
                        true
                )
        );

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(firstProduct.getId(), 2),
                        new CreateOrderItemRequest(secondProduct.getId(), 1)
                )
        );

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.createOrder(request)
        );

        Product unchangedFirstProduct =
                productRepository.findById(firstProduct.getId()).orElseThrow();

        assertEquals(10, unchangedFirstProduct.getStock());
        assertEquals(0, orderRepository.count());
    }
}
