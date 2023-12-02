import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

val fileName = args.getOrNull(0) ?: error("No file path provided")

if (!Files.exists(Paths.get(fileName))) error("No such file $fileName")

val input = File(args.elementAt(0)).readText()

//

val validDigits = arrayOf(
    "1" to "one",
    "2" to "two",
    "3" to "three",
    "4" to "four",
    "5" to "five",
    "6" to "six",
    "7" to "seven",
    "8" to "eight",
    "9" to "nine",
)

fun partOne(input: String): Int {
    return input
			.split('\n')
			.filter { it.isNotBlank() }
			.map { line ->
				line.mapNotNull { char -> char.toString().toIntOrNull() }
			}
			.filter { it.isNotEmpty() }
            .sumOf { digits ->
				"${digits[0]}${digits.last()}".toInt()
			}
}

fun partTwo(input: String): Int {
    return input
			.split('\n')
			.filter { it.isNotBlank() }
			.map { line ->

				val map = mutableMapOf<Int, String>()

				validDigits.forEach { (digit, word) ->
					map[line.indexOf(digit)] = digit
					map[line.indexOf(word)] = digit

					map[line.lastIndexOf(digit)] = digit
					map[line.lastIndexOf(word)] = digit
				}
				map.remove(-1)

				map.entries
						.sortedBy { it.key }
						.map { it.value.toInt() }
			}
			.filter { it.isNotEmpty() }
            .sumOf { digits ->
				"${digits[0]}${digits.last()}".toInt()
			}
}

partTwo(input)