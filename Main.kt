package cryptography

import java.awt.Color
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

tailrec fun main() {
    println("Task (hide, show, exit):")
    val output = when (val input = readln()) {
        "hide" -> hide()
        "show" -> "Obtaining message from image."
        "exit" -> "Bye!"
        else -> "Wrong task: $input"
    }
    println(output)
    if (output != "Bye!") {
        main()
    }
}

fun hide(): String {
    println("Input image file:")
    val inputFilename = readln()
    println("Output image file:")
    val outputFilename = readln()
    val bufferedImage = try {
        ImageIO.read(File(inputFilename))
    } catch (e: IOException) {
        return "Can't read input file!"
    }
    println("Input Image: $inputFilename")
    for (x in 0 until bufferedImage.width) {
        for (y in 0 until bufferedImage.height) {
            val color = Color(bufferedImage.getRGB(x, y))
            val newColor = Color(color.red or 1, color.green or 1, color.blue or 1)
            bufferedImage.setRGB(x, y, newColor.rgb)
        }
    }
    return try {
        ImageIO.write(bufferedImage, "png", File(outputFilename))
        println("Output Image: $outputFilename")
        "Image $outputFilename is saved."
    } catch (e: IOException) {
        "Can't write output file!"
    }
}