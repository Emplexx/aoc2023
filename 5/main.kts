import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.suspendCoroutine
import kotlin.math.pow

val fileName = args.getOrNull(0) ?: error("No file path provided")

if (!Files.exists(Paths.get(fileName))) error("No such file $fileName")

val input = File(fileName).readText()

// --------------------------------

data class MapEntry(

        val len: Int,
)

data class FrRange(
        val from: Long,
        val to: Long,
) {
    operator fun contains(long: Long): Boolean {
        return from <= long && to >= long
    }

    fun indexOf(long: Long): Long {
        return (long - from)
    }

    fun elementAt(long: Long): Long {
        return from + long
    }
}

fun Map<FrRange, FrRange>.getOrSame(single: Long): Long {

    return this.entries
            .find { single in it.key }
            ?.let { entry ->
                val index = entry.key.indexOf(single)
                entry.value.elementAt(index)
            }
            ?: single
}

fun inputToMap(input: String, name: String): Map<FrRange, FrRange> {

    val entries = input
            .substring(input.indexOf("$name map:"))
            .let {
                it.substring(it.indexOf('\n'),
                        it.indexOf("\n\n").takeIf { it != -1 } ?: it.length)
            }
            .lines()
            .filter { it.isNotBlank() }

    return buildMap {
        for (e in entries) {
            val (value, key, rangeLen) = e.split(' ').map { it.toLong() }

            this[FrRange(key, key + rangeLen - 1)] = FrRange(value, value + rangeLen - 1)
        }
    }
}

fun partOne(input: String): Long {

    val seeds = input.lines()[0].split(' ').mapNotNull { it.toLongOrNull() }
    val seedToSoil = inputToMap(input, "seed-to-soil")
    val soilToFertilizer = inputToMap(input, "soil-to-fertilizer")
    val fertilizerToWater = inputToMap(input, "fertilizer-to-water")
    val waterToLight = inputToMap(input, "water-to-light")
    val lightToTemperature = inputToMap(input, "light-to-temperature")
    val temperatureToHumidity = inputToMap(input, "temperature-to-humidity")
    val humidityToLocation = inputToMap(input, "humidity-to-location")

    return seeds.minOf { seed ->
        seedToSoil.getOrSame(seed)
                .let { soilToFertilizer.getOrSame(it) }
                .let { fertilizerToWater.getOrSame(it) }
                .let { waterToLight.getOrSame(it) }
                .let { lightToTemperature.getOrSame(it) }
                .let { temperatureToHumidity.getOrSame(it) }
                .let { humidityToLocation.getOrSame(it) }

    }.toLong()
}

//fun partTwo(input: String): Long {
//
//    val seedToSoil = inputToMap(input, "seed-to-soil")
//    val soilToFertilizer = inputToMap(input, "soil-to-fertilizer")
//    val fertilizerToWater = inputToMap(input, "fertilizer-to-water")
//    val waterToLight = inputToMap(input, "water-to-light")
//    val lightToTemperature = inputToMap(input, "light-to-temperature")
//    val temperatureToHumidity = inputToMap(input, "temperature-to-humidity")
//    val humidityToLocation = inputToMap(input, "humidity-to-location")
//
//
//    val seeds = input.lines()[0].split(' ').mapNotNull { it.toLongOrNull() }
//   val seq =  seeds.zipWithNext().map { (from, to) ->
//        generateSequence(from) { if (it == from + to) null else it + 1 }
//        
//    }
//    
//    val locations = mutableListOf<Long>()
//    var current: Sequence<Long>? = null
//    seq.forEach { sequence ->
//        current = sequence
//        current?.forEach { seed ->
//            seedToSoil.getOrSame(seed)
//                .let { soilToFertilizer.getOrSame(it) }
//                .let { fertilizerToWater.getOrSame(it) }
//                .let { waterToLight.getOrSame(it) }
//                .let { lightToTemperature.getOrSame(it) }
//                .let { temperatureToHumidity.getOrSame(it) }
//                .let { humidityToLocation.getOrSame(it) }
//                .let { locations.add(it) }
//        }
//        current = null
//    }
//    
//    return locations.min()
//    
//
//}

println("Part one: ${partOne(input)}")
//println("Part two: ${partTwo(input)}")


// Implementation with Kotlins LongRange which was unbelievably slow
//fun Map<LongRange, LongRange>.getOrSame(single: Long): Long {
//	
//	println("getOrSame $single")
//	
//	return this.entries
//		.indexOfFirst {
//			println("indexOfFirst $single")
//			it.key.first <= single && it.key.last >= single
//		}
//		.takeIf { it != -1 }
//		?.let { i ->
//			val elem = this.entries.elementAt(i)
//			val index = elem.key.indexOf(single)
//			elem.value.elementAt(index)
//		} ?: single
//}

//fun inputToMap(input: String, name: String): Map<LongRange, LongRange> {
//	val entries = input
//		.substring(input.indexOf("$name map:"))
//		.let {
//			it.substring(it.indexOf('\n'),
//						 it.indexOf("\n\n").takeIf { it != -1 } ?: it.length)
//		}
//		.lines()
//		.filter { it.isNotBlank() }
//	
//	println("inputToMap $entries")
//	
//	val map = mutableMapOf<LongRange, LongRange>()
//	entries.forEach { e ->
//		val (value, key, rangeLen) = e.split(' ').map { it.toLong() }
////		(0 until rangeLen.toLong()).forEach {
////			map[key.plusNum(it)] = value.plusNum(it)
////		}
//		
//		map[key until key+rangeLen] = value until value + rangeLen
//	}
//	
//	return map
//}