package pl.marcin.production_hall.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.marcin.production_hall.admin.Admin;
import pl.marcin.production_hall.admin.AdminRepository;
import pl.marcin.production_hall.security.AdminDetailsService;
import pl.marcin.production_hall.security.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AdminDetailsService adminDetailsService;

    public AuthController(AdminRepository adminRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AdminDetailsService adminDetailsService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.adminDetailsService = adminDetailsService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request){
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials")
                        );

        if(!admin.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
        }
        boolean passwordMatches=passwordEncoder.matches(request.getPassword(), admin.getPasswordHash());

        if(!passwordMatches){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
        }
        UserDetails userDetails=adminDetailsService.loadUserByUsername(admin.getEmail());
        String token= jwtService.generateToken(userDetails);

        return new LoginResponse(token);

    }
}
