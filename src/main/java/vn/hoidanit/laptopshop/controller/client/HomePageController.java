package vn.hoidanit.laptopshop.controller.client;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.dto.RegisterDTO;

import vn.hoidanit.laptopshop.service.UserService;
import vn.hoidanit.laptopshop.service.OrderService;
import vn.hoidanit.laptopshop.service.ProductService;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomePageController {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;
    private final PasswordEncoder passwordEncoder;

    public HomePageController(ProductService productService,
            OrderService orderService,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // Load product
    @GetMapping("/")
    public String getHomePage(Model model) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> pageProducts = this.productService.fetchProducts(pageable);
        List<Product> dataProducts = pageProducts.getContent();

        model.addAttribute("dataProducts", dataProducts);
        return "client/home/show";
    }

    @RequestMapping("/product")
    public String getProductPage(Model model) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> pageProducts = this.productService.fetchProducts(pageable);

        List<Product> dataProducts = pageProducts.getContent();
        model.addAttribute("dataProducts", dataProducts);
        return "client/product/detail";
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

    @GetMapping("/order-history")
    public String getOrderHistoryPage(Model model, HttpServletRequest request) {
        User currentUser = new User();
        HttpSession session = request.getSession(false);
        long id = (long) session.getAttribute("id");
        currentUser.setId(id);

        List<Order> orders = this.orderService.fetchOrderByUser(currentUser);
        model.addAttribute("orders", orders);

        return "client/cart/order-history";
    }

}
