package com.bandyer.android_common.fetcher

/**
 * Interface that must be called on user mapped from user aliases.
 */
interface OnUserInformationFetcherObserver {

    /**
     * To be called with valid mapped users based on user alias.
     * @param user UserDisplayInfo
     */
    fun onUserFetched(user: UserDisplayInfo)
}

/**
 * Interface that must be called on users mapped from users aliases.
 */
interface OnUserImageFetcherObserver {

    /**
     * To be called with validUserImageDisplayInfo based on user alias.
     * @param userImagesDisplayInfo user image display information.
     */
    fun onUserImagesFetched(userImagesDisplayInfo: UserImageDisplayInfo)
}