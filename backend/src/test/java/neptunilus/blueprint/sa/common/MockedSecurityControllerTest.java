package neptunilus.blueprint.sa.common;

import neptunilus.blueprint.sa.security.service.impl.AuthenticatedUserDetailsService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

@WithMockUser
public abstract class MockedSecurityControllerTest {

    @MockBean
    protected AuthenticatedUserDetailsService authenticatedUserDetailsService;

}
