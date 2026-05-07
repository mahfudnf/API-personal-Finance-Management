package com.personalfinance.management.service.impl;

import com.personalfinance.management.constant.MonthValue;
import com.personalfinance.management.entity.MonthlySpendingLimit;
import com.personalfinance.management.entity.UserEntity;
import com.personalfinance.management.exception.custom.ResourceNotFoundException;
import com.personalfinance.management.model.request.CreateSpendingLimitRequest;
import com.personalfinance.management.model.request.ListSpendingLimitRequest;
import com.personalfinance.management.model.request.UpdateSpendingLimitRequest;
import com.personalfinance.management.model.response.MonthlySpendingLimitResponse;
import com.personalfinance.management.repository.MonthlySpendingLimitRepository;
import com.personalfinance.management.repository.UserRepository;
import com.personalfinance.management.service.MonthlySpendingLimitService;
import com.personalfinance.management.utils.ResponseUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class MonthlySpendingLimitServiceImpl implements MonthlySpendingLimitService {
    private final UserRepository userRepository;
    private final MonthlySpendingLimitRepository monthlySpendingLimitRepository;
    private final ResponseUtils responseUtils;

    public MonthlySpendingLimitServiceImpl(UserRepository userRepository, MonthlySpendingLimitRepository monthlySpendingLimitRepository, ResponseUtils responseUtils) {
        this.userRepository = userRepository;
        this.monthlySpendingLimitRepository = monthlySpendingLimitRepository;
        this.responseUtils = responseUtils;
    }

    @Override
    public MonthlySpendingLimitResponse createSpendingLimit(CreateSpendingLimitRequest request) {
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        MonthlySpendingLimit limit = new MonthlySpendingLimit();
        limit.setLimitValue(request.getLimitValue());
        limit.setMonthValue(request.getMonthValue());
        limit.setYearValue(request.getYearValue());
        limit.setUser(user);

        monthlySpendingLimitRepository.save(limit);

        return responseUtils.toMonthlySpendingLimit(limit);
    }

    @Override
    public MonthlySpendingLimitResponse getCurrentLimit() {
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        LocalDate now = LocalDate.now();

        MonthValue monthEnum = MonthValue.fromInt(now.getMonthValue());
        int currentYear = now.getYear();

        MonthlySpendingLimit limit = monthlySpendingLimitRepository
                .findByUserAndMonthValueAndYearValue(user, monthEnum, currentYear)
                .orElseThrow(() -> new ResourceNotFoundException(
                                String.format("Limit bulan %s tahun %d tidak ditemukan", monthEnum, currentYear))
                        );

        return responseUtils.toMonthlySpendingLimit(limit);
    }

    @Override
    public MonthlySpendingLimitResponse getLimitByMonthAndYear(MonthValue month, Integer year) {
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        MonthlySpendingLimit limit = monthlySpendingLimitRepository.findByUserAndMonthValueAndYearValue(
                user,month,year
        ).orElseThrow(
                () -> new ResourceNotFoundException(
                        String.format("Limit bulan %s tahun %d tidak ditemukan", month, year))
                );

        return responseUtils.toMonthlySpendingLimit(limit);
    }

    @Override
    public Page<MonthlySpendingLimitResponse> listLimits(ListSpendingLimitRequest request) {
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Unauthorized") {});

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<MonthlySpendingLimit> limits;
        if (request.getLimitValue() != null) {
            limits = monthlySpendingLimitRepository.findByUserAndLimitValue(
                    user,
                    request.getLimitValue(),
                    pageable
            );
        } else {
            limits = monthlySpendingLimitRepository.findByUser(user, pageable);
        }

        return limits.map(responseUtils::toMonthlySpendingLimit);
    }

    @Override
    public MonthlySpendingLimitResponse updateLimit(String limitId, UpdateSpendingLimitRequest request) {
        String email = responseUtils.getCurrentUserEmail();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new AuthenticationException("Unauthorized") {});

        MonthlySpendingLimit limit = monthlySpendingLimitRepository.findByUserAndLimitId(user,limitId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                String.format("Limit dengan id %s tidak ditemukan", limitId))
                );

        Optional<MonthlySpendingLimit> existing =
                monthlySpendingLimitRepository.findByUserAndMonthValueAndYearValue(
                        user,
                        request.getMonthValue(),
                        request.getYearValue()
                );

        if (existing.isPresent() && !existing.get().getLimitId().equals(limitId)) {
            throw new IllegalArgumentException("Limit untuk bulan tersebut sudah ada");
        }

        limit.setLimitValue(request.getLimitValue());
        limit.setMonthValue(request.getMonthValue());
        limit.setYearValue(request.getYearValue());

        monthlySpendingLimitRepository.save(limit);

        return responseUtils.toMonthlySpendingLimit(limit);
    }
}
