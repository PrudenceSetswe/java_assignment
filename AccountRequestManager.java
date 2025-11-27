import java.util.List;

public class AccountRequestManager {

    // Static list to hold all account requests
    private static List<AccountRequest> requests;

    // Set the requests (used at startup or to load requests)
    public static void setRequests(List<AccountRequest> reqs) {
        requests = reqs;
    }

    // Get the current list of requests
    public static List<AccountRequest> getRequests() {
        return requests;
    }
}
