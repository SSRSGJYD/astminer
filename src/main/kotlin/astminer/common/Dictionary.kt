package astminer.common

import astminer.common.OrientedNodeType
import astminer.common.storage.IncrementalIdStorage
import astminer.common.storage.writeLinesToFile
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.Exception


fun loadTokenDict(file : String) : MutableMap<String, Long> {
    println("loading token dictionary")
    var tokenDict : MutableMap<String, Long> = HashMap()
    var fileReader = BufferedReader(FileReader(file))
    fileReader.readLine()
    // Read the file line by line starting from the second line
    var line = fileReader.readLine()
    while (line != null) {
        val firstIndex = line.indexOf(",")
        try {
            val id = line.substring(0, firstIndex).toLong()
            val str = line.substring(firstIndex+1)
            tokenDict[str] = id
        }
        catch (e: Exception) {
        }
        finally {
            line = fileReader.readLine()
        }
    }
    println("loading token dictionary finished")
    return tokenDict
}


fun loadNodeDict(file : String) : MutableMap<OrientedNodeType, Long> {
    println("loading node dictionary")
    var nodeDict : MutableMap<OrientedNodeType, Long> = HashMap()
    var fileReader = BufferedReader(FileReader(file))
    fileReader.readLine()
    // Read the file line by line starting from the second line
    var line = fileReader.readLine()
    while (line != null) {
        val tokens = line.split(",")
        if (tokens.size > 1) {
            var words = tokens[1].split(" ")
            var typeLabel = words[0]
            var direction : Direction
            if(words[1] == "UP"){
                direction = Direction.UP
            }
            else{
                direction = Direction.DOWN
            }
            var nodeType = OrientedNodeType(typeLabel, direction)
            nodeDict[nodeType] = tokens[0].toLong()
        }
        line = fileReader.readLine()
    }
    println("loading node dictionary finished")
    return nodeDict
}

fun loadPathDict(file : String) : MutableMap<List<Long>, Long> {
    println("loading path dictionary")
    var pathDict : MutableMap<List<Long>, Long> = HashMap()
    var fileReader = BufferedReader(FileReader(file))
    fileReader.readLine()
    // Read the file line by line starting from the second line
    var line = fileReader.readLine()
    while (line != null) {
        val tokens = line.split(",")
        if (tokens.size > 1) {
            var words = tokens[1].split(" ")
            var list = mutableListOf<Long>()
            words.forEach { list.add(it.toLong()) }
            pathDict[list] = tokens[0].toLong()
        }
        line = fileReader.readLine()
    }
    println("loading path dictionary finished")
    return pathDict
}

fun saveTokenDict(file : File, dict : MutableMap<String, Long>,
                  csvSerializer: (String) -> String) {

    val lines = mutableListOf("token, id")
    dict.forEach {
        val id = it.value
        val item = it.key
        lines.add("${csvSerializer.invoke(item)}, $id")
    }
    writeLinesToFile(lines, file)
}


fun saveNodeDict(file : File, dict : MutableMap<OrientedNodeType, Long>,
                 csvSerializer: (OrientedNodeType) -> String) {
    val lines = mutableListOf("node_type, id")
    dict.forEach {
        val id = it.value
        val item = it.key
        lines.add("${csvSerializer.invoke(item)}, $id")
    }
    writeLinesToFile(lines, file)
}

fun savePathDict(file : File, dict : MutableMap<List<Long>, Long>,
                 csvSerializer: (List<Long>) -> String) {
    val lines = mutableListOf("path, id")
    dict.forEach {
        val id = it.value
        val item = it.key
        lines.add("${csvSerializer.invoke(item)}, $id")
    }
    writeLinesToFile(lines, file)
}