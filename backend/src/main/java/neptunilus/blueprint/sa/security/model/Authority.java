package neptunilus.blueprint.sa.security.model;

/**
 * The authorities used to define which actions are allowed.
 */
public enum Authority {

    // category related

    CREATE_CATEGORY,
    UPDATE_CATEGORY,
    DELETE_CATEGORY,

    // product related

    CREATE_PRODUCT,
    UPDATE_PRODUCT,
    DELETE_PRODUCT,

    // user related

    CREATE_USER,
    UPDATE_USER,
    DELETE_USER;
}
