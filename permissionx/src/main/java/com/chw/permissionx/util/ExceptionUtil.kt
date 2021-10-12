package com.chw.permissionx.util

/**
 * @author hanwei.chen
 * 2020/12/2 11:37
 */
internal class RequestPermissionEmptyException : RuntimeException("Request permission is empty!")

internal class AndroidManifestPermissionEmptyException :
    RuntimeException("AndroidManifest.xml permission is empty!")

internal class AndroidManifestPermissionException(permission: String) :
    RuntimeException("Request $permission is not in AndroidManifest.xml!")

internal class Android12BluetoothPermissionException :
    RuntimeException("current system is more than android12 , you need use android.permission.BLUETOOTH_CONNECT or android.permission.BLUETOOTH_SCAN or android.permission.BLUETOOTH_ADVERTISE!")

internal class Android11StoragePermissionException :
    RuntimeException("current system is more than android11 , you need use android.permission.MANAGE_EXTERNAL_STORAGE , but it is not in AndroidManifest.xml!")

internal class NoAndroid11StoragePermissionException :
    RuntimeException("android.permission.MANAGE_EXTERNAL_STORAGE is android11's api , then request android.permission.WRITE_EXTERNAL_STORAGE or android.permission.READ_EXTERNAL_STORAGE is not in AndroidManifest.xml!")

internal class Android10AccessBackgroundLocationPermissionException :
    RuntimeException("android.permission.ACCESS_BACKGROUND_LOCATION is android10.0's api , you need request android.permission.ACCESS_FINE_LOCATION or android.permission.ACCESS_COARSE_LOCATION first , but it is not in AndroidManifest.xml!")

internal class NoAndroid10AccessBackgroundLocationPermissionException :
    RuntimeException("android.permission.ACCESS_BACKGROUND_LOCATION is android10.0's api , then request android.permission.ACCESS_FINE_LOCATION or android.permission.ACCESS_COARSE_LOCATION is not in AndroidManifest.xml!")

internal class NoAndroid10ActivityRecognitionPermissionException :
    RuntimeException("android.permission.ACTIVITY_RECOGNITION is android10.0's api , then request android.permission.BODY_SENSORS is not in AndroidManifest.xml!")

internal class NoAndroid8AnswerPhoneCallsPermissionException :
    RuntimeException("android.permission.ANSWER_PHONE_CALLS is android8.0's api , then request android.permission.PROCESS_OUTGOING_CALLS is not in AndroidManifest.xml!")

internal class NoAndroid8ReadPhoneNumbersPermissionException :
    RuntimeException("android.permission.READ_PHONE_NUMBERS is android8.0's api , then request android.permission.READ_PHONE_STATE is not in AndroidManifest.xml!")

internal class Android8ReadPhoneStatePermissionException :
    RuntimeException("current system is more than android8.0 , you need use android.permission.READ_PHONE_NUMBERS , but it is not in AndroidManifest.xml!")

internal class Android8ProcessOutgoingCallsPermissionException :
    RuntimeException("current system is more than android8.0 , you need use android.permission.ANSWER_PHONE_CALLS , but it is not in AndroidManifest.xml!")
