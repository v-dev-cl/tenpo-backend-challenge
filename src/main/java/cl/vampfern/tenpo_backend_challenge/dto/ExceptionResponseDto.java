package cl.vampfern.tenpo_backend_challenge.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionResponseDto {

    private String mensaje;

}
