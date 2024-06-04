package cryptography

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
    val inputFilename = println("Input image file:").run { readln() }
    val outputFilename = println("Output image file:").run { readln() }
    val message = println("Message to hide:").run { readln() }
    val bufferedImage = try {
        ImageIO.read(File(inputFilename))
    } catch (e: IOException) {
        return "Can't read input file! ${e.message}"
    }

    val messageByteArray = message.encodeToByteArray() + byteArrayOf(0, 0, 3)
    val messageBitLength = messageByteArray.size * 8
    if (messageBitLength > bufferedImage.width * bufferedImage.height) {
        return "The input image is not large enough to hold this message."
    }

    val messageBitArray = byteToBits(messageByteArray)

    loop@ for (x in 0 until bufferedImage.width) {
        for (y in 0 until bufferedImage.height) {
            val index = x * bufferedImage.width + y
            if (index == messageBitLength) {
                break@loop
            }
            val rgb = bufferedImage.getRGB(x, y)
            val bit = messageBitArray[index]
            val newRGB = rgb shr 1 shl 1 or bit
            bufferedImage.setRGB(x, y,  newRGB)
        }
    }

    return try {
        ImageIO.write(bufferedImage, "png", File(outputFilename))
        "Message saved in $inputFilename image."
    } catch (e: IOException) {
        "Can't write output file! ${e.message}"
    }
}

fun byteToBits(byteArray: ByteArray): IntArray {
    val result = IntArray(byteArray.size * 8)
    var i = 0
    for (b in byteArray) {
        for (bit in 7 downTo 0) {
            result[i++] = b.toInt() shr bit and 1
        }
    }
    return result
}