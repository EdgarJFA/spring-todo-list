package mz.com.edgaramado.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.com.edgaramado.todolist.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
    @Autowired
    private UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var servletPath = request.getServletPath();
        if (servletPath.startsWith("/tasks")) {
            // Get authentication (user and password)
            var authorization = new String(Base64.getDecoder().decode(request.getHeader("Authorization").substring("Basic".length()).trim()));
            String[] credentials = authorization.split(":");
            String username = credentials[0];
            String password = credentials[1];
            // Validate user
            var user = this.userRepository.findByUserName(username);
            if (user == null) {
                response.sendError(401);
            } else {
                // Validate password
                var passwordVerified = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerified.verified) {
                    // go to
                    request.setAttribute("userId", user.getId());
                    filterChain.doFilter(request,response);
                } else {
                    response.sendError(401);
                }
            }
        } else {
            filterChain.doFilter(request,response);
        }

    }
}
