package http_utilities;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HttpCodeTypes {
    public boolean isInfoCode(int code) {
        return code > 99 && code < 104;
    }

    public boolean isSuccessCode(int code) {
        return code > 199 && code < 226;
    }

    public boolean isRedirectionCode(int code) {
        return code > 299 && code < 309;
    }

    public boolean isClientErrorCode(int code) {
        return code > 399 && code < 500;
    }

    public boolean isServerErrorCode(int code) {
        return code > 500 && code < 527;
    }
}
