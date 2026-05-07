package com.personalfinance.management.service.impl;

import com.personalfinance.management.entity.Income;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.exception.custom.ResourceNotFoundException;
import com.personalfinance.management.model.request.CreateIncomeRequest;
import com.personalfinance.management.model.response.IncomeResponse;
import com.personalfinance.management.model.request.ListIncomeRequest;
import com.personalfinance.management.model.request.UpdateIncomeRequest;
import com.personalfinance.management.repository.IncomeRepository;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.service.IncomeService;
import com.personalfinance.management.utils.ResponseUtils;
import org.springframework.data.domain.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class IncomeServiceImpl implements IncomeService {
    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;
    private final ResponseUtils responseUtils;

    public IncomeServiceImpl(IncomeRepository incomeRepository, UserRepository userRepository, ResponseUtils responseUtils) {
        this.incomeRepository = incomeRepository;
        this.userRepository = userRepository;
        this.responseUtils = responseUtils;
    }

    public IncomeResponse createIncome(CreateIncomeRequest request){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        Income income = new Income();
        income.setAmount(request.getAmount());
        income.setCategory(request.getCategory());
        income.setDescription(request.getDescription());
        income.setUser(user);
        incomeRepository.save(income);

        return responseUtils.toIncomeResponse(income);
    }

    public IncomeResponse getIncome(String incomeId){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        Income income = incomeRepository.findByUserAndIncomeId(user,incomeId)
                .orElseThrow(()-> new ResourceNotFoundException("Income dengan id " + incomeId + " tidak ditemukan"));

        return responseUtils.toIncomeResponse(income);
    }

    public Page<IncomeResponse> listIncome(ListIncomeRequest request) {
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Unauthorized") {});

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<Income> incomes;
        if (request.getCategory() != null) {
            incomes = incomeRepository.findByUserAndCategoryContaining(
                    user,
                    request.getCategory(),
                    pageable
            );
        } else {
            incomes = incomeRepository.findByUser(user, pageable);
        }

        return incomes.map(responseUtils::toIncomeResponse);
    }

    public IncomeResponse editIncome(String incomeId,UpdateIncomeRequest request){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});
        Income income = incomeRepository.findByUserAndIncomeId(user,incomeId)
                .orElseThrow(()-> new ResourceNotFoundException("Income dengan id " + incomeId + " tidak ditemukan"));

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

        return responseUtils.toIncomeResponse(income);
    }

    public void deleteIncome(String incomeId){
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});
        Income income = incomeRepository.findByUserAndIncomeId(user,incomeId)
                .orElseThrow(()-> new ResourceNotFoundException("Income dengan id " + incomeId + " tidak ditemukan"));

        incomeRepository.delete(income);
    }

}
