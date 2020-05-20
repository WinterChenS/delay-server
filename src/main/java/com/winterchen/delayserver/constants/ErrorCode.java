package com.winterchen.delayserver.constants;
/**
 * @author Donghua.Chen 2020/5/20
 */
public interface ErrorCode {

    interface Common {

        String SYSTEM_ERROR = "system.error";

        String ILLEGAL_DATA = "illegal.data";

        String DATA_IS_DELETED = "data.is.deleted";

        String DATA_IS_ALREADY_EXIST = "data.is.already.exist";
    }

    interface Delay {
        String REPEATED_REQUESTS = "repeated.requests";
        String REMOTE_BAD_RESPONSE = "remote.bad.response";
        String REMOTE_RESPOSE_FAIL = "remote.response.fail";
    }

}
