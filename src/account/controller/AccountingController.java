package account.controller;

import account.model.dto.requests.SalaryUploadRequestDTO;
import account.model.record.SalaryUploadResponse;
import account.service.SalaryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/acct")
@Validated
public class AccountingController {


    private final SalaryService salaryService;

    public AccountingController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }


    @PostMapping(value = "/payments", consumes = "application/json")
    public ResponseEntity<SalaryUploadResponse> uploadPayment(@Valid
                                                              @RequestBody
                                                              List<SalaryUploadRequestDTO> salaryUploadRequestDTOS) {

        return ResponseEntity.ok(salaryService.uploadSalary(
                salaryUploadRequestDTOS));
    }

    @PutMapping(value = "/payments", consumes = "application/json")
    public ResponseEntity<SalaryUploadResponse> updatePayment(
            @Valid @RequestBody
            SalaryUploadRequestDTO salaryUploadRequestDTO) {

        return ResponseEntity.ok(salaryService.updateSalary(
                salaryUploadRequestDTO));
    }


}
