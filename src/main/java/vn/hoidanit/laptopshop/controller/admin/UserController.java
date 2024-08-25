package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;
import jakarta.validation.Valid;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UserService;
import vn.hoidanit.laptopshop.service.UploadService;

import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class UserController {

    // DI: dependency injection
    private final UserService userService;
    private final UploadService uploadService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UploadService uploadService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> arrUsers = this.userService.getAllUsersByEmail("ngolong.hh@gmail.com");
        System.out.println(arrUsers);

        model.addAttribute("eric", "test");
        return "hello";
    }

    @RequestMapping("/admin/user")
    public String getUserPage(Model model) {
        List<User> users = this.userService.getAllUsers();
        model.addAttribute("dataUsers", users);
        return "admin/user/show";
    }

    @GetMapping("/admin/user/create") // GET
    public String getCreateUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @PostMapping(value = "/admin/user/create") // POST
    public String createUserPage(Model model,
            @ModelAttribute("newUser") @Valid User data,
            BindingResult bindingResult,
            @RequestParam("avatarNameFile") MultipartFile file) {

        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(error.getObjectName());
        }

        String avatar = this.uploadService.handleSaveUploadFile(file, "avatar");
        String hashPassword = this.passwordEncoder.encode(data.getPassword());

        data.setAvatar(avatar);
        data.setPassword(hashPassword);
        data.setRole(this.userService.getRoldByName(data.getRole().getName()));
        this.userService.handleSaveUser(data);

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
    @GetMapping("/admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        User currentUser = this.userService.getUserById(id);
        model.addAttribute("dataUser", currentUser);
        return "admin/user/delete";
    }

    @PostMapping("/admin/user/delete")
    public String postDeleteUser(Model model, @ModelAttribute("dataUser") User data) {
        this.userService.deleteUserById(data.getId());
        return "redirect:/admin/user";
    }

}
