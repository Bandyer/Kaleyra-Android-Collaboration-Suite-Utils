package com.bandyer.android_common.fetcher

import java.io.Serializable

/**
 * This class represents some user display info.
 * A user can be represented with the following info:
 * @property userAlias a not null Bandyer valid user alias.
 * @property nickName nickname
 * @property firstName firstname
 * @property lastName lastname
 * @property email email
 * @constructor
 */
@Suppress("DataClassPrivateConstructor")
data class UserDisplayInfo private constructor(val userAlias: String, val nickName: String? = "", val firstName: String? = "", val lastName: String? = "", val email: String? = null): Serializable {

    /**
     * This class creates a UserDisplayInfo object representing some user display info.
     * A user can be represented with the following info:
     * @property userAlias a not null Bandyer valid user alias.
     * @property nickName nickname
     * @property firstName firstname
     * @property lastName lastname
     * @property email email
     * @constructor
     */
    class Builder(private var userAlias: String) {

        private var nickName: String = ""
        private var firstName: String = ""
        private var lastName: String = ""
        private var email: String = ""

        /**
         * User's nickname.
         * @param nickName
         * @return Builder
         */
        fun withNickName(nickName: String): Builder {
            this.nickName = nickName
            return this
        }

        /**
         * User's first name.
         * @param firstName
         * @return Builder
         */
        fun withFirstName(firstName: String): Builder {
            this.firstName = firstName
            return this
        }

        /**
         * User's last name.
         * @param lastName
         * @return Builder
         */
        fun withLastName(lastName: String): Builder {
            this.lastName = lastName
            return this
        }

        /**
         * User's email.
         * @param email
         * @return Builder
         */
        fun withEmail(email: String): Builder {
            this.email = email
            return this
        }

        /**
         * Create
         * @return UserDisplayInfo
         */
        fun build(): UserDisplayInfo {
            return UserDisplayInfo(userAlias, nickName, firstName, lastName, email)
        }
    }
}