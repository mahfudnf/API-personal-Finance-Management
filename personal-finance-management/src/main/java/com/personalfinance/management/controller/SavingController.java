package com.personalfinance.management.controller;

import com.personalfinance.management.model.PagingResponse;
import com.personalfinance.management.model.WebResponse;
import com.personalfinance.management.model.saving.*;
import com.personalfinance.management.service.SavingService;
import com.personalfinance.management.service.impl.SavingServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SavingController {

    @Autowired
    private SavingService savingService;

    @PostMapping(
            path = "/api/savings",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SavingResponse> createSaving(@AuthenticationPrincipal User user,@RequestBody @Valid CreateSavingRequest request){
        SavingResponse response = savingService.createSaving(user.getUsername(), request);
        return WebResponse.<SavingResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/api/savings/{savingId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SavingResponse> getSaving(@AuthenticationPrincipal User user,@PathVariable("savingId") String savingId){
        SavingResponse response = savingService.getSaving(user.getUsername(), savingId);
        return WebResponse.<SavingResponse>builder().data(response).build();
    }

    @PostMapping(
            path = "/api/savings/{savingId}/saving_transaction",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> createSavingTransaction(
            @AuthenticationPrincipal User user,
            @PathVariable("savingId") String savingId,
            @RequestBody @Valid CreateSavingTransactionRequest request){

        savingService.createSavingTransaction(user.getUsername(), savingId,request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/api/savings/{savingId}/saving_transaction/progress",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SavingProgressResponse> getSavingProgress(@AuthenticationPrincipal User user,@PathVariable("savingId") String savingId){
        SavingProgressResponse response = savingService.getSavingProgress(user.getUsername(), savingId);
        return WebResponse.<SavingProgressResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/api/savings",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<SavingResponse>> listSaving(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "nameSaving",required = false) String nameSaving,
            @RequestParam(value = "page", required = false,defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false,defaultValue = "10") Integer size
    ){

        ListSavingRequest request = ListSavingRequest.builder()
                .nameSaving(nameSaving)
                .page(page)
                .size(size)
                .build();

        Page<SavingResponse> savingResponses = savingService.listSaving(user.getUsername(), request);
        return WebResponse.<List<SavingResponse>>builder()
                .data(savingResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(savingResponses.getNumber())
                        .totalPage(savingResponses.getTotalPages())
                        .size(savingResponses.getSize())
                        .build())
                .build();

    }

    @PutMapping(
            path = "/api/savings/{savingId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SavingResponse> editSaving(
            @AuthenticationPrincipal User user,
            @PathVariable("savingId") String savingId,
            @RequestBody @Valid CreateSavingRequest request){

        SavingResponse response = savingService.editSaving(user.getUsername(), savingId,request);
        return WebResponse.<SavingResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "/api/savings/{savingId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteSaving(@AuthenticationPrincipal User user,@PathVariable("savingId") String savingId){
        savingService.deleteSaving(user.getUsername(), savingId);
        return WebResponse.<String>builder().data("OK").build();
    }
}
