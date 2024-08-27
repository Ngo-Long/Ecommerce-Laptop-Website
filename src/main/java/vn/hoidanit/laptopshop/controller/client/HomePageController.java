package vn.hoidanit.laptopshop.controller.client;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.dto.RegisterDTO;
import vn.hoidanit.laptopshop.service.UserService;
import vn.hoidanit.laptopshop.service.ProductService;

@Controller
public class HomePageController {

    private final ProductService productService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public HomePageController(ProductService productService,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.productService = productService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // Register and login
    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerUser", new RegisterDTO());
        return "client/auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @ModelAttribute("registerUser") @Valid RegisterDTO registerDTO,
            BindingResult bindingResult) {

        // validate
        if (bindingResult.hasErrors()) {
            return "client/auth/register";
        }

        User dataUser = this.userService.registerDTOtoUser(registerDTO);

        String hashPassword = this.passwordEncoder.encode(dataUser.getPassword());
        dataUser.setPassword(hashPassword);
        dataUser.setRole(this.userService.getRoldByName("USER"));
        this.userService.handleSaveUser(dataUser);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        return "client/auth/login";
    }

    @GetMapping("/access-deny")
    public String getDenyPage(Model model) {
        return "client/auth/access-deny";
    }

    // Load product
    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Product> dataProducts = this.productService.getAllProducts();
        model.addAttribute("dataProducts", dataProducts);
        return "client/home/show";
    }

    @RequestMapping("/product")
    public String getProductPage(Model model) {
        List<Product> dataProducts = this.productService.getAllProducts();
        model.addAttribute("dataProducts", dataProducts);
        return "client/product/detail";
    }

    @RequestMapping("/product/{id}")
    public String getProductDetailPage(Model model, @PathVariable long id) {
        Product dataProduct = this.productService.getProductById(id);
        model.addAttribute("dataProduct", dataProduct);
        return "client/product/detail";
    }

}
