package vn.hoidanit.laptopshop.service;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;

@Service
public class ProductService {

    private final UserService userService;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public ProductService(
            ProductRepository productRepository,
            CartRepository cartRepository,
            CartDetailRepository cartDetailRepository,
            UserService userService,
            OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
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

    public void handleAddProductToCart(String email, long productId, HttpSession session, long quantity) {
        User dataUser = this.userService.getUserByEmail(email);
        if (dataUser == null) {
            return;
        }

        // check user
        Cart dataCart = this.cartRepository.findByUser(dataUser);
        if (dataCart == null) {
            // create new cart
            Cart otherCart = new Cart();
            otherCart.setUser(dataUser);
            otherCart.setSum(0);

            dataCart = this.cartRepository.save(otherCart);
        }

        // find product
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
            cartDetail.setQuantity(quantity);
            this.cartDetailRepository.save(cartDetail);

            // update add (sum)
            int numSum = dataCart.getSum() + 1;
            dataCart.setSum(numSum);
            this.cartRepository.save(dataCart);
            session.setAttribute("sum", numSum);
        } else {
            oldDetail.setQuantity(oldDetail.getQuantity() + quantity);
            this.cartDetailRepository.save(oldDetail);
        }

    }

    public void handleRemoveCartDetail(long cartDetailId, HttpSession session) {
        // delete cart detail
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(cartDetailId);
        if (!cartDetailOptional.isPresent()) {
            return;
        }

        // delete cart detail
        CartDetail cartDetail = cartDetailOptional.get();
        this.cartDetailRepository.delete(cartDetail);

        // data cart
        Cart dataCart = cartDetail.getCart();
        if (dataCart.getSum() > 1) {
            // update remove (sum)
            int numSum = dataCart.getSum() - 1;
            dataCart.setSum(numSum);

            this.cartRepository.save(dataCart);
            session.setAttribute("sum", numSum);
        } else {
            // delete cart (sum = 1)
            this.cartRepository.delete(dataCart);
            session.setAttribute("sum", 0);
        }
    }

    public void handleUpdateCartBeforeCheckout(List<CartDetail> cartDetails) {
        if (cartDetails == null) {
            return;
        }

        for (CartDetail cartDetail : cartDetails) {
            Optional<CartDetail> cdOptional = this.cartDetailRepository.findById(cartDetail.getId());
            if (!cdOptional.isPresent()) {
                return;
            }

            CartDetail currentCartDetail = cdOptional.get();
            currentCartDetail.setQuantity(cartDetail.getQuantity());
            this.cartDetailRepository.save(currentCartDetail);
        }
    }

    public void handlePlaceOrder(
            User user, HttpSession session,
            String receiverName, String receiverAddress, String receiverPhone) {

        // step 1: get cart by user
        Cart cart = this.cartRepository.findByUser(user);
        if (cart == null) {
            return;
        }

        List<CartDetail> cartDetails = cart.getCartDetails();
        if (cartDetails == null) {
            return;
        }

        // create order
        Order order = new Order();
        order.setUser(user);
        order.setReceiverName(receiverName);
        order.setReceiverAddress(receiverAddress);
        order.setReceiverPhone(receiverPhone);
        order.setStatus("PENDING");

        double sum = 0;
        for (CartDetail cd : cartDetails) {
            sum += cd.getPrice() * cd.getQuantity();
        }
        order.setTotalPrice(sum);
        order = this.orderRepository.save(order);

        // create orderDetail
        for (CartDetail cd : cartDetails) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cd.getProduct());
            orderDetail.setPrice(cd.getPrice());
            orderDetail.setQuantity(cd.getQuantity());

            this.orderDetailRepository.save(orderDetail);
        }

        // step 2: delete cart_detail and cart
        for (CartDetail cd : cartDetails) {
            this.cartDetailRepository.deleteById(cd.getId());
        }

        this.cartRepository.deleteById(cart.getId());

        // step 3 : update session
        session.setAttribute("sum", 0);
    }
}
