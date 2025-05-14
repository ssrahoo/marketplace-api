package ssrahoo.marketplaceapi.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ssrahoo.marketplaceapi.repository.ProductRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @Nested
    class findAll {
        //TODO: add unit tests
    }

    @Nested
    class findById {
        //TODO: add unit tests
    }


    @Nested
    class updateById{
        //TODO: add unit tests
    }

    @Nested
    class deleteById{
        //TODO: add unit tests
    }


}