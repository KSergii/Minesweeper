package minesweeper
import kotlin.random.Random

var x = 0
var y = 0
var start = 0
var poleMine = Array(9) { Array(9) { "." } }
val poleGame = Array(9) { Array(9) { "." } }

fun main() {
    print("How many mines do you want on the field? ")
    val input = readln().toInt()
    random(input)
    game(input)
}

fun printPole() {
    println()
    println("""
        │123456789│
       —│—————————│""".trimIndent())
    for ((i, j) in poleGame.withIndex()) {
       println("${i + 1}│${j.joinToString("")}│")
   }
    print("—│—————————│")
}

fun random(input: Int) {
    poleMine = Array(9) { Array(9) { "." } }
    var count = input
    while (count != 0) {
        val row = Random.nextInt(9)
        val col = Random.nextInt(9)
        if (poleMine[row][col] != "X") {
            poleMine[row][col] = "X"
            count--
        }
    }
    countMine()
}

fun countMine() {
    var count = 0
    for (a in 0 until 9) {
        for (b in 0 until 9) {
            if (poleMine[a][b] == ".") {
                for (c in -1..1) {
                    for (d in -1..1) {
                        try { if (poleMine[a + c][b + d] == "X") count++ } catch (_:Exception) { }
                    }
                }
                if (count > 0) {
                    poleMine[a][b] = "$count"
                    count = 0
                }
            }
        }
    }
}

fun game(input: Int) {
    while (true) {
        printPole()
        print("\nSet/unset mines marks or claim a cell as free: ")
        val inp = readln().split(" ")
        x = inp[1].toInt() - 1
        y = inp[0].toInt() - 1
        var command = inp[2]
        firstStep(input)
        start++

        if (poleMine[x][y] == "." && inp[2] == "free") {
            freeCells()
            freeBoard()
        }
        if (poleMine[x][y] == "X" && inp[2] == "free") {
            loseField()
            printPole()
            println("\nYou stepped on a mine and failed!")
            break
        }
        if (poleMine[x][y] in "1".."8" && inp[2] == "free") poleGame[x][y] = poleMine[x][y]
        if (poleGame[x][y] in "1".."8" && inp[2] == "mine") print("There is a number here!")
        if (poleGame[x][y] == "." && command == "mine") {
            poleGame[x][y] = "*"
            command = ""
        }
        if (poleGame[x][y] == "*" && command == "mine") poleGame[x][y] = "."
        if (winGame(input)) {
            printPole()
            println("\nCongratulations! You found all the mines!")
            break
        }
    }
}

fun firstStep(input: Int) {
    if (poleMine[x][y] == "X" && start == 0) {
        random(input)
        start++
        if (poleMine[x][y] == "X") {
            start--
            firstStep(input)
        }
    }
}

fun winGame(input: Int): Boolean {
    var star = 0
    var x = 0
    var dot = 0
    var y = 0
    for (i in 0..8) {
       for (j in 0..8) {
           if (poleGame[i][j] == "*") {
               star++
               if (poleMine[i][j] == "X") x++
           }
           if (poleGame[i][j] == ".") {
               dot++
               if (poleMine[i][j] == "X") y++
           }
       }
    }
    return (star == x && x == input) || (dot == y && y == input)
}

fun freeCellsFill(x: Int, y: Int, free: String) {
    if (x < 0 || x > 8 || y < 0 || y > 8) return
    if (poleMine[x][y] != free) return
    poleMine[x][y] = "/"
    poleGame[x][y] = "/"
    freeCellsFill(x + 1, y, free)
    freeCellsFill(x + 1, y - 1, free)
    freeCellsFill(x + 1, y + 1, free)
    freeCellsFill(x - 1, y, free)
    freeCellsFill(x - 1, y - 1, free)
    freeCellsFill(x - 1, y + 1, free)
    freeCellsFill(x, y + 1, free)
    freeCellsFill(x, y - 1, free)
}

fun freeCells() {
    val free = poleMine[x][y]
    if (free == "/") return
    freeCellsFill(x, y, free)
}

fun freeBoard() {
    for (a in 0 until 9) {
        for (b in 0 until 9) {
            if (poleMine[a][b] == "/") {
                for (c in -1..1) {
                    for (d in -1..1) {
                        try { if (poleMine[a + c][b + d] in "1".."8")
                            poleGame[a + c][b + d] = poleMine[a + c][b + d]
                        } catch (_:Exception) { }
                    }
                }
            }
        }
    }
}

fun loseField() {
    for (i in 0..8) {
        for (j in 0..8) {
            if (poleMine[i][j] == "X")
                poleGame[i][j] = poleMine[i][j]
        }
    }
}
