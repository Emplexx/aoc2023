import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

val fileName = args.getOrNull(0) ?: error("No file path provided")

if (!Files.exists(Paths.get(fileName))) error("No such file $fileName")

val input = File(fileName).readText()

// --------------------------------

data class Hand(
	val cards: List<Char>,
	val bid: Int,
) : Comparable<Hand> {
	
	init {
		if (cards.size != 5) error("A hand should have exactly 5 cards")
	}
	
	val type: Type = run {
		val folded = cards.fold(mutableMapOf<Char, Int>()) { map, char ->
			map[char] = (map[char] ?: 0) + 1
			map
		}

		when (folded.size) {
			1 -> Type.FiveKind
			2 -> when {
				folded.any { it.value == 4 } -> Type.FourKind
				folded.any { it.value == 3 } -> Type.FullHouse
				else -> error("")
			}

			3 -> when {
				folded.any { it.value == 3 } -> Type.ThreeKind
				else -> Type.TwoPair
			}

			4 -> Type.OnePair
			5 -> Type.HighCard

			else -> error("")
		}
	}
	
	override operator fun compareTo(other: Hand): Int {
		val strength = this.type.strength.compareTo(other.type.strength)
		
		if (strength != 0) return strength
		else {
			
			var killMe: Int? = null
			
			for (i in 0..4) {
				val cardStrength = cardStrenghts[cards[i]]!!.compareTo(cardStrenghts[other.cards[i]]!!)

				println("compare ${this} to $other : $cardStrength")
				
				when {
					i == 4 -> killMe = cardStrength
					cardStrength == 0 -> continue
					else -> {
						killMe = cardStrength
						break
					}
				}
			}
			
			return killMe!!
		}
	}
	
}

enum class Type(val strength: Int) {
	FiveKind(7),
	FourKind(6),
	FullHouse(5),
	ThreeKind(4),
	TwoPair(3),
	OnePair(2),
	HighCard(1),
}

val cardStrenghts = mapOf(
	"2" to 2,
	"3" to 3,
	"4" to 4,
	"5" to 5,
	"6" to 6,
	"7" to 7,
	"8" to 8,
	"9" to 9,
	"T" to 10,
	"J" to 11,
	"Q" to 12,
	"K" to 13,
	"A" to 14,
).mapKeys { it.key[0] }

fun parseInput(input: String): List<Hand> {
	return input.lines()
		.map { Hand(
			cards = it.toList().take(5),
			bid = it.substring(6).toInt()
		) }
}

fun partOne(input: String) {
	
	parseInput(input)
		.sortedBy { it }
		.onEach { println(it) }
		.mapIndexed { index, hand -> index + 1 to hand }
		.sumOf { (rank, hand) -> rank * hand.bid  }
		.also { println("Part one: $it") }
}

partOne(input)