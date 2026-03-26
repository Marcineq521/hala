package pl.marcin.production_hall.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.marcin.production_hall.admin.AdminRepository;
import pl.marcin.production_hall.admin.Admin;

import java.util.List;

@Service
public class AdminDetailsService implements UserDetailsService {
    private final AdminRepository adminRepository;

    public AdminDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Admin admin = adminRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Admin not found: "+email));

        if(!admin.isActive()){
            throw new UsernameNotFoundException("Admin is inactive: "+email);
        }

        return new User(
                admin.getEmail(),
                admin.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

    }
}
