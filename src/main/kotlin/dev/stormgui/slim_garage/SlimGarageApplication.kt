package dev.stormgui.slim_garage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SlimGarageApplication

fun main(args: Array<String>) {
	runApplication<SlimGarageApplication>(*args)
}
