package com.om.sitaramfrem.api;

public interface StandardStatusCodes {
    int SUCCESS = 200;
    int IN_PROGRESS = 201;
    int No_More_Records = 204;
    int BAD_REQUEST = 400;
    int POLICY_NOT_FULL_FILLED = 420;
    int INTERNAL_SERVER_ERROR = 500;
    int NO_DATA_FOUND = 404;
    int CONFLICT = 409;
    int UNAUTHORISE = 401;
    int NOTACCEPTABLE = 406;
    int DUPLICATE_ERROR = 208;
}
