package com.bandyer.android_common.fetcher

/**
 * UserDisplayInfoFormatter interface is used to customize how a user is displayed with a UserDisplayInfo object.
 */
interface UserDisplayInfoFormatter  {
    /**
     * Customize how to print and display user info returning desired string concatenation as the following example:
     * return userDisplayInfo.getFirstName() + " " + userDisplayInfo.getLastName();
     * @param userDisplayInfo UserDisplayInfo
     * @return String
     */
    fun format(userDisplayInfo: UserDisplayInfo): String
}

class UserDisplayInfoFormatterImpl: UserDisplayInfoFormatter {

    override fun format(userDisplayInfo: UserDisplayInfo): String {

        val firstName = userDisplayInfo.firstName ?: ""
        val lastName = userDisplayInfo.lastName ?: ""
        val nickname = userDisplayInfo.nickName ?: ""
        val email = userDisplayInfo.nickName ?: ""

        return if(firstName.isNotEmpty() || lastName.isNotEmpty()) {
            "$firstName $lastName"
        }
        else if(nickname.isNotEmpty()) {
            nickname
        }
        else if(email.isNotEmpty()) {
            email
        }
        else ""
    }
}