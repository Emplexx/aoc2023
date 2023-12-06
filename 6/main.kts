val input = listOf(
	44 to 277,
	89 to 1136,
	96 to 1890,
	91 to 1768
)

val input2Sample = 71530L to 940200L
val input2 = 44_899_691L to 277_113_618_901_768L

// --------------------------------

fun partOne(input: List<Pair<Int, Int>>) {
	
	input.map { (time, record) ->
		
		val beat = mutableListOf<Int>()
		
		for (speed in 1 until time) {
			
			val rem = time - speed
			val distance = speed * rem
			if (distance > record) beat.add(distance)
			
		}
		
		beat.size
	}
		.also { println(it) }
		.reduce { acc, i -> acc * i }
		.also { println(it) }
}

fun partTwo(input: Pair<Long, Long>) {
	val (time, record) = input
	
	var atLeast: Long = 0
	for (speed in 1 until time) {
		val rem = time - speed
		val distance = speed * rem
		if (distance > record) {
			atLeast = speed
			break
		}
	}
	
	println(atLeast)
	
	var atMost: Long = 0
	for (speed in time downTo 1) {
		val rem = time - speed
		val distance = speed * rem
		if (distance > record) {
			atMost = speed
			break
		}
	}
	println(atMost)
	
	println("Result: ${time - atLeast - (time - atMost) + 1}")
	
}

//partOne(input)
//partTwo(input2Sample)
partTwo(input2)