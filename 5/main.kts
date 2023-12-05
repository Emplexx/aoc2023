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

    val lastIndex get() = to - from
}

data class MultiRange(
        val ranges: List<FrRange>,
        val rawStart: Long?,
        val rawEnd: Long?
) {

}

fun Pair<FrRange, FrRange>.lowcut(at: Long): Pair<FrRange, FrRange> {

    val indexOfAt = this.first.indexOf(at)

    return this.copy(
            first.copy(at, first.to),
            second.copy(second.from + indexOfAt, second.to)
    )

}

fun Pair<FrRange, FrRange>.highcut(at: Long): Pair<FrRange, FrRange> {

    val indexOfAt = this.first.indexOf(at)
    val remove = this.first.lastIndex - indexOfAt

    return this.copy(
            first.copy(first.from, first.to - remove),
            second.copy(second.from, second.to - remove)
    )

}

fun Map<FrRange, FrRange>.getMulti(range: MultiRange): MultiRange {
    
}

fun Map<FrRange, FrRange>.getMulti(range: FrRange): MultiRange {

    val start = this.entries
            .indexOfFirst { range.from in it.key }

    val end = this.entries
            .indexOfFirst { range.to in it.key }

    val l = buildList<FrRange> {

        if (start != -1 && start == end) {
            val newStart = this@getMulti.entries.elementAt(start).toPair()
                .lowcut(range.from).highcut(range.to)
            add(newStart.second)
            return@buildList
        }

        if (start != -1) {
            val newStart = this@getMulti.entries.elementAt(start).toPair().lowcut(range.from)
            add(newStart.second)
        }
        if (end != -1) {
            val newEnd = this@getMulti.entries.elementAt(end).toPair().highcut(range.to)
            add(newEnd.second)
        }
    }

    return MultiRange(
            ranges = l,
            rawStart = range.from,
            rawEnd = range.to,
    )

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

fun Map<FrRange, FrRange>.sortMap(): Map<FrRange, FrRange> =
        this.entries.sortedBy { it.key.from }.associate { it.key to it.value }

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

fun partTwo(input: String): Long {

    val seedToSoil = inputToMap(input, "seed-to-soil").sortMap()
    val soilToFertilizer = inputToMap(input, "soil-to-fertilizer").sortMap()
    val fertilizerToWater = inputToMap(input, "fertilizer-to-water").sortMap()
    val waterToLight = inputToMap(input, "water-to-light").sortMap()
    val lightToTemperature = inputToMap(input, "light-to-temperature").sortMap()
    val temperatureToHumidity = inputToMap(input, "temperature-to-humidity").sortMap()
    val humidityToLocation = inputToMap(input, "humidity-to-location").sortMap()


    val seeds = input.lines()[0].split(' ').mapNotNull { it.toLongOrNull() }
    val seedRanges = seeds.chunked(2)
        .map { it[0] to it[1] }
        .map { FrRange(it.first, it.first + it.second - 1) }

    println("soil")
    val try1 = seedRanges.map { range ->
        println("seed range $range")
        seedToSoil.getMulti(range).also {
            println("multirange $it")
        }
    }

    println("fert")
    val try2 = try1.map { mrange ->
        println("seed range $range")
        soilToFertilizer.
    }


//    val seq =  seeds.zipWithNext().map { (from, to) ->
//        generateSequence(from) { if (it == from + to) null else it + 1 }
//    }

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


    return 0
}

//println("Part one: ${partOne(input)}")
println("Part two: ${partTwo(input)}")


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