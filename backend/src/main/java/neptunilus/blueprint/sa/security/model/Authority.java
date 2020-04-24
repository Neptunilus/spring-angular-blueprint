package neptunilus.blueprint.sa.security.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * The authorities used to define which actions are allowed.
 */
public enum Authority implements GrantedAuthority {

    // category related
    CREATE_CATEGORY,
    READ_CATEGORY,
    UPDATE_CATEGORY,
    DELETE_CATEGORY,

    // product related
    CREATE_PRODUCT,
    READ_PRODUCT,
    UPDATE_PRODUCT,
    DELETE_PRODUCT,

    // user related
    CREATE_USER,
    READ_USER,
    UPDATE_USER,
    DELETE_USER,

    // user role related
    READ_USER_ROLE;

    @Override
    public String getAuthority() {
        return name();
    }

}
