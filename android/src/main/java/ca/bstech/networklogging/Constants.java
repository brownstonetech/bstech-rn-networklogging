package ca.bstech.networklogging;

public interface Constants {

    String NETWORK_INFO_EVENT = "@BST/NETWORK_INFO";
    String PING_EVENT = "@BST/PING";

    String E_RUNTIME_EXCEPTION = "E_RUNTIME_EXCEPTION";
    String E_INVALID_PARAM = "E_INVALID_PARAM";
    String E_LOGGER_ALREADY_STARTED = "E_LOGGER_ALREADY_STARTED";
    String E_PERMISSION_REQUIRED = "E_PERMISSION_REQUIRED";

    String E_LOGFILE_CREATE = "E_LOGFILE_CREATE";
    String E_LOGFILE_WRITE = "E_LOGFILE_WRITE";
    String E_LOGFILE_CLOSE = "E_LOGFILE_CLOSE";

    /* PING related events */
    String ICMP_PACKET="ICMP_PACKET";
    String STATISTIC="STATISTIC";
    String SUMMARY="SUMMARY";
    String UNKNOWN="UNKNOWN";

    String MODULE_NAME = "BSTNetworkLoggingModule";

}
