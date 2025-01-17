package cl.vampfern.tenpo_backend_challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalculatorConfigDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1604564898106422422L;

    Float additionalPercentage;

}
