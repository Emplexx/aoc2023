import kotlin.math.sqrt

// https://tutorialwing.com/kotlin-program-to-find-all-prime-factors-of-given-number/
fun Int.getPrimeFactors(): List<Int> {
	
	val list = mutableListOf<Int>()
	var n = this
	
	while (n % 2 == 0) {
		list.add(2)
		n /= 2
	}
	
	val sqrt = sqrt(n.toDouble()).toInt()
	
	for (i in 3..sqrt step 2) {
		while (n % i == 0) {
			list.add(i)
			n /= i
		}
	}
	
	if (n > 2) {
		list.add(n)
	}
	
	return list
	
}

val sampleList = listOf(260, 380, 460, 560, 760, 960)
val inputList = listOf(11653, 19783, 19241, 16531, 12737, 14363)

sealed interface Factor {
	val num: Int

	data class Regular(override val num: Int) : Factor {

		override fun toString(): String {
			return num.toString()
		}
		
	}
	data class WithPower(
		override val num: Int,
		val pow: Int
	) : Factor {
		override fun toString(): String {
			return "$num^$pow"
		}
	}
} 



fun getCommonDivisible(
	numbers: List<Int> = inputList
): Long {
	
	val factors = numbers
		.map { num ->
			num to num
				.getPrimeFactors()
				.fold(mutableListOf<Factor>()) { list, n ->
	
					list.also {
						when {
							list.isEmpty() || list.last().num != n ->
								list.add(Factor.Regular(n))
							else -> {

								when (val item = list[list.lastIndex]) {
									is Factor.Regular -> list[list.lastIndex] = Factor.WithPower(n, 2)
									is Factor.WithPower -> list[list.lastIndex] = Factor.WithPower(n, item.pow + 1)
									else -> error("Wtf")
								}
																
							}
						}
					}
				}
			
		}
		.onEach { (n, factors) ->
			println("$n -> $factors")
		}
	
	
	return 0
}

getCommonDivisible()