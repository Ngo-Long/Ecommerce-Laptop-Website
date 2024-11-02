package vn.ecommerce.laptopshop.domain.dto;

import jakarta.validation.constraints.Size;
import vn.ecommerce.laptopshop.service.validator.RegisterChecked;
import jakarta.validation.constraints.NotEmpty;

@RegisterChecked
public class RegisterDTO {
    @Size(min = 3, message = "Tên phải có tối thiểu 3 ký tự")
    private String firstName;
    private String lastName;

    // @Email(message = "Email không hợp lệ!", regexp =
    // "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[azA-Z0-9.-]+$")
    @NotEmpty(message = "Email không được để trống!")
    private String email;

    private String password;

    @Size(min = 3, message = "Nhập lại mật khẩu phải có tối thiểu 3 ký tự")
    private String confirmPassword;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}