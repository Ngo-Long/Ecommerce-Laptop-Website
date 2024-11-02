package vn.ecommerce.laptopshop.controller.admin;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import vn.ecommerce.laptopshop.domain.User;
import vn.ecommerce.laptopshop.service.UploadService;
import vn.ecommerce.laptopshop.service.UserService;

import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class UserController {

    // DI: dependency injection
    private final UserService userService;
    private final UploadService uploadService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
            UploadService uploadService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping("/admin/user")
    public String getUserPage(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent())
                page = Integer.parseInt(pageOptional.get());
        } catch (Exception e) {
        }

        Pageable pageable = PageRequest.of(page - 1, 5);
        Page<User> pageUsers = this.userService.getAllUsers(pageable);
        List<User> dataUsers = pageUsers.getContent();

        model.addAttribute("dataUsers", dataUsers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageUsers.getTotalPages());

        return "admin/user/show";
    }

    @GetMapping("/admin/user/create") // GET
    public String getCreateUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @PostMapping(value = "/admin/user/create") // POST
    public String createUserPage(Model model,
            @ModelAttribute("newUser") @Valid User dataUser,
            BindingResult newUserBindingResult,
            @RequestParam("avatarNameFile") MultipartFile file) {

        List<FieldError> errors = newUserBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(error.getField() + " - " + error.getDefaultMessage());
        }

        // validate
        if (newUserBindingResult.hasErrors()) {
            return "/admin/user/create";
        }

        String avatar = this.uploadService.handleSaveUploadFile(file, "avatar");
        String hashPassword = this.passwordEncoder.encode(dataUser.getPassword());

        dataUser.setAvatar(avatar);
        dataUser.setPassword(hashPassword);
        dataUser.setRole(this.userService.getRoldByName(dataUser.getRole().getName()));
        this.userService.handleSaveUser(dataUser);

        return "redirect:/admin/user";
    }

    @RequestMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        User dataUser = this.userService.getUserById(id);
        model.addAttribute("dataUser", dataUser);
        return "admin/user/detail";
    }

    // Update
    @RequestMapping("/admin/user/update/{id}")
    public String getUpdateUserPage(Model model, @PathVariable long id) {
        User currentUser = this.userService.getUserById(id);
        model.addAttribute("dataUser", currentUser);
        return "admin/user/update";
    }

    @PostMapping(value = "/admin/user/update")
    public String postUpdateUser(Model model, @ModelAttribute("newUser") User data) {
        User currentUser = this.userService.getUserById(data.getId());
        if (currentUser != null) {
            currentUser.setFullName(data.getFullName());
            currentUser.setEmail(data.getEmail());
            currentUser.setPhone(data.getPhone());
            currentUser.setAddress(data.getAddress());

            this.userService.handleSaveUser(currentUser);
        }
        return "redirect:/admin/user";
    }

    // Delete
    @PostMapping("/admin/user/delete")
    public String postDeleteUser(Model model, @ModelAttribute("dataUser") User data) {
        this.userService.deleteUserById(data.getId());
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        User currentUser = this.userService.getUserById(id);
        model.addAttribute("dataUser", currentUser);
        return "admin/user/delete";
    }

}
