package org.marcfischer.modbusmonitor

/**
 * This class holds a volatile storage of available modbus controllers (queried via 0x2b)
 */

data class Controllers (
    val title: String,
    val serial: String,
    val software: String,
    val model: String,
    val isConnectable: Boolean = false,
)