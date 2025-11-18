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

/**
 * Configuração de beans para os casos de uso da aplicação.
 *
 * Mantemos os casos de uso como beans gerenciados pelo Spring para que a
 * camada de entrada (controllers) os injete facilmente enquanto a infraestrutura
 * (adapters/repositories) permanece desacoplada via portas.
 */
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
        // Cria o caso de uso com as portas necessárias (load/save cart, load product)
        return new AddToCartUseCase(loadCartPort, saveCartPort, loadProductByIdPort);
    }

    @Bean
    public GetCartUseCase getCartUseCase(LoadCartPort loadCartPort) {
        // Caso de uso de leitura do carrinho
        return new GetCartUseCase(loadCartPort);
    }

    @Bean
    public com.example.ecommerce.application.cart.UpdateCartItemUseCase updateCartItemUseCase(LoadCartPort loadCartPort,
                                                                                              SaveCartPort saveCartPort,
                                                                                              LoadProductByIdPort loadProductByIdPort) {
        return new com.example.ecommerce.application.cart.UpdateCartItemUseCase(loadCartPort, saveCartPort, loadProductByIdPort);
    }
}
