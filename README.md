# 无任何依赖的权限申请框架（纯kotlin编写）

![](picture/1.jpg)

```kotlin
PermissionXUtils.requestPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            showLog = true,
            onPermissionGranded = {
                PermissionXUtils.logD(" 权限申请通过 ")
            },
            onPermissionDined = { dinedList, noShowRationableList ->
                dinedList.forEach {
                    PermissionXUtils.logD(" 权限被拒绝 = $it ")
                }
                noShowRationableList.forEach {
                    PermissionXUtils.logD(" 权限被拒绝且不显示弹窗 = $it ")
                }
            })
```
