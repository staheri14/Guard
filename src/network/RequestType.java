package network;

/**
 * Represents the possible types of a request.
 */
public enum RequestType {

    // Handled by PingHandler (for testing):
    PING("ping"),

    // Handled by SkipNode:
    GET_INFO("get_info"),
    GET_LEFT_NODE("get_left_node"),
    GET_RIGHT_NODE("get_right_node"),
    SET_LEFT_NODE("set_left_node"),
    SET_RIGHT_NODE("set_right_node"),
    SEARCH_BY_NUM_ID("search"),
    ROUTE_SEARCH_NUM_ID("route_search"),
    FIND_LADDER("find_ladder"),
    INSERT("insert"),

    // Handled by TTP:
    TTP_REGISTER("ttp_register"),
    TTP_AUTH_CHALLENGE("auth_challenge"),
    TTP_RETRIEVE_GUARDS("retrieve_guards"),
    TTP_RETRIEVE_GUARD_INFO("retrieve_guard_info"),

    // Handled by AuthNode:
    NODE_REGISTER("node_register"),
    NODE_CONSTRUCT("node_construct"),
    NODE_ASSIGN("node_assign"),
    GET_TABLE_PROOF_ENTRY("get_table_proof_entry"),
    GET_GUARD_NEIGHBOR("get_guard_neighbor"),
    SET_GUARD_NEIGHBOR("set_guard_neighbor"),
    INFORM_GUARD("inform_guard"),
    GET_GUARD_SIGNATURE("get_guard_signature"),

    // Handled by NodeUserInterface:
    JOIN("join"),
    SEARCH("search"),
    INITIALIZE("initialize"),
    EXPERIMENT("experiment"),
    TERMINATE("terminate");

    public final String str;

    RequestType(String str) {
        this.str = str;
    }
}
