package account.service;

import account.exception.SalaryInfoNotMatchException;
import account.model.dto.requests.SalaryUploadRequestDTO;
import account.model.dto.responses.UserPaymentDTO;
import account.model.entity.AppUser;
import account.model.entity.Salary;
import account.model.record.SalaryUploadResponse;
import account.repository.AppUserRepository;
import account.repository.SalaryRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final AppUserRepository appUserRepository;

    public SalaryService(SalaryRepository salaryRepository,
                         AppUserRepository appUserRepository) {
        this.salaryRepository = salaryRepository;
        this.appUserRepository = appUserRepository;
    }

    private static LocalDate StringToDate(String date) {
        String[] dateParts = date.split("-");
        return LocalDate.of(Integer.parseInt(dateParts[1]),
                Integer.parseInt(dateParts[0]), 1);
    }

    private static List<UserPaymentDTO> sortSalariesByPeriod(
            List<UserPaymentDTO> salaries) {
        salaries.sort(Comparator.comparing(UserPaymentDTO::getPeriod));
        return salaries;
    }

    public SalaryUploadResponse uploadSalary(
            List<SalaryUploadRequestDTO> salaryUploadRequestDTOS) {
        for (SalaryUploadRequestDTO req : salaryUploadRequestDTOS) {

            Optional<AppUser> user = appUserRepository.findAppUserByEmail(
                    req.getEmployee());
            if (user.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }

            if (!user.get().getEmail().equals(req.getEmployee())) {
                throw new SalaryInfoNotMatchException("Email does not match");
            }


            Salary salary = new Salary();
            salary.setSalary(req.getSalary());
            salary.setPeriod(StringToDate(req.getPeriod()));
            salary.setEmail(req.getEmployee());
            salary.setAppUser(user.get());

            salaryRepository.save(salary);

        }
        return new SalaryUploadResponse("Added successfully!");

    }

    public SalaryUploadResponse updateSalary(
            SalaryUploadRequestDTO salaryUploadRequestDTO) {
        System.out.println(salaryUploadRequestDTO.toString());
        System.out.println("updateSalary called");


        Optional<AppUser> user = appUserRepository.findAppUserByEmail(
                salaryUploadRequestDTO.getEmployee());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        System.out.println("user fetched");

        Optional<Salary> salaryToUpdate = salaryRepository.findSalaryByEmailAndPeriod(
                salaryUploadRequestDTO.getEmployee(),
                StringToDate(salaryUploadRequestDTO.getPeriod()));
        System.out.println("salaryToUpdate fetched");

        if (salaryToUpdate.isEmpty()) {
            throw new SalaryInfoNotMatchException("Period does not exist");
        }

        if (!user.get().getEmail().equals(
                salaryUploadRequestDTO.getEmployee()) ||
            !salaryToUpdate.get()
                    .getEmail()
                    .equals(salaryUploadRequestDTO.getEmployee())) {
            throw new SalaryInfoNotMatchException("Email does not match");
        }


        Salary salary = salaryToUpdate.get();

        salary.setSalary(salaryUploadRequestDTO.getSalary());
        salary.setPeriod(StringToDate(salaryUploadRequestDTO.getPeriod()));
        salaryRepository.save(salary);

        return new SalaryUploadResponse("Updated successfully!");

    }

    public List<UserPaymentDTO> getAllSalaries(String email) {
        List<Salary> salaries = salaryRepository.findAllByEmail(email);
        salaries.sort(Comparator.comparing(Salary::getPeriod).reversed());

        List<UserPaymentDTO> allSalaries = new ArrayList<>();
        salaries.forEach(salary -> {

            UserPaymentDTO builder = new UserPaymentDTO.Builder()
                    .setName(salary.getAppUser().getName())
                    .setLastname(salary.getAppUser().getLastname())
                    .setPeriod(salary.getPeriod())
                    .setSalary(salary.getSalary()).build();
            allSalaries.add(builder);
        });


        return allSalaries;
    }

    public UserPaymentDTO getSalary(String email, String period) {

        Optional<Salary> salary =
                salaryRepository.findSalaryByEmailAndPeriod(email,
                        StringToDate(period));
        if (salary.isEmpty()) {
            throw new SalaryInfoNotMatchException("Period does not exist");
        }

        return new UserPaymentDTO.Builder()
                .setName(salary.get().getAppUser().getName())
                .setLastname(salary.get().getAppUser().getLastname())
                .setPeriod(salary.get().getPeriod())
                .setSalary(salary.get().getSalary()).build();
    }


}