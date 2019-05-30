package com.dyptan;

import com.dyptan.model.Filter;
import com.dyptan.model.Role;
import com.dyptan.model.User;
import com.dyptan.repository.UserRepository;
import com.dyptan.service.AuthService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest

public class SecurityTest {

    @Autowired
    User user;

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;


    @Test
    public void contextLoads() throws Exception {
        assertThat(user).isNotNull();
        assertThat(authService).isNotNull();
    }


    @Test
    public void saveWithEncryptedPassword() {
        user.setUsername("admin");
        user.setPassword("admin");
        Set roles = new HashSet();
        roles.add(Role.Roles.ADMIN);
        roles.add(Role.Roles.USER);
        user.setRoles(roles);

        Filter filter = new Filter();
        filter.setModels("[{Astra}]");
        filter.setBrands("Opel");
        filter.setYearTo((short) 2018);
        filter.setPeriodRange(3);
        filter.setPeriodMultiplier(Filter.Period.WEEKS);

        user.addFilter(filter);

        Filter filter2 = new Filter();
        filter2.setBrands("honda");

        user.addFilter(filter2);

        authService.saveEncrypted(user);
        assertThat("$2a$10$X4QTNFtpbheRAt8Ay3jE9OxPqvTLL2xHSu0k0fhvXeCKE3EACB8om").contains("$2a$10$");
    }
}
