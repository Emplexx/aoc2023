import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

val fileName = args.getOrNull(0) ?: error("No file path provided")

if (!Files.exists(Paths.get(fileName))) error("No such file $fileName")

val input = File(fileName).readText()

// --------------------------------

data class Input(
	val seeds: List<Pair<Long, Long>>,
	val maps: List<List<HellishEntry>>
)

data class HellishEntry(
		val from: Long, val to: Long, val shift: Long,
) {

	operator fun contains(long: Long): Boolean {
		return from <= long && to >= long
	}

	operator fun contains(bounds: Pair<Long, Long>): Boolean {
		return from <= bounds.first && bounds.second <= to
	}

}

operator fun Pair<Long, Long>.contains(long: Long): Boolean {
	return first <= long && second >= long
}

fun Pair<Long, Long>.shiftBy(shift: Long) = this.copy(first + shift, second + shift)

fun List<HellishEntry>.findBounds(bounds: Pair<Long, Long>): List<Pair<Long, Long>> {
	val (bStart, bEnd) = bounds

	// the range we're looking for is entirely covered by a map entry
	find { bounds in it }
			?.let { return listOf(bounds.shiftBy(it.shift)) }

	// the range we're looking for is covered partly, or is larger, so we'll have to split it
	val ranges = filter {
		bStart < it.from && it.to < bEnd // range is larger than a single map entry
				|| bStart in it
				|| bEnd in it
	}
			.also { if (it.isEmpty()) return listOf(bounds) } // the range is actually not covered at all
			.also { println("found ranges $it") }
	
	return ranges
		.sortedBy { it.from }
		.flatMap { listOf(it.from, it.to) }
		.toMutableList()
		.also {
			if (it.first() < bStart) it[0] = bStart
			if (it[0] > bStart) it.addAll(0, listOf(bStart, it[0] - 1))

			if (it.last() > bEnd) it[it.lastIndex] = bEnd
			if (it.last() < bEnd) it.addAll(listOf(it.last() + 1, bEnd))
		}
		.chunked(2)
		.map { it[0] to it[1] }
		.also { println(it) }
		.flatMap { this.findBounds(it) }
}

fun parseInput(input: String): Input {
	val seeds = input.lines()[0].split(' ').mapNotNull { it.toLongOrNull() }
		.chunked(2)
		.map { it[0] to it[0] + it[1] - 1 }
	
	val maps = buildList {
		add(inputToMap(input, "seed-to-soil"))
		add(inputToMap(input, "soil-to-fertilizer"))
		add(inputToMap(input, "fertilizer-to-water"))
		add(inputToMap(input, "water-to-light"))
		add(inputToMap(input, "light-to-temperature"))
		add(inputToMap(input, "temperature-to-humidity"))
		add(inputToMap(input, "humidity-to-location"))
	}
	return Input(seeds, maps)
}

fun inputToMap(input: String, name: String): List<HellishEntry> {

	val entries = input
			.substring(input.indexOf("$name map:"))
			.let { str ->
				str.substring(
					str.indexOf('\n'),
					str.indexOf("\n\n").takeIf { it != -1 } ?: str.length)
			}
			.lines()
			.filter { it.isNotBlank() }

	return buildList {
		for (e in entries) {
			val (value, key, rangeLen) = e.split(' ').map { it.toLong() }
			add(HellishEntry(key, key + rangeLen - 1, value - key))
		}
	}
}

fun partTwo(input: String): Long {
	val parsed = parseInput(input)
	
	val result = parsed.seeds
		.flatMap { seed ->
			var bounds = listOf<Pair<Long, Long>>(seed)
			for (map in parsed.maps) {
				bounds = bounds.flatMap { map.findBounds(it) }
			}
			bounds
		}
		.minOf { it.first }
	
	return result
}

println("Part two: ${partTwo(input)}")