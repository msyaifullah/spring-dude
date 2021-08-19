package com.yyggee.eggs.model.ds1.player;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Data
@AllArgsConstructor
@Embeddable
public class Company {

    @Column(name = "company_name")
    private String name;
    private String catchPhrase;
    private String bs;

    public Company() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(name, company.name) &&
                Objects.equals(catchPhrase, company.catchPhrase) &&
                Objects.equals(bs, company.bs);
    }
}
