package ca.polymtl.inf8405_tp2.domain

data class Device(
    val latitude: Long,
    val longitude: Long,
    val name: String,
    val address: String,
    val deviceClass: String,
    val type: String
)

data class DeviceClass(
    val id: Int,
    val name: String
)

val majorDeviceClasses: List<DeviceClass> = listOf(
    DeviceClass(name = "MISC", id = 0x0000),
    DeviceClass(name = "COMPUTER", id = 0x0100),
    DeviceClass(name = "PHONE", id = 0x0200),
    DeviceClass(name = "NETWORKING", id = 0x0300),
    DeviceClass(name = "AUDIO_VIDEO", id = 0x0400),
    DeviceClass(name = "PERIPHERAL", id = 0x0500),
    DeviceClass(name = "IMAGING", id = 0x0600),
    DeviceClass(name = "WEARABLE", id = 0x0700),
    DeviceClass(name = "TOY", id = 0x0800),
    DeviceClass(name = "HEALTH", id = 0x0900),
    DeviceClass(name = "UNCATEGORIZED", id = 0x1F00)
)