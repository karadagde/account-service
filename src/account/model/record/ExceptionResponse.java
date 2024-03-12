package account.model.record;


import java.util.Date;


public record ExceptionResponse(Date timestamp, String message,
                                String details) {
}