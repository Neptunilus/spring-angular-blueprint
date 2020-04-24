package neptunilus.blueprint.sa.security.filter.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * A login request
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest {

    private String username;

    private String password;

}
