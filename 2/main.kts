import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths

val fileName = args.getOrNull(0) ?: error("No file path provided")

if (!Files.exists(Paths.get(fileName))) error("No such file $fileName")

val input = File(args.elementAt(0)).readText()

// === === === === === === === === === === === === === === === 

enum class Color {
	Red, Green, Blue;
	
	companion object {
		fun fromRawValue(value: String): Color =
			when {
				"red" in value -> Red
				"green" in value -> Green
				"blue" in value -> Blue
				else -> error("Unknown color value $value")
			}
	}
}

data class Game(
	val id: Int,
	val reveals: List<List<Pair<Int, Color>>>
) {
	
	fun knownMaxOfCubes(color: Color): Int {
	    return reveals.flatten()
			.filter { it.second == color }
			.maxOf { it.first }
	}
	
}

fun convertRawInput(input: String): List<Game> {
	return input.split('\n')
		.map { line ->
			Game(
				id = Regex("[0-9]+").find(line)!!.value.toInt(),
				reveals = line
					.substring(line.indexOf(':') + 2)
					.split(';')
					.map { reveal ->
						println(reveal)
						reveal.split(',')
							.map {
								Regex("[0-9]+").find(it)!!.value.toInt() to Color.fromRawValue(it)
							}
					}
			)
		}
}

fun partOne(input: String): Int {
	val games = convertRawInput(input)
	
	return games
		.filter {
			it.knownMaxOfCubes(Color.Red) <= 12
			&& it.knownMaxOfCubes(Color.Green) <= 13
			&& it.knownMaxOfCubes(Color.Blue) <= 14
		}
		.sumOf { it.id }
}

fun partTwo(input: String): Int {
	val games = convertRawInput(input)

	games.forEach { println(it) }
	
	return games
		.sumOf {
			val red = it.knownMaxOfCubes(Color.Red)
			val green = it.knownMaxOfCubes(Color.Green)
			val blue = it.knownMaxOfCubes(Color.Blue)
			val power =	red * green * blue
			println("${it.id} | red $red green $green blue $blue | power: $power")
			power
		}
}

partTwo(input)