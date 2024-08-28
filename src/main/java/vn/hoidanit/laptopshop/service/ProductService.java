package vn.hoidanit.laptopshop.service;

import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;

@Service
public class ProductService {

    private final UserService userService;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

    public ProductService(
            UserService userService,
            ProductRepository productRepository,
            CartRepository cartRepository,
            CartDetailRepository cartDetailRepository) {
        this.userService = userService;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
    }

    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    public Product getProductById(long id) {
        return this.productRepository.findById(id);
    }

    public Product handleSaveProduct(Product product) {
        return this.productRepository.save(product);
    }

    public void deleteProductById(long id) {
        this.productRepository.deleteById(id);
    }

    public Cart fetchByUser(User user) {
        return this.cartRepository.findByUser(user);
    }

    public void handleAddProductToCart(String email, long productId, HttpSession session) {
        User dataUser = this.userService.getUserByEmail(email);
        if (dataUser == null) {
            return;
        }

        // check user đã có Cart chưa ? nếu chưa -> tạo mới
        Cart dataCart = this.cartRepository.findByUser(dataUser);
        if (dataCart == null) {
            // tạo mới cart
            Cart otherCart = new Cart();
            otherCart.setUser(dataUser);
            otherCart.setSum(0);

            dataCart = this.cartRepository.save(otherCart);
        }

        // save cart_detail
        Product dataProduct = this.productRepository.findById(productId);
        if (dataProduct == null) {
            return;
        }

        CartDetail oldDetail = this.cartDetailRepository.findByCartAndProduct(dataCart, dataProduct);
        if (oldDetail == null) {
            CartDetail cartDetail = new CartDetail();
            cartDetail.setCart(dataCart);
            cartDetail.setProduct(dataProduct);
            cartDetail.setPrice(dataProduct.getPrice());
            cartDetail.setQuantity(1);
            this.cartDetailRepository.save(cartDetail);

            // update add (sum)
            int numSum = dataCart.getSum() + 1;
            dataCart.setSum(numSum);
            this.cartRepository.save(dataCart);
            session.setAttribute("sum", numSum);
        } else {
            oldDetail.setQuantity(oldDetail.getQuantity() + 1);
            this.cartDetailRepository.save(oldDetail);
        }

    }

}
