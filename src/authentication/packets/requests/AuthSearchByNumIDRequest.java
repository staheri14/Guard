package authentication.packets.requests;

import misc.Logger;
import skipnode.packets.requests.SearchByNumIDRequest;

public class AuthSearchByNumIDRequest extends SearchByNumIDRequest {

    public AuthSearchByNumIDRequest(int target) {
        super(target);
        this.auth = true;
        this.phase = Logger.Phase.SEARCH;
    }
}
