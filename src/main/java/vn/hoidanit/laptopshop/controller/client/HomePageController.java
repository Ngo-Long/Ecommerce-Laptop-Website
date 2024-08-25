package vn.hoidanit.laptopshop.controller.client;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.ProductService;

@Controller
public class HomePageController {

    private final ProductService productService;

    public HomePageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String getHomePage() {
        return "client/home/show";
    }

    @RequestMapping("/product")
    public String getProductPage(Model model) {
        List<Product> dataProducts = this.productService.getAllProduct();
        model.addAttribute("dataProducts", dataProducts);
        return "client/product/show";
    }

    @RequestMapping("/product/{id}")
    public String getProductDetailPage(Model model, @PathVariable long id) {
        // Product dataProduct = this.productService.getProductById(id);
        // model.addAttribute("dataProduct", dataProduct);
        return "client/product/show";
    }

}
