package com.dyptan.controller;

import com.dyptan.model.Filter;
import com.dyptan.model.User;
import com.dyptan.repository.UserRepository;
import com.dyptan.service.SearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class LoginController {
    Logger log = Logger.getLogger(this.getClass().getName());

    @Autowired
    UserRepository userRepository;
    @Autowired
    SearchService service;
    @Autowired
    ObjectMapper objectMapper;


//    SecurityContext context = SecurityContextHolder.getContext();
//    Authentication authentication = context.getAuthentication();

//    @PostMapping("/login")
//    public String auth(@RequestParam(name = "userName", required = false, defaultValue = "Guest") String userName, Model model, HttpSession session) {
//        if (userRepository.existsByUsername(userName)) {
//            model.addAttribute("filters", userRepository.findByUsername(userName).get().getFilters());
//            model.addAttribute("brands", service.getBrands());
//            model.addAttribute("userName", userName);
//            session.setAttribute("userName", userName);
//            return "home";
//        } else {
//            model.addAttribute("userNotFound", true);
//            return "login";
//        }
//    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/stream")
    public String stream() {
        return "stream";
        }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/signin")
    public String register() { return "signin"; }

    @GetMapping("/search")
    public String search(Model model, HttpSession httpSession, UsernamePasswordAuthenticationToken principal) {
        User user = (User) principal.getPrincipal();
        model.addAttribute("filters", user.getFilters());
        model.addAttribute("brands", service.getBrands());
        model.addAttribute("userName", user.getUsername());
        httpSession.setAttribute("userName", user.getUsername());

        return "search";
    }

    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String searchWithfilter(@ModelAttribute Filter filter,
                                   @RequestParam(name="saveFilter", required=false, defaultValue="false") Boolean saveFilter,
                                   HttpServletRequest httpRequest,
                                   HttpSession session,
                                   Model model) {

        log.info("Filter is built: "+filter);
        List<Map<String, Object>> documents = service.getHitsAsList(filter);

        log.fine("Docs found : "+documents.size()+"\n Content is: "+documents);

        model.addAttribute("documents", documents);
        model.addAttribute("brands", service.getBrands());
        model.addAttribute("userName", session.getAttribute("userName").toString());

        if (saveFilter) {

            String fooResourceUrl
                    = String.format("http://%s:%d/user/%s/filters", httpRequest.getServerName(), httpRequest.getServerPort(), session.getAttribute("userName").toString());

            ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            RestTemplate restTemplate = new RestTemplate(requestFactory);

            HttpEntity<Filter> request = new HttpEntity<>(filter);

            restTemplate.postForEntity(fooResourceUrl, request, String.class);
        }

        return "search";
    }
}

