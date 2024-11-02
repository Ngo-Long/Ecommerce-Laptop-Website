package vn.ecommerce.laptopshop.config;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import vn.ecommerce.laptopshop.domain.User;
import vn.ecommerce.laptopshop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class CustomSuccessHandle implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private UserService userService;

    protected String determineTargetUrl(final Authentication authentication) {
        Map<String, String> roleTargetUrlMap = new HashMap<>();
        roleTargetUrlMap.put("ROLE_USER", "/");
        roleTargetUrlMap.put("ROLE_ADMIN", "/admin");

        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (roleTargetUrlMap.containsKey(authorityName)) {
                return roleTargetUrlMap.get(authorityName);
            }
        }

        throw new IllegalStateException();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
        clearAuthenticationAttributes(request, authentication);
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request,
            Authentication authentication) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        // get email
        String email = authentication.getName();

        // get user
        User dataUser = this.userService.getUserByEmail(email);
        if (dataUser == null) {
            return;
        }

        session.setAttribute("id", dataUser.getId());
        session.setAttribute("email", dataUser.getEmail());
        session.setAttribute("avatar", dataUser.getAvatar());
        session.setAttribute("fullName", dataUser.getFullName());
        int sum = dataUser.getCart() != null ? dataUser.getCart().getSum() : 0;
        session.setAttribute("sum", sum);
    }

}
