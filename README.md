<p align="center">
<img src="img/bandyer.jpg" alt="Bandyer" title="Bandyer" />
</p>



[![Download](https://badgen.net/maven/v/metadata-url/https/maven.bandyer.com/releases/com/bandyer/bandyer-android-common/maven-metadata.xml?label=maven.bandyer.com/releases) ](https://maven.bandyer.com/index.html#releases/com/bandyer/bandyer-android-common/)[![Android CI](https://github.com/Bandyer/Bandyer-Android-Common/actions/workflows/android.yml/badge.svg?branch=master)](https://github.com/Bandyer/Bandyer-Android-Common/actions/workflows/android.yml)[![Docs](https://img.shields.io/badge/docs-current-brightgreen.svg)](https://bandyer.github.io/Bandyer-Android-Common/kDoc)
[![Twitter](https://img.shields.io/twitter/url/http/shields.io.svg?style=social&logo=twitter)](https://twitter.com/intent/follow?screen_name=bandyersrl)


Bandyer is a young innovative startup that enables audio/video communication and collaboration from any platform and browser! Through its WebRTC architecture, it makes video communication simple and punctual. 

---


**[Installation](#installation)** .
**[Documentation](#documentation)** .
**[Support](#support)** .
**[Credits](#credits)** .

---

## Installation

Download the [latest JAR](https://bintray.com/bandyer/Communication/Android-Common) or grab via Gradle:

```groovy
implementation ("com.bandyer:bandyer-android-common:1.0.10") {
        exclude group: "android.arch.core"
        exclude group: "android.arch.lifecycle"
}
```

## Documentation

You can find the complete documentation in two different styles

Kotlin Doc: [https://bandyer.github.io/Bandyer-Android-Common/kDoc/](https://bandyer.github.io/Bandyer-Android-Common/kDoc/bandyer-android-common/)

## Support
To get basic support please submit an [Issue](https://github.com/Bandyer/Bandyer-Android-Common/issues) 

If you prefer commercial support, please contact [bandyer.com](https://bandyer.com) by mail: <mailto:info@bandyer.com>.


## Credits
- [Android-weak-handler](https://github.com/badoo/android-weak-handler) by Badoo
