package authentication.packets.requests;

import skipnode.packets.requests.SearchByNumIDRequest;

public class AuthSearchByNumIDRequest extends SearchByNumIDRequest {

    public AuthSearchByNumIDRequest(int target) {
        super(target);
    }
}
