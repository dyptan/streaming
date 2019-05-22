package com.dyptan;

import com.dyptan.model.Filter;
import com.dyptan.model.User;
import com.dyptan.repository.UserRepository;
import com.dyptan.service.SearchService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersistanceTest {

    @Autowired
    User user;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SearchService searchService;

    @Test
    public void contextLoads() throws Exception {
        assertThat(user).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void TestUserPersistance(){

        user.setName("Guest");
        user.setPassword("22222");

        Filter filter = new Filter();
        filter.setModels("[{Astra}]");
        filter.setBrands("Opel");
        filter.setYearTo((short)2018);
        filter.setPeriodRange(3);
        filter.setPeriodMultiplier(Filter.Period.WEEKS);

        user.addFilter(filter);

        Filter filter2 = new Filter();
        filter2.setBrands("honda");

        user.addFilter(filter2);

        userRepository.save(user);
        Assert.assertEquals("Guest", userRepository.findByName("Guest").get().getName());
    }

    @Test
    public void TestFilterPersistance(){
        Assert.assertEquals(2018, userRepository.findByName("Guest").get().getFilters().get(0).getYearTo());
    }
}
