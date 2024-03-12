package account.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp.format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
