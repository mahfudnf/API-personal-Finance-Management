package com.personalfinance.management.service.impl;

import com.personalfinance.management.entity.Income;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.model.income.CreateIncomeRequest;
import com.personalfinance.management.model.income.IncomeResponse;
import com.personalfinance.management.model.income.ListIncomeRequest;
import com.personalfinance.management.model.income.UpdateIncomeRequest;
import com.personalfinance.management.repository.IncomeRepository;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.service.IncomeService;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Validated
public class IncomeServiceImpl implements IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;


    @Transactional
    public IncomeResponse createIncome(String email,@Valid CreateIncomeRequest request){

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Income income = new Income();
        income.setAmount(request.getAmount());
        income.setCategory(request.getCategory());
        income.setCreatedAt(LocalDateTime.now());
        income.setDescription(request.getDescription());
        income.setUser(user);
        incomeRepository.save(income);

        return toIncomeResponse(income);
    }

    @Transactional(readOnly = true)
    public IncomeResponse getIncome(String email,String incomeId){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Income income = incomeRepository.findByUserAndIncomeId(user,incomeId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"income not found"));

        return toIncomeResponse(income);
    }

    @Transactional
    public Page<IncomeResponse> listIncome(String email,@Valid ListIncomeRequest request){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        Specification<Income> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("user"),user));

            if (Objects.nonNull(request.getCategory())){
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("category"),"%" + request.getCategory() + "%")
                ));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Income> incomes = incomeRepository.findAll(specification,pageable);
        List<IncomeResponse> incomeResponses = incomes.getContent().stream()
                .map(this::toIncomeResponse)
                .toList();

        return new PageImpl<>(incomeResponses,pageable,incomes.getTotalElements());
    }

    @Transactional
    public IncomeResponse editIncome(String email, String incomeId,@Valid UpdateIncomeRequest request){

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));
        Income income = incomeRepository.findByUserAndIncomeId(user,incomeId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"income not found"));

        if (Objects.nonNull(request.getAmount())){
            income.setAmount(request.getAmount());
        }

        if (Objects.nonNull(request.getCategory())){
            income.setCategory(request.getCategory());
        }

        if (Objects.nonNull(request.getDescription())){
            income.setDescription(request.getDescription());
        }

        incomeRepository.save(income);

        return toIncomeResponse(income);
    }

    @Transactional
    public void deleteIncome(String email,String incomeId){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));
        Income income = incomeRepository.findByUserAndIncomeId(user,incomeId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"income not found"));

        incomeRepository.delete(income);
    }

    // Method Helper
    private IncomeResponse toIncomeResponse(Income income){
        return IncomeResponse.builder()
                .incomeId(income.getIncomeId())
                .amount(income.getAmount())
                .category(income.getCategory())
                .createdAt(income.getCreatedAt())
                .description(income.getDescription())
                .build();
    }
}
