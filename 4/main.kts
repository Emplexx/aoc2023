import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.pow

val fileName = args.getOrNull(0) ?: error("No file path provided")

if (!Files.exists(Paths.get(fileName))) error("No such file $fileName")

val input = File(args.elementAt(0)).readText()

// --------------------------------

data class Card(
	val id: Int,
	val winningNums: Set<Int>,
	val nums: List<Int>
) {
	val worth: Int
		get() = nums
			.map { n ->	n in winningNums }
			.filter { it }
			.let { list ->
				if (list.isEmpty()) 0
				else generateSequence(1) { it * 2 }
					.take(list.size)
					.last()
			}
	
	val matchingNumsCount: Int = nums
		.map { it in winningNums }
		.filter { it }
		.size
}

fun parseInput(input: String): List<Card> {
	return input.lines().map { line ->
		Card(
			id = Regex("[0-9]+").find(line)!!.value.toInt(),
			winningNums = line
				.substring(line.indexOf(':') + 1, line.indexOf('|'))
				.split(' ')
				.filter { it.isNotBlank() }
				.map { it.toInt() }
				.toSet(),
			nums = line
				.substring(line.indexOf('|') + 1)
				.split(' ')
				.filter { it.isNotBlank() }
				.map { it.toInt() },
		)
	}
}

fun produceRecordTable(all: List<Card>): Map<Int, Int> {
	return all.associate {
		it.id to it.matchingNumsCount
	}
}

// First attempt at part two which ran out of memory and otherwise too slow
fun rec(all: List<Card>, card: Card): List<Card> {
	
	val matching = card.matchingNumsCount
	println("card ${card.id} | matching: $matching")
	
	if (matching == 0) return listOf(card)
	
	val copies = all.subList(card.id, card.id+matching)
	
	return run {
		if (copies.isEmpty()) emptyList()
		else copies.flatMap {
			println("${it.id} copied")
			rec(all, it)
		}
	} + card
}

fun recCount(records: Map<Int, Int>, cardId: Int): Int {
	val copyIds = cardId+1..cardId+records[cardId]!!
	return 1 + copyIds.sumOf { recCount(records, it) }
}

// --------------------------------

fun partOne(input: String): Int {
	return parseInput(input).sumOf {
		it.worth
	}
}

fun partTwo(input: String): Int {
	val cards = parseInput(input)
	val birthRecords = produceRecordTable(cards)
	
	return cards.sumOf { recCount(birthRecords, it.id) }
	
}

//println("Part one: ${partOne(input)}")
println("Part two: ${partTwo(input)}")