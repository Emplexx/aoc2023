import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

val fileName = args.getOrNull(0) ?: error("No file path provided")

if (!Files.exists(Paths.get(fileName))) error("No such file $fileName")

val input = File(fileName).readText()

// --------------------------------

enum class Dir {
	Left, Right;
}

data class Node(
	val name: String,
	val left: String,
	val right: String,
) {
	fun get(dir: Dir) = when (dir) {
		Dir.Left -> left
		Dir.Right -> right
	}
}

data class Input(
	val directions: List<Dir>,
	val map: Map<String, Node> 
)

fun parseInput(input: String): Input {
	
	val directions = input.lines()[0]
		.mapNotNull {
			when (it) {
				'L' -> Dir.Left
				'R' -> Dir.Right
				else -> null
			}
		}
	
	val map = input.lines().drop(2)
		.associate { line ->
			
			val nodeKey = line.take(3)
			val node = Node(
				nodeKey,
				line.substring(7, 10),
				line.substring(12, 15),
			)
			nodeKey to node
		}
	
	return Input(directions, map)
	
}

fun partOne(input: String) {
	
	val (dir, map) = parseInput(input)
	
	var node = map["AAA"]!!
	var steps = 0
	var directionStep = 0
	
	do {
		if (directionStep > dir.lastIndex) directionStep = 0
		
		val nextNodeKey = node.get(dir[directionStep])
		node = map[nextNodeKey]!!
		
		steps++
		directionStep++
		
	} while (node.name != "ZZZ")
	
	println("Part one: $steps")
	
}

fun partTwo(input: String) {

	val (dir, map) = parseInput(input)

	val nodes = map.mapNotNull { if (it.key[2] == 'A') it.value else null }

	val steps = nodes.map {
		var node = it
		var steps = 0
		var directionStep = 0
		do {
			if (directionStep > dir.lastIndex) directionStep = 0

			val nextNodeKey = node.get(dir[directionStep])
			node = map[nextNodeKey]!!

			steps++
			directionStep++

		} while (node.name[2] != 'Z')
		steps to node
	}.onEach { (steps, node) ->
		println("$steps steps to reach node $node")
	}
	
	var stepsList = steps.map { it.first.toLong() to it.first.toLong() }.toMutableList()
	
	while (!stepsList.all { it.second == stepsList[0].second }) {
		
		stepsList = stepsList
			.also {
				val i = it.indexOf(stepsList.minBy { (steps, accumulated) -> accumulated })
				val item = it[i]
				it[i] = item.copy(second = item.second + item.first)
			}
		println(stepsList)
	}
	
	println("Part one: ${stepsList}")
	
//	do {
//		if (directionStep > dir.lastIndex) directionStep = 0
//
//		val nextNodesKeys = nodes.map {
//			val next = it.get(dir[directionStep])
//			map[next]!!
//		}
//		nodes = nextNodesKeys
//		
//		steps++
//		directionStep++
//		println("step: $steps | $nodes")
//
//	} while (nodes.all { it.name[2] == 'Z' }.not())

//	println("Part one: $steps")

}

//partOne(input)
partTwo(input)