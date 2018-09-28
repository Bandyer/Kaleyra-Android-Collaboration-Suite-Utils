package com.bandyer.android_common.fetcher

/**
 * Utility class to be user to map user alias with a class representation of a user.
 * Load your user synchronously or asynchronously and return result on the OnUserFetcherObserver provided interface
 * as an HashMap<String, BaseUser> of alias mapped with BaseUser class.
 */
abstract class UserInformationFetcher {

    /**
     * Load your user synchronously or asynchronously and return result on the OnUserFetcherObserver provided interface
     * @param userAlias user alias
     * @param onUserInformationFetcherObserver OnUserInformationFetcherObserver uses to return loaded info.
     */
    open fun fetchUser(userAlias: String, onUserInformationFetcherObserver: OnUserInformationFetcherObserver) {}

    /**
     * Load your users' images synchronously or asynchronously and return result on the OnUserFetcherObserver provided interface
     * @param userAlias user alias
     * @param onUserImageFetcherObserver OnUserInformationFetcherObserver uses to return loaded info.
     */
    open fun fetchUserImage(userAlias: String, onUserImageFetcherObserver: OnUserImageFetcherObserver) {}
}