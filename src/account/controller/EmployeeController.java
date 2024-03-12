package account.controller;

import account.model.dto.data.AppUserDTO;
import account.model.dto.responses.UserPaymentDTO;
import account.service.SalaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empl")
public class EmployeeController {

    private final SalaryService salaryService;

    public EmployeeController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }


    @GetMapping("/payment")
    public ResponseEntity<?> getUserPayment(Authentication auth,
                                            @RequestParam(required = false)
                                            Map<String, String> param
    ) {
        System.out.println("get patyments");
        System.out.println("name" + auth.getName());
        if (auth != null) {
            AppUserDTO details = (AppUserDTO) auth.getPrincipal();
            System.out.println("should be null" + details.getUsername());

            if (param.isEmpty()) {
                List<UserPaymentDTO> userPaymentDTO = salaryService.getAllSalaries(
                        details.getUsername());
                return ResponseEntity.ok(userPaymentDTO);
            } else if (param.containsKey("period")) {
                return ResponseEntity.ok(
                        salaryService.getSalary(details.getUsername(),
                                param.get("period")));

            } else {
                return ResponseEntity.badRequest().body("Invalid request");
            }
        }
        return ResponseEntity.badRequest().body("Invalid request");
    }

}
