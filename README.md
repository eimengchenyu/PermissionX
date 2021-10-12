# 很简单易用的权限请求框架

### 引用方法
根目录下build.gradle：allprojects下
```gradle
maven { url 'https://jitpack.io' }
```
module下的build.gradle：
```gradle
implementation 'com.github.eimengchenyu:PermissionX:1.0.1'
```

![](picture/1.jpg)

### 使用方法：
```kotlin
PermissionXUtils.requestPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            showLog = true,
            onPermissionGranded = {
                // 权限全部申请通过
                PermissionXUtils.logD(" 权限申请通过 ")
            },
            onPermissionDined = { dinedList, noShowRationableList ->
                // dinedList：被拒绝的权限集合。noShowRationableList：被拒绝且不再显示弹窗的集合
                dinedList.forEach {
                    PermissionXUtils.logD(" 权限被拒绝 = $it ")
                }
                noShowRationableList.forEach {
                    PermissionXUtils.logD(" 权限被拒绝且不显示弹窗 = $it ")
                }
            })
```

### Android8.0 ANSWER_PHONE_CALLS、READ_PHONE_NUMBERS 已做兼容处理

### Android10.0 ACCESS_BACKGROUND_LOCATION、ACCESS_MEDIA_LOCATION、ACTIVITY_RECOGNITION 已做兼容处理
Android10.0 ACCESS_BACKGROUND_LOCATION 申请后台定位权限，需要优先申请 ACCESS_FINE_LOCATION 或 ACCESS_COARSE_LOCATION，已做兼容处理

### Android11.0 存储权限 MANAGE_EXTERNAL_STORAGE
需要在AndroidManifest.xml中添加：
```xml
<application
        android:requestLegacyExternalStorage="true">
</application>
```
优先使用Manifest.permission.MANAGE_EXTERNAL_STORAGE进行存储权限的申请，
当然也可以用Manifest.permission.WRITE_EXTERNAL_STORAGE和Manifest.permission.READ_EXTERNAL_STORAGE，内部已做兼容处理。

### Android12.0 蓝牙权限 BLUETOOTH_SCAN、BLUETOOTH_ADVERTISE、BLUETOOTH_CONNECT
Android12以下动态申请以上三个蓝牙权限，直接通过，内部已做兼容处理。
Android12及以上若申请Android12以下的BLUETOOTH和BLUETOOTH_ADMIN，会直接抛Exception，由于这两个不是危险权限，不需要动态申请，应该没人会这样做的吧。
