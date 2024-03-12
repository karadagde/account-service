package account.model.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter

public class UserPaymentDTO {
    private String name;
    private String lastname;
    private String period;
    private String salary;


    private UserPaymentDTO(String name, String lastname, String period,
                           String salary) {
        this.name = name;
        this.lastname = lastname;
        this.period = period;
        this.salary = salary;
    }


    public static class Builder {
        private String name;
        private String lastname;
        private String period;
        private String salary;

        public Builder() {
        }

        public Builder setName(String name) {

            this.name = name;
            return this;
        }

        public Builder setLastname(String lastname) {

            this.lastname = lastname;
            return this;
        }

//        public Builder setPeriod(String period) {
//            String[] dateParts = period.split("-");
//
//            LocalDate date = LocalDate.of(Integer.parseInt(dateParts[1]),
//                    Integer.parseInt(dateParts[0]), 1);
//            String month =
//                    date.getMonth()
//                            .toString()
//                            .substring(0, 1)
//                            .concat(date.getMonth()
//                                    .toString()
//                                    .substring(1)
//                                    .toLowerCase());
//            this.period = month.concat("-").concat(dateParts[1]);
//
//            return this;
//        }

        public Builder setPeriod(LocalDate period) {
            this.period = period.format(
                    DateTimeFormatter.ofPattern("MMMM-yyyy"));

            return this;
        }


        public Builder setSalary(Long salary) {

            int dollars = (int) (salary / 100);
            int cents = (int) (salary % 100);
            this.salary = dollars + " dollar(s) " + cents + " cent(s)";
            return this;
        }

        public UserPaymentDTO build() {
            return new UserPaymentDTO(name, lastname, period, salary);
        }
    }

}
