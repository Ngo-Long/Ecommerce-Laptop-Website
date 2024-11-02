package vn.ecommerce.laptopshop.service;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.ecommerce.laptopshop.domain.Cart;
import vn.ecommerce.laptopshop.domain.CartDetail;
import vn.ecommerce.laptopshop.domain.Order;
import vn.ecommerce.laptopshop.domain.OrderDetail;
import vn.ecommerce.laptopshop.domain.Product;
import vn.ecommerce.laptopshop.domain.Product_;
import vn.ecommerce.laptopshop.domain.User;
import vn.ecommerce.laptopshop.domain.dto.ProductCriteriaDTO;
import vn.ecommerce.laptopshop.repository.CartDetailRepository;
import vn.ecommerce.laptopshop.repository.CartRepository;
import vn.ecommerce.laptopshop.repository.OrderDetailRepository;
import vn.ecommerce.laptopshop.repository.OrderRepository;
import vn.ecommerce.laptopshop.repository.ProductRepository;
import vn.ecommerce.laptopshop.service.specification.ProductSpecs;

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

    public Page<Product> fetchProducts(Pageable page) {
        return this.productRepository.findAll(page);
    }

    public Page<Product> fetchProductsWithSpec(Pageable page, ProductCriteriaDTO productCriteriaDTO) {
        if (productCriteriaDTO.getFactory() == null
                && productCriteriaDTO.getTarget() == null
                && productCriteriaDTO.getPrice() == null) {
            return this.productRepository.findAll(page);
        }

        Specification<Product> combinedSpec = Specification.where(null);

        if (productCriteriaDTO.getTarget() != null && productCriteriaDTO.getTarget().isPresent()) {
            Specification<Product> currentSpecs = ProductSpecs.matchListTarget(productCriteriaDTO.getTarget().get());
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        if (productCriteriaDTO.getFactory() != null && productCriteriaDTO.getFactory().isPresent()) {
            Specification<Product> currentSpecs = ProductSpecs.matchListFactory(productCriteriaDTO.getFactory().get());
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        if (productCriteriaDTO.getPrice() != null && productCriteriaDTO.getPrice().isPresent()) {
            Specification<Product> currentSpecs = this.buildPriceSpecification(productCriteriaDTO.getPrice().get());
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        return this.productRepository.findAll(combinedSpec, page);
    }

    public Specification<Product> buildPriceSpecification(List<String> price) {
        Specification<Product> combinedSpec = Specification.where(null);
        for (String p : price) {
            double min = 0;
            double max = 0;

            // Set the appropriate min and max based on the price range string
            switch (p) {
                case "duoi-10-trieu":
                    min = 1;
                    max = 10000000;
                    break;
                case "10-15-trieu":
                    min = 10000000;
                    max = 15000000;
                    break;
                case "15-20-trieu":
                    min = 15000000;
                    max = 20000000;
                    break;
                case "tren-20-trieu":
                    min = 20000000;
                    max = 200000000;
                    break;
                // Add more cases as needed
            }

            if (min != 0 && max != 0) {
                Specification<Product> rangeSpec = ProductSpecs.matchMultiplePrice(min, max);
                combinedSpec = combinedSpec.or(rangeSpec);
            }
        }

        return combinedSpec;
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
