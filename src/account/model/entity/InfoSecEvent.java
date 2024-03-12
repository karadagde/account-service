package account.model.entity;

import account.model.enums.LoggingEvents;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "info_sec_event")
@Getter
@Setter
@NoArgsConstructor
public class InfoSecEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private LoggingEvents action;

    @NotNull
    private String subject;
    @NotNull
    private String object;
    @NotNull
    private String path;

}
