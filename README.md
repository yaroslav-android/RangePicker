# RangePicker
Custom Range Picker view with modern design.</br>![version 1.0.7-beta03](https://img.shields.io/badge/version-1.0.7--beta03-green.svg)</br>

<p align="center">
	<kbd>
 		<img src="https://github.com/yaroslav-android/RangePicker/blob/master/assets/ezgif.com-video-to-gif.gif" alt="preview"/>
	</kbd>
</p>

**Attributes:**</br>
In **Table 1** you could see list of available attributes.

| Attribute name          | Description                                               | Example                                           |
| :---------------------- |:--------------------------------------------------------- | :------------------------------------------------ |
| `backgroundSelectedTint`| Change background color of selected item                  | `app:backgroundSelectedTint="@color/colorPrimary"`|
| `backgroundStripTint`   | Change background color of strip between 2 selected items | `app:backgroundStripTint="@color/colorPrimary"`   | 
| `textColorOnSelected`   | Change color of text on `backgroundSelectedTint`          |  `app:textColorOnSelected="@color/colorPrimary"`  | 
| `textColorOnSurface`    | Change color of text on other surface                     |  `app:textColorOnSurface="@color/colorPrimary"`   | 
| `cornerRadius`          | Change corner radius for selected background              |  `app:cornerRadius="@dimen/some_value"`           | 
| `stripThickness`        | Change thickness of strip between 2 selected items        |  `app:stripThickness="@dimen/some_value"`         | 
| `extraPadding`          | Change padding of inside selected items                   |  `app:extraPadding="@dimen/some_value"`           | 
| `android:textSize`      | Change all texts size                                     |  `android:textSize="@dimen/some_value"`           | 
| `android:fontFamily`    | Change all texts font                                     |  `android:fontFamily="@font/sone_font"`           | 

<p align="center"> <b>Table 1</b> - <i>list of available attributes</i></p>

**Planned:**
- [x] Long click support 
    - [ ] Dragging selected items
    - [ ] Dragging from not selected item
    - [x] Dismiss drag
- [ ] Fix calculations for drawings
- [ ] Add possibility to change view properties programatically
- [ ] Allow set default selected items by position
- [ ] Add change listener


**Dependencies:**</br>
Maven:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.yaroslav-android</groupId>
    <artifactId>RangePicker</artifactId>
    <version>version</version>
</dependency>
```

or Gradle:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

```
```groovy
dependencies {
    implementation 'com.github.yaroslav-android:RangePicker:version'
}
``` 
