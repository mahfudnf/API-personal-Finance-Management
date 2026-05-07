package com.personalfinance.management.controller;

import com.personalfinance.management.model.request.CreateSavingRequest;
import com.personalfinance.management.model.request.CreateSavingTransactionRequest;
import com.personalfinance.management.model.request.ListSavingRequest;
import com.personalfinance.management.model.response.PagingResponse;
import com.personalfinance.management.model.response.SavingProgressResponse;
import com.personalfinance.management.model.response.SavingResponse;
import com.personalfinance.management.model.response.WebResponse;
import com.personalfinance.management.service.SavingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SavingController {
    private final SavingService savingService;

    public SavingController(SavingService savingService) {
        this.savingService = savingService;
    }

    @PostMapping(
            path = "/api/savings",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<SavingResponse>> createSaving(@RequestBody @Valid CreateSavingRequest request){
        SavingResponse response = savingService.createSaving(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.<SavingResponse>builder().data(response).build());
    }

    @GetMapping(
            path = "/api/savings/{savingId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<SavingResponse>> getSaving(@PathVariable("savingId") String savingId){
        SavingResponse response = savingService.getSaving(savingId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<SavingResponse>builder().data(response).build());
    }

    @PostMapping(
            path = "/api/savings/{savingId}/saving_transaction",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> createSavingTransaction(
            @PathVariable("savingId") String savingId,
            @RequestBody @Valid CreateSavingTransactionRequest request){

        savingService.createSavingTransaction(savingId,request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebResponse.<String>builder().data("OK").build());
    }

    @GetMapping(
            path = "/api/savings/{savingId}/saving_transaction/progress",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<SavingProgressResponse>> getSavingProgress(@PathVariable("savingId") String savingId){
        SavingProgressResponse response = savingService.getSavingProgress(savingId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<SavingProgressResponse>builder().data(response).build());
    }

    @GetMapping(
            path = "/api/savings",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<SavingResponse>>> listSaving(
            @RequestParam(value = "nameSaving",required = false) String nameSaving,
            @RequestParam(value = "page", required = false,defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false,defaultValue = "10") Integer size
    ){

        ListSavingRequest request = ListSavingRequest.builder()
                .nameSaving(nameSaving)
                .page(page)
                .size(size)
                .build();

        Page<SavingResponse> savingResponses = savingService.listSaving(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<List<SavingResponse>>builder()
                        .data(savingResponses.getContent())
                        .paging(PagingResponse.builder()
                                .currentPage(savingResponses.getNumber())
                                .totalPage(savingResponses.getTotalPages())
                                .size(savingResponses.getSize())
                                .build())
                        .build());

    }

    @PutMapping(
            path = "/api/savings/{savingId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<SavingResponse>> editSaving(
            @PathVariable("savingId") String savingId,
            @RequestBody @Valid CreateSavingRequest request){

        SavingResponse response = savingService.editSaving(savingId,request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<SavingResponse>builder().data(response).build());
    }

    @DeleteMapping(
            path = "/api/savings/{savingId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> deleteSaving(@PathVariable("savingId") String savingId){
        savingService.deleteSaving(savingId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(WebResponse.<String>builder().data("OK").build());
    }
}
