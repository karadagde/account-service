package account.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "salary",
       uniqueConstraints = {@UniqueConstraint(
               columnNames = {"email", "period"})})
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long salary;


    private LocalDate period;

    @Column(name = "email")
    private String email;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appUser_id")
    private AppUser appUser;


}
