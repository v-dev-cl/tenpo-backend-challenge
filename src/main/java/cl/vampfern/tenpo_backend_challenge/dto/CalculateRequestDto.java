package cl.vampfern.tenpo_backend_challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalculateRequestDto {

    private Float num1;

    private Float num2;

}
