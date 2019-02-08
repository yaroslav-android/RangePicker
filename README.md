# RangePicker
Custom Range Picker view with modern design.

**Attributes:**</br>
In **Table 1** you could see list of available attributes.

| Attribute name          | Description                                               | Example                                           | Format      |
| :---------------------- |:--------------------------------------------------------- | :------------------------------------------------ | :---------: |
| `backgroundSelectedTint`| Change background color of selected item                  | `app:backgroundSelectedTint="@color/colorPrimary"`| _color_     | 
| `backgroundStripTint`   | Change background color of strip between 2 selected items | `app:backgroundStripTint="@color/colorPrimary"`   | _color_     |
| `textColorOnSelected`   | Change color of text on `backgroundSelectedTint`          |  `app:textColorOnSelected="@color/colorPrimary"`  | _color_     |
| `textColorOnSurface`    | Change color of text on other surface                     |  `app:textColorOnSurface="@color/colorPrimary"`   | _color_     |
| `cornerRadius`          | Change corner radius for selected background              |  `app:cornerRadius="@dimen/some_value"`           | _dimension_ |
| `stripThickness`        | Change thickness of strip between 2 selected items        |  `app:stripThickness="@dimen/some_value"`         | _dimension_ |
| `extraPadding`          | Change padding of inside selected items                   |  `app:extraPadding="@dimen/some_value"`           | _dimension_ |
| `android:textSize`      | Change all texts size                                     |  `android:textSize="@dimen/some_value"`           | _dimension_ |
| `android:fontFamily`    | Change all texts font                                     |  `android:fontFamily="@font/sone_font"`           | _font_      |

<p align="center"> <b>Table 1</b> - <i>list of available attributes</i> </p>

**Planned:**
- [ ] Long click support (dragging selected items)
- [ ] Fix calculations for drawings
