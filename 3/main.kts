import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

val fileName = args.getOrNull(0) ?: error("No file path provided")

if (!Files.exists(Paths.get(fileName))) error("No such file $fileName")

val input = File(args.elementAt(0)).readText()

// --------------------------------
// defs

val digitChars = listOf(1,2,3,4,5,6,7,8,9,0).map { it.toString().first() }

data class Location(val x: Int, val y: Int)

data class PartNum(
		val num: Int,
		val loc: Location,
		val gears: List<Gear>
)

data class Gear(val loc: Location)

// --------------------------------
// code

// returns map of X locations to Numbers in string format
fun findNumberLocations(line: String): Map<IntRange, String> {

	return line.foldIndexed(mutableListOf<Pair<IntRange, String>>()) { index, list, char ->

		when {
			char !in digitChars -> list

			list.isEmpty() || list.last().first.contains(index - 1).not() ->
				list.apply {
					add(index..index to char.toString())
				}

			else ->
				list.apply {
					val oldRecord = list[lastIndex]
					set(lastIndex, oldRecord.first.first..index to oldRecord.second + char)
				}
		}

	}.associate { it.first to it.second }

}

/**
 * @param lineIndex index of the line on which the number is located
 * @param range range of indices the number takes up
 * @return pairs of Int and IntRange, where Int is the index of line that should be cheked
 * and IntRange are the indices on this line that should be checked
 */
fun createSurroundingIndices(
	mapWidth: Int, mapHeight: Int,
	lineIndex: Int, range: IntRange
): List<Pair<Int, IntRange>> {

	val xStart = if (range.first == 0) 0 else range.first - 1
	val xEnd = if (range.last == mapWidth - 1) mapWidth - 1 else range.last + 1

	val lineAbove = lineIndex - 1 to xStart..xEnd
	val thisLine = lineIndex to xStart..xEnd
	val lineBelow = lineIndex + 1 to xStart..xEnd

	return buildList {
		if (lineIndex != 0) add(lineAbove)
		add(thisLine)
		if (lineIndex != mapHeight - 1) add(lineBelow)
	}
}

// check out surrounding indices on the map
fun getCharsOfIndices(
	map: Sequence<String>,
	indices: List<Pair<Int, IntRange>>
): List<Char> {

	return indices
		.map { (lineIndex, range) ->
			val line = map.elementAt(lineIndex)

			range.map { line[it] }
		}
		.flatten()
}

fun getCharsOfIndicesWithLoc(
	map: Sequence<String>,
	indices: List<Pair<Int, IntRange>>
): List<Pair<Char, Location>> {

	return indices
		.map { (lineIndex, range) ->
			val line = map.elementAt(lineIndex)

			range.map {
				line[it] to Location(x = it, y = lineIndex)
			}
		}
		.flatten()
}

fun partOne(input: String): Int {

	val map = input.lineSequence()
	val width = map.elementAt(0).length
	val height = map.count()

	println("w$width h$height")

	val result = map.mapIndexed { i, line ->
		val locs = findNumberLocations(line)

		locs.mapNotNull { (range, engineNum) ->
			val hasBeenSurrounded = createSurroundingIndices(width, height, i, range)
				.let { getCharsOfIndices(map, it) }
				.any { it != '.' && it !in digitChars }

			engineNum.toInt().takeIf { hasBeenSurrounded }
		}
	}.flatten().sum()

	return result
}

fun partTwo(input: String): Int {

	val map = input.lineSequence()
	val width = map.elementAt(0).length
	val height = map.count()

	return map.mapIndexed { i, line ->

		val locs = findNumberLocations(line)

		locs.mapNotNull { (range, engineNum) ->
			val surrounding =  createSurroundingIndices(width, height, i, range)
				.let { getCharsOfIndicesWithLoc(map, it) }
			val hasGear = surrounding.any { it.first == '*' }

			if (!hasGear) return@mapNotNull null

			PartNum(
				engineNum.toInt(),
				loc = Location(x = range.first, y = i),
				gears = surrounding
					.filter { it.first == '*' }
					.map { Gear(it.second) }
			)
		}
	}
		.flatten()
		.flatMap { partNum ->
			partNum.gears.map { it to partNum.num }
		}
		.groupBy { it.first }
		.filterValues {
			it.size == 2
		}
		.entries
		.sumOf { (_, value) ->
			val n1 = value[0].second
			val n2reference = value[1].second
			n1 * n2reference
		}
}

//println("Part one: ${partOne(input)}")
println("Part two: ${partTwo(input)}")