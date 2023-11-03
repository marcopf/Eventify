let ip: string = "<IP>:<PORT>";
export abstract class API{
    static readonly GET_TAGS=`${ip}/api/v1/category/all`;
    static readonly GET_SINGLE_EVENT=`${ip}/api/v1/event`;
    static readonly GET_EVENT_IMAGE=`${ip}/api/v1/event/image`;
    static readonly GET_REGISTERED_EVENTS=`${ip}/api/v1/event/registered`;
    static readonly GET_INITIAL_EVENTS=`${ip}/api/v1/event/all`;
    static readonly GET_MORE_EVENTS=`${ip}/api/addEvents`;
    static readonly GET_SEARCH_EVENT=`${ip}/api/v1/event/all`;
    static readonly GET_USER_INFO=`${ip}/api/v1/user`;
    static readonly GET_OWN_CREATED_EVENTS=`${ip}/api/v1/event/owned`;
    static readonly GET_NOTIFICATION=`${ip}/api/v1/user/notification`;
    static readonly GET_IMMINENT_EVENT=`${ip}/api/v1/event/imminent`;
    static readonly GET_USER_LIST_ADMIN=`${ip}/api/v1/user/all`;
    static readonly GET_USER_IMAGE=`${ip}/api/v1/user/image`;

    static readonly POST_NEW_EVENT_INFO=`${ip}/api/v1/event/add`;
    static readonly POST_NEW_EVENT_IMAGES=`${ip}/api/v1/event/upload`;
    static readonly POST_MODIFIED_EVENT_INFO=`${ip}/api/v1/event/edit`;
    static readonly POST_MODIFIED_EVENT_IMAGES=`${ip}/api/events/upload`;
    static readonly POST_LOGOUT=`${ip}/api/v1/credential/logout`;
    static readonly POST_NEW_USER_PICTURE=`${ip}/api/v1/user/upload`;
    static readonly POST_LOGIN=`${ip}/api/v1/credential/login`;
    static readonly POST_SIGNUP_INFO=`${ip}/api/v1/credential/signUp`;
    static readonly POST_SIGNUP_IMAGE=`${ip}/api/v1/credential/upload`;
    static readonly POST_ADD_TAGS=`${ip}/api/v1/category/addList`;

    static readonly PUT_NEW_USER_INFO=`${ip}/api/v1/user/update`;
    static readonly PUT_NEW_USER_PASSWORD=`${ip}/api/v1/user/update`;
    static readonly PUT_REGISTER_TO_EVENT=`${ip}/api/v1/user/register`;
    static readonly PUT_UNREGISTER_TO_EVENT=`${ip}/api/v1/user/unregister`;
    static readonly PUT_BLOCK_USER=`${ip}/api/v1/user/block`;
    static readonly PUT_UNBLOCK_USER=`${ip}/api/v1/user/unblock`;
    static readonly PUT_MODIFY_TAG=`${ip}/api/v1/category/update`;

    static readonly DELETE_EVENT = `${ip}/api/v1/event/delete`;
    static readonly DELETE_USER = `${ip}/api/v1/user/delete`;
    static readonly DELETE_TAG=`${ip}/api/v1/category/delete`;

    static readonly REFRESH_TOKEN=`${ip}/api/v1/credential/refresh`;
} 
