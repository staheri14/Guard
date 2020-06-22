package protocol;

public enum RequestType {
    // Handled by PingHandler (for testing):
    PING,

    // Handled by SkipNode:
    GET_INFO,
    GET_LEFT_NODE,
    GET_RIGHT_NODE,
    SET_LEFT_NODE,
    SET_RIGHT_NODE,
    SEARCH_BY_NUM_ID,
    ROUTE_SEARCH_NUM_ID,
    FIND_LADDER,
    INSERT,

    // Handled by TTP:
    TTP_REGISTER,
    TTP_AUTH_CHALLENGE,
    TTP_RETRIEVE_GUARDS,
    TTP_RETRIEVE_GUARD_KEYS,

    // Handled by AuthNode:
    NODE_REGISTER,
    NODE_CONSTRUCT,
    NODE_ASSIGN,
    GET_TABLE_PROOF_ENTRY,
    GET_GUARD_NEIGHBOR,
    SET_GUARD_NEIGHBOR,
    INFORM_GUARD,
    GET_GUARD_SIGNATURE
}
