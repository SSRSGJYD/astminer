package astminer.examples

import astminer.common.OrientedNodeType
import astminer.common.PathStorage
import astminer.common.loadNodeDict
import astminer.common.loadPathDict
import astminer.common.loadTokenDict
import astminer.parse.antlr.python.PythonParser
import astminer.paths.*
import java.io.File



fun allPythonFiles(generate_vocab:Boolean) {
    val folder = "./data/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))

    if(generate_vocab) {
        var storage = VocabularyPathStorage()
        File(folder).forFilesWithSuffix(".py") { file ->
            try {
                val node = PythonParser().parse(file.inputStream()) ?: return@forFilesWithSuffix
                val paths = miner.retrievePaths(node)

                storage.store(paths.map { toPathContext(it) }, entityId = file.path)
            }
            catch (e:Exception) {
                println(e)
            }
        }
        storage.save("output")
    }
    else{
        var storage = RegistratedPathStorage()
        val tokenDict = loadTokenDict("input/tokens.csv")
        val nodeDict = loadNodeDict("input/node_types.csv")
        val pathDict = loadPathDict("input/paths.csv")
        File(folder).forFilesWithSuffix(".py") { file ->
            try {
                val node = PythonParser().parse(file.inputStream()) ?: return@forFilesWithSuffix
                val paths = miner.retrievePaths(node)

                storage.store(paths.map { toPathContext(it) }, entityId = file.path,
                                tokenDict = tokenDict, nodeDict = nodeDict, pathDict = pathDict)
            }
            catch (e:Exception) {
                println(e)
            }
        }
        storage.save("output")
    }

}