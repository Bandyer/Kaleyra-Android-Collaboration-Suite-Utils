package com.bandyer.android_common.fetcher

import android.graphics.Bitmap
import com.bandyer.android_common.BaseTest
import io.mockk.mockk
import org.junit.Test


class Fetchertests: BaseTest() {


    @Test()
    fun testInfoBuilder() {

        val userDisplayInfo = UserDisplayInfo.Builder("alias")
                .withEmail("mail")
                .withFirstName("firstname")
                .withLastName("lastname")
                .withNickName("nickname")
                .build()

        assert(userDisplayInfo.userAlias    == "alias")
        assert(userDisplayInfo.firstName    == "firstname")
        assert(userDisplayInfo.lastName     == "lastname")
        assert(userDisplayInfo.email        == "mail")
        assert(userDisplayInfo.nickName     == "nickname")
    }

    @Test()
    fun testImageBitmapVisualDisplayBuilder() {

        val bitmap = mockk<Bitmap>(relaxed = true)

        val visualInfo = UserImageDisplayInfo.Builder("alias")
                .withImageBitmap(bitmap)

        assert(visualInfo.alias == "alias")
        assert(visualInfo is UserImageBitmapDisplayInfo)
    }

    @Test()
    fun testImageUrlVisualDisplayBuilder() {

        val visualInfo = UserImageDisplayInfo.Builder("alias")
                .withImageUrl("url")

        assert(visualInfo.alias == "alias")
        assert(visualInfo is UserImageUrlDisplayInfo)
    }

    @Test()
    fun testImageResIDUrlVisualDisplayBuilder() {

        val visualInfo = UserImageDisplayInfo.Builder("alias")
                .withResId(123)

        assert(visualInfo.alias == "alias")
        assert(visualInfo is UserImageResIdDisplayInfo)
    }

    @Test()
    fun testUserInfoFormatter() {

        val userDisplayInfo = UserDisplayInfo.Builder("alias")
                .withEmail("mail")
                .withFirstName("firstname")
                .withLastName("lastname")
                .withNickName("nickname")
                .build()

        val formatter = object: UserDisplayInfoFormatter {
            override fun format(userDisplayInfo: UserDisplayInfo): String {
                return userDisplayInfo.userAlias +
                        userDisplayInfo.firstName +
                        userDisplayInfo.lastName +
                        userDisplayInfo.nickName +
                        userDisplayInfo.email
            }
        }

        assert(formatter.format(userDisplayInfo) == userDisplayInfo.userAlias +
                userDisplayInfo.firstName +
                userDisplayInfo.lastName +
                userDisplayInfo.nickName +
                userDisplayInfo.email)
    }
}