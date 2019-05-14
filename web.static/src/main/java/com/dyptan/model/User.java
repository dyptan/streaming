package com.dyptan.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Component
@Table(name = "Users")
public class User implements Serializable{
    @Id
    private String name;
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Filter> filters = new ArrayList<>();

    public void addFilter(Filter filter) {
        filters.add(filter);
    }
    public void clearFilters() {
        filters.clear();
    }
    public void deleteFilter(int id) {
        filters.remove(id);
    }
}
