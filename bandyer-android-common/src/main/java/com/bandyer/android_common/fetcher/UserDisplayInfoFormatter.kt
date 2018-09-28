package com.bandyer.android_common.fetcher

/**
 * UserDisplayInfoFormatter interface is used to customize how a user is displayed with a UserDisplayInfo object.
 */
interface UserDisplayInfoFormatter  {
    /**
     * Customize how to print and display user info returning desired string concatenation as the following example:
     * return userDisplayInfo.getFirstName() + " " + userDisplayInfo.getLastName();
     * @param userDisplayInfo UserDisplayInfo
     * @return String the formatted user
     */
    fun format(userDisplayInfo: UserDisplayInfo): String
}

/**
 * UserDisplayInfoFormatter default Implementation
 */
class UserDisplayInfoFormatterImpl: UserDisplayInfoFormatter {

    override fun format(userDisplayInfo: UserDisplayInfo): String {

        val firstName = userDisplayInfo.firstName ?: ""
        val lastName = userDisplayInfo.lastName ?: ""
        val nickname = userDisplayInfo.nickName ?: ""
        val email = userDisplayInfo.nickName ?: ""

        return when {
            firstName.isNotEmpty() || lastName.isNotEmpty() -> "$firstName $lastName"
            nickname.isNotEmpty() -> nickname
            email.isNotEmpty() -> email
            else -> ""
        }
    }
}