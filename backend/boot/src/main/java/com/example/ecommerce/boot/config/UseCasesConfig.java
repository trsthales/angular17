package com.example.ecommerce.boot.config;

import com.example.ecommerce.application.ports.LoadProductsPort;
import com.example.ecommerce.application.ports.cart.LoadCartPort;
import com.example.ecommerce.application.ports.cart.SaveCartPort;
import com.example.ecommerce.application.ports.product.LoadProductByIdPort;
import com.example.ecommerce.application.product.ListProductsUseCase;
import com.example.ecommerce.application.cart.AddToCartUseCase;
import com.example.ecommerce.application.cart.GetCartUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configura os casos de uso (injeção manual favorece Clean Architecture). */
@Configuration
public class UseCasesConfig {

    @Bean
    public ListProductsUseCase listProductsUseCase(LoadProductsPort loadProductsPort) {
        return new ListProductsUseCase(loadProductsPort);
    }

    @Bean
    public AddToCartUseCase addToCartUseCase(LoadCartPort loadCartPort,
                                             SaveCartPort saveCartPort,
                                             LoadProductByIdPort loadProductByIdPort) {
        return new AddToCartUseCase(loadCartPort, saveCartPort, loadProductByIdPort);
    }

    @Bean
    public GetCartUseCase getCartUseCase(LoadCartPort loadCartPort) {
        return new GetCartUseCase(loadCartPort);
    }
}
