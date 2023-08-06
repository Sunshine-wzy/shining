package io.github.sunshinewzy.shining.utils

import java.util.*

object CollectionUtils {

    fun <T> sortDependencies(collection: Collection<T>, dependenciesMapper: (T) -> Set<T>): List<T> {
        val dependencies = collection.associateWith { dependenciesMapper(it).filter(collection::contains) }

        val inp = collection.toMutableList()
        val res = LinkedList<T>()

        while (inp.isNotEmpty()) {
            var changes = false

            inp.removeIf {
                if (res.containsAll(dependencies[it]!!)) {
                    res.add(it)
                    changes = true
                    return@removeIf true
                }

                return@removeIf false
            }

            if (!changes) throw buildCircularDependencyException(collection, inp, res, dependencies, emptyMap())
        }

        return res
    }

    fun <T, B> sortDependenciesMapped(collection: Collection<T>, dependenciesMapper: (T) -> Set<B>, mapper: (T) -> B): List<T> {
        val mapped = collection.map(mapper)
        val dependencies = collection.associateWith { dependenciesMapper(it).filter(mapped::contains) }

        val inp = collection.toMutableList()
        val out = LinkedList<T>()

        while (inp.isNotEmpty()) {
            var changes = false

            inp.removeIf {
                if (out.map(mapper).containsAll(dependencies[it]!!)) {
                    out.add(it)
                    changes = true
                    return@removeIf true
                }

                return@removeIf false
            }

            if (!changes)
                throw buildCircularDependencyException(collection, inp, out, dependencies, emptyMap())

        }

        return out
    }

    /**
     * Sorts the given [collection] by their [beforeThisMapper] and [afterThisMapper] properties.
     *
     * @param collection The collection to sort.
     * @param beforeThisMapper A function that returns a set of elements that should be before the given element.
     * @param afterThisMapper A function that returns a set of elements that should be after the given element.
     * @throws IllegalArgumentException If the collection contains a circular dependency.
     */
    fun <T> sortDependencies(collection: Collection<T>, beforeThisMapper: (T) -> Set<T>, afterThisMapper: (T) -> Set<T>): List<T> {
        val beforeThisMap = collection.associateWith { beforeThisMapper(it).filter(collection::contains) }
        val afterThisMap = collection.associateWith { afterThisMapper(it).filter(collection::contains) }

        val inp = collection.toMutableList()
        val out = LinkedList<T>()

        while (inp.isNotEmpty()) {
            var changes = false

            inp.removeIf { candidate ->
                if (// all elements that should be before this element are already in the output
                    out.containsAll(beforeThisMap[candidate]!!)
                    // no other element requires the candidate to be after it
                    && inp.none { afterThisMap[it]!!.contains(candidate) }
                ) {
                    out.add(candidate)
                    changes = true
                    return@removeIf true
                }

                return@removeIf false
            }

            if (!changes)
                throw buildCircularDependencyException(collection, inp, out, beforeThisMap, afterThisMap)

        }

        return out
    }

    private fun <T, B> buildCircularDependencyException(
        all: Collection<T>,
        inp: Collection<T>, out: Collection<T>,
        beforeThatMap: Map<T, Collection<B>>, afterThatMap: Map<T, Collection<B>>
    ): IllegalArgumentException {
        fun buildDependencyString(list: Collection<*>): String = StringBuilder().apply {
            append("[\n")
            for (element in list) {
                append("  ")
                append(element)
                append(": before that: ")
                append(beforeThatMap[element] ?: "[]")
                append(", after that: ")
                append(afterThatMap[element] ?: "[]")
                append(",\n")
            }
            append("]")

        }.toString()

        return IllegalArgumentException("""
                    Could not sort collection (circular dependencies?): $all
                    Current: ${buildDependencyString(out)}
                    Remaining: ${buildDependencyString(inp)}
                """.trimIndent())
    }

    fun <T> poolDependencies(collection: Collection<T>, dependenciesMapper: (T) -> Set<T>): Set<Set<T>> {
        val dependencies: Map<T, Set<T>> = collection.associateWithTo(HashMap()) { dependenciesMapper(it).filterTo(HashSet(), collection::contains) }
        val dependants: Map<T, Set<T>> = collection.associateWithTo(HashMap()) { collection.filterTo(HashSet()) { candidate -> it in dependencies[candidate]!! } }

        val pools = HashSet<HashSet<T>>()
        val queue = LinkedList(collection)

        while (queue.isNotEmpty()) {
            val pool = HashSet<T>()

            val unexplored = LinkedList<T>()
            unexplored += queue.poll()

            while (unexplored.isNotEmpty()) {
                val entry = unexplored.poll()
                pool += entry

                fun explore(entries: Set<T>) =
                    entries.forEach { if (it !in pool) unexplored += it }

                explore(dependencies[entry]!!)
                explore(dependants[entry]!!)
            }

            pools += pool
        }

        return pools
    }

}