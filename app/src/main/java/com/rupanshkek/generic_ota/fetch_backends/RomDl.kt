package com.rupanshkek.generic_ota.fetch_backends

import java.time.LocalDate

data class RomDl(val device: String, val download: String, val zipName: String, val buildDate: LocalDate, val maintainer: String, val xdaThread: String)