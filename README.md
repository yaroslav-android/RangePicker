# RangePicker
Custom Range Picker view with modern design.

In **Table 1** you could see list of avalible attributes.

| Attribute name       | Format           | Example  | Description |
| :--------------------- |:----------------:| :--------| :--------|
| `backgroundSelectedTint`| _color_ | `app:backgroundSelectedTint="@color/colorPrimary"` | Change background color of selected item |
| `backgroundStripTint`      | _color_ |  `app:backgroundStripTint="@color/colorPrimary"`  | Change background color of strip between 2 selected items |
| `textColorOnSelected`      | _color_ |  `app:textColorOnSelected="@color/colorPrimary"`  | Change color of text on `backgroundSelectedTint` |
| `textColorOnSurface`      | _color_ |  `app:textColorOnSurface="@color/colorPrimary"`  | Change color of text on other surface |
| `cornerRadius`      | _dimension_ |  `app:cornerRadius="@dimen/some_value"`  | Change corner radius for selected background |
| `stripThickness`      | _dimension_ |  `app:stripThickness="@dimen/some_value"`  | Change thickness of strip between 2 selected items |
| `extraPadding`      | _dimension_ |  `app:extraPadding="@dimen/some_value"`  | Change padding of inside selected items |
| `android:textSize`      | _dimension_ |  `android:textSize="@dimen/some_value"`  | Change all texts size |
| `android:fontFamily`      | _font_ |  `android:fontFamily="@font/sone_font"`  | Change all texts font |

<p align="center"> <b>Table 1</b> - list of avalible attributes </p>

**Planned:**
- [ ] Long click support (dragging selected items)
- [ ] Fix calculations for drawings
