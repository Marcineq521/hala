package pl.marcin.production_hall.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.marcin.production_hall.admin.Admin;
import pl.marcin.production_hall.admin.AdminRepository;


@Component
public class AdminSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private PasswordEncoder passwordEncoder;

    public AdminSeeder(AdminRepository adminRepository, PasswordEncoder passwordEncoder){
        this.adminRepository=adminRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public void run(String...args){
        String email="admin@hall.local";
        String rawPassword="Admin512!@";

        if(adminRepository.existsByEmail(email)){
            return;
        }

        Admin admin=new Admin();
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(rawPassword));
        admin.setActive(true);

        adminRepository.save(admin);

        System.out.println("ADMIN CREATED: "+email+"/"+rawPassword);
    }


}
