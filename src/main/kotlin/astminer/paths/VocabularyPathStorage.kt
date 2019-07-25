package astminer.paths

import astminer.common.*
import astminer.common.storage.*
import java.io.File

class VocabularyPathStorage : PathStorage {

    private val tokensMap: IncrementalIdStorage<String> = IncrementalIdStorage()
    private val orientedNodeTypesMap: IncrementalIdStorage<OrientedNodeType> = IncrementalIdStorage()
    private val pathsMap: IncrementalIdStorage<List<Long>> = IncrementalIdStorage()

    private val tokenDict: MutableMap<String, Long> = HashMap()
    private val nodeDict: MutableMap<OrientedNodeType, Long> = HashMap()
    private val pathDict: MutableMap<List<Long>, Long> = HashMap()

    private val pathContextsPerEntity: MutableMap<String, Collection<PathContextId>> = HashMap()

    data class PathContextId(val startTokenId: Long, val pathId: Long, val endTokenId: Long)

    private fun doStore(pathContext: PathContext): PathContextId {
        val startTokenId = tokensMap.record(pathContext.startToken)
        val endTokenId = tokensMap.record(pathContext.endToken)
        val orientedNodesIds = pathContext.orientedNodeTypes.map { orientedNodeTypesMap.record(it) }
        val pathId = pathsMap.record(orientedNodesIds)
        tokenDict[pathContext.startToken] = startTokenId

        return PathContextId(startTokenId, pathId, endTokenId)
    }

    override fun store(pathContexts: Collection<PathContext>, entityId: String) {
        val pathContextIds = pathContexts.map { doStore(it) }
        pathContextsPerEntity[entityId] = pathContextIds
    }

    private fun dumpTokenStorage(file: File) {
        dumpIdStorage(tokensMap, "token", tokenToCsvString, file)
    }

    private fun dumpOrientedNodeTypesStorage(file: File) {
        dumpIdStorage(orientedNodeTypesMap, "node_type", orientedNodeToCsvString, file)
    }

    private fun dumpPathsStorage(file: File) {
        dumpIdStorage(pathsMap, "path", pathToCsvString, file)
    }

    private fun dumpPathContexts(file: File) {
        val lines = mutableListOf("id,path_contexts")
        pathContextsPerEntity.forEach { id, pathContexts ->
            val pathContextsString = pathContexts.joinToString(separator = ";") { pathContextId ->
                "${pathContextId.startTokenId} ${pathContextId.pathId} ${pathContextId.endTokenId}"
            }
            lines.add("$id,$pathContextsString")
        }

        writeLinesToFile(lines, file)
    }

    override fun save(directoryPath: String) {
        File(directoryPath).mkdirs()
        dumpTokenStorage(File("$directoryPath/tokens.csv"))
        dumpOrientedNodeTypesStorage(File("$directoryPath/node_types.csv"))
        dumpPathsStorage(File("$directoryPath/paths.csv"))

        dumpPathContexts(File("$directoryPath/path_contexts.csv"))

//        saveTokenDict(File("$directoryPath/tokens.dic"), tokenDict, tokenFromCsvString)
//        saveNodeDict(File("$directoryPath/nodes.dic"), nodeDict, nodeTypeFromCsvString)
//        savePathDict(File("$directoryPath/paths.dic"), pathDict, pathToCsvString)

    }
}