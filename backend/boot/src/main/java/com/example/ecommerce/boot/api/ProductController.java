package com.example.ecommerce.boot.api;

import com.example.ecommerce.application.product.ListProductsUseCase;
import com.example.ecommerce.application.product.ProductView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para produtos. Componente fino que apenas orquestra o
 * caso de uso `ListProductsUseCase` e realiza o mapeamento para `ProductView`.
 *
 * Não contém regra de negócio — essa responsabilidade está na camada de aplicação.
 */
@Tag(name = "Products", description = "Operações relacionadas a produtos")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ListProductsUseCase listProducts;

    public ProductController(ListProductsUseCase listProducts) {
        this.listProducts = listProducts;
    }

    /**
     * Lista produtos disponíveis.
     *
     * @return lista de `ProductView` montada pelo caso de uso
     */
    @Operation(summary = "Listar produtos", description = "Retorna todos os produtos disponíveis")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductView.class)))
    })
    @GetMapping
    public List<ProductView> list() { return listProducts.handle(); }
}
