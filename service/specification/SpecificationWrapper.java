package com.epam.parking.service.specification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@AllArgsConstructor
public class SpecificationWrapper<T> {
    private Specification<T> specification;

    public void and(Specification<T> modifier){
        specification = specification.and(modifier);
    }

    public void or(Specification<T> modifier){
        specification = specification.or(modifier);
    }
}
