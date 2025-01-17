package cl.vampfern.tenpo_backend_challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "endpoint_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndpointHistory {

    @Id
    private long id;

    @Column
    private String ip;

    @Column
    private String endpoint;

    @Column
    private String parameters;

    @Column
    private String response;

    @Column
    private String error;

    @Column("created_at")
    private LocalDateTime created_at;

    @Column("updated_at")
    private LocalDateTime update_at;

}
