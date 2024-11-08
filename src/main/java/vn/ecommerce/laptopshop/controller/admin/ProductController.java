package vn.ecommerce.laptopshop.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import jakarta.validation.Valid;
import vn.ecommerce.laptopshop.domain.Product;
import vn.ecommerce.laptopshop.service.ProductService;
import vn.ecommerce.laptopshop.service.UploadService;

import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProductController {

    private final ProductService productService;
    private final UploadService uploadService;

    public ProductController(ProductService productService, UploadService uploadService) {
        this.productService = productService;
        this.uploadService = uploadService;
    }

    @GetMapping("/admin/product")
    public String getProductPage(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent())
                page = Integer.parseInt(pageOptional.get());
        } catch (Exception e) {
        }

        Pageable pageable = PageRequest.of(page - 1, 5);
        Page<Product> pageProducts = this.productService.fetchProducts(pageable);

        List<Product> dataProducts = pageProducts.getContent().size() > 0
                ? pageProducts.getContent()
                : new ArrayList<Product>();

        model.addAttribute("dataProducts", dataProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageProducts.getTotalPages());

        return "admin/product/show";
    }

    @GetMapping("/admin/product/create")
    public String getCreateProductPage(Model model) {
        model.addAttribute("newProduct", new Product());
        return "admin/product/create";
    }

    @PostMapping(value = "/admin/product/create") // POST
    public String createProductPage(Model model,
            @ModelAttribute("newProduct") @Valid Product dataProduct,
            BindingResult newProductBindingResult,
            @RequestParam("imageNameFile") MultipartFile file) {
        // validate
        if (newProductBindingResult.hasErrors()) {
            return "/admin/product/create";
        }

        // upload image
        String image = this.uploadService.handleSaveUploadFile(file, "product");
        dataProduct.setImage(image);

        this.productService.handleSaveProduct(dataProduct);

        return "redirect:/admin/product";
    }

    // Detail
    @RequestMapping("/admin/product/{id}")
    public String getProductDetailPage(Model model, @PathVariable long id) {
        Product dataProduct = this.productService.getProductById(id);
        model.addAttribute("dataProduct", dataProduct);
        return "admin/product/detail";
    }

    // Delete
    @GetMapping("/admin/product/delete/{id}")
    public String getDeleteProductPage(Model model, @PathVariable long id) {
        Product currentProduct = this.productService.getProductById(id);
        model.addAttribute("dataProduct", currentProduct);
        return "admin/product/delete";
    }

    @PostMapping("/admin/product/delete")
    public String postDeleteProduct(Model model, @ModelAttribute("dataProduct") Product data) {
        this.productService.deleteProductById(data.getId());
        return "redirect:/admin/product";
    }

    // Update
    @GetMapping("/admin/product/update/{id}")
    public String getUpdateProductPage(Model model, @PathVariable long id) {
        Product currentProduct = this.productService.getProductById(id);
        model.addAttribute("dataProduct", currentProduct);
        return "admin/product/update";
    }

    @PostMapping("/admin/product/update")
    public String postUpdateProduct(@ModelAttribute("dataProduct") @Valid Product data,
            BindingResult dataProductBindingResult,
            @RequestParam("imageNameFile") MultipartFile file) {

        // validate
        if (dataProductBindingResult.hasErrors()) {
            return "admin/product/update";
        }

        Product currentProduct = this.productService.getProductById(data.getId());
        if (currentProduct != null) {
            // update new image
            if (!file.isEmpty()) {
                String img = this.uploadService.handleSaveUploadFile(file, "product");
                currentProduct.setImage(img);
            }

            currentProduct.setName(data.getName());
            currentProduct.setPrice(data.getPrice());
            currentProduct.setQuantity(data.getQuantity());
            currentProduct.setSold(data.getSold());
            currentProduct.setDetailDesc(data.getDetailDesc());
            currentProduct.setShortDesc(data.getShortDesc());
            currentProduct.setFactory(data.getFactory());
            currentProduct.setTarget(data.getTarget());

            this.productService.handleSaveProduct(currentProduct);
        }

        return "redirect:/admin/product";
    }
}
