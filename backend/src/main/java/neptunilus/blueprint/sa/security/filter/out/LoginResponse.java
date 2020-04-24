package neptunilus.blueprint.sa.security.filter.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * A login response
 */
@Data
@JsonInclude(NON_NULL)
public class LoginResponse {

    @JsonProperty("access_token")
    private String accessToken;

}
