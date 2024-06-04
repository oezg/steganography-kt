package cryptography

import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

tailrec fun main() {
    println("Task (hide, show, exit):")
    val output = when (val input = readln()) {
        "hide" -> hide()
        "show" -> show()
        "exit" -> "Bye!"
        else -> "Wrong task: $input"
    }
    println(output)
    if (output != "Bye!") {
        main()
    }
}

fun show(): String {
    val inputFilename = println("Input image file:").run { readln() }
    val bufferedImage = try {
        ImageIO.read(File(inputFilename))
    } catch (e: IOException) {
        return "Can't read input file! ${e.message}"
    }

    val messageBytes = mutableListOf<Byte>()
    var tempByte = 0
    var index = 0

    loop@ for (y in 0 until bufferedImage.height) {
        for (x in 0 until bufferedImage.width) {
            tempByte = tempByte shl 1 or (bufferedImage.getRGB(x, y) and 1)
            if (++index == 8) {
                messageBytes.add(tempByte.toByte())
                index = 0
                tempByte = 0
            }
            if (messageBytes.takeLast(3) == listOf(0, 0, 3)) {
                break@loop
            }
        }
    }
    return "Message: \n${messageBytes.toByteArray().decodeToString()}"
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

    loop@ for (y in 0 until bufferedImage.height) {
        for (x in 0 until bufferedImage.width) {
            val index = y * bufferedImage.height + x
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
    var index = 0
    for (byte in byteArray) {
        for (positionFromLeft in 7 downTo 0) {
            result[index++] = byte.toInt() shr positionFromLeft and 1
        }
    }
    return result
}