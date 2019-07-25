package astminer.paths

import astminer.common.OrientedNodeType
import astminer.common.PathContext
import astminer.common.PathStorage
import astminer.common.storage.*
import java.io.File

class RegistratedPathStorage {

//    private val tokensMap: IncrementalIdStorage<String> = IncrementalIdStorage()
//    private val orientedNodeTypesMap: IncrementalIdStorage<OrientedNodeType> = IncrementalIdStorage()
//    private val pathsMap: IncrementalIdStorage<List<Long>> = IncrementalIdStorage()

    private val pathContextsPerEntity: MutableMap<String, Collection<PathContextId>> = HashMap()

    data class PathContextId(val startTokenId: Long, val pathId: Long, val endTokenId: Long)

    private fun mapOrientedNodeType(node : OrientedNodeType, nodeDict: MutableMap<OrientedNodeType, Long>) : Long {
        return nodeDict[node] ?: 0L
    }

    private fun doStore(pathContext: PathContext,
                        tokenDict: MutableMap<String, Long>,
                        nodeDict: MutableMap<OrientedNodeType, Long>,
                        pathDict: MutableMap<List<Long>, Long>): PathContextId {
        val startTokenId = tokenDict[pathContext.startToken] ?: 0L
        val endTokenId = tokenDict[pathContext.endToken] ?: 0L
        val path = pathContext.orientedNodeTypes.map { mapOrientedNodeType(it, nodeDict) }
        val pathId = pathDict[path] ?: 0L

        return PathContextId(startTokenId, pathId, endTokenId)
    }

    fun store(pathContexts: Collection<PathContext>, entityId: String,
                       tokenDict: MutableMap<String, Long>,
                       nodeDict: MutableMap<OrientedNodeType, Long>,
                       pathDict: MutableMap<List<Long>, Long>) {
        val pathContextIds = pathContexts.map { doStore(it, tokenDict, nodeDict, pathDict) }
        pathContextsPerEntity[entityId] = pathContextIds
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

    fun save(directoryPath: String) {
        File(directoryPath).mkdirs()

        dumpPathContexts(File("$directoryPath/path_contexts.csv"))
    }
}