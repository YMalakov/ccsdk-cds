/*
 *  Copyright © 2017-2018 AT&T Intellectual Property.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.onap.ccsdk.cds.controllerblueprints.resource.dict.utils

import com.att.eelf.configuration.EELFLogger
import org.apache.commons.collections.CollectionUtils
import org.onap.ccsdk.cds.controllerblueprints.core.utils.TopologicalSortingUtils
import org.onap.ccsdk.cds.controllerblueprints.resource.dict.ResourceAssignment
import com.att.eelf.configuration.EELFManager
import java.util.ArrayList
/**
 * BulkResourceSequencingUtils.
 *
 * @author Brinda Santh
 */
object BulkResourceSequencingUtils {
    private val log: EELFLogger = EELFManager.getInstance().getLogger(BulkResourceSequencingUtils::class.java)

    @JvmStatic
    fun process(resourceAssignments: MutableList<ResourceAssignment>): List<List<ResourceAssignment>> {
        val resourceAssignmentMap: MutableMap<String, ResourceAssignment> = hashMapOf()
        val sequenceBatchResourceAssignment = ArrayList<List<ResourceAssignment>>()
        log.info("Assignments ({})", resourceAssignments)
        // Prepare Map
        resourceAssignments.forEach { resourceAssignment ->
            log.trace("Processing Key ({})", resourceAssignment.name)
            resourceAssignmentMap.put(resourceAssignment.name, resourceAssignment)
        }

        val startResourceAssignment = ResourceAssignment()
        startResourceAssignment.name = "*"

        // Preepare Sorting Map
        val topologySorting = TopologicalSortingUtils<ResourceAssignment>()
        resourceAssignmentMap.forEach { _, resourceAssignment ->
            if (CollectionUtils.isNotEmpty(resourceAssignment.dependencies)) {
                for (dependency in resourceAssignment.dependencies!!) {
                    topologySorting.add(resourceAssignmentMap[dependency]!!, resourceAssignment)
                }
            } else {
                topologySorting.add(startResourceAssignment, resourceAssignment)
            }
        }

        val sequencedResourceAssignments: MutableList<ResourceAssignment> = topologySorting.topSort()!! as MutableList<ResourceAssignment>
        log.info("Sorted Sequenced Assignments ({})", sequencedResourceAssignments)

        var batchResourceAssignment: MutableList<ResourceAssignment>? = null
        var batchAssignmentName: MutableList<String>? = null

        // Prepare Sorting
        sequencedResourceAssignments.forEachIndexed { index, resourceAssignment ->

            var previousResourceAssignment: ResourceAssignment? = null

            if (index > 0) {
                previousResourceAssignment = sequencedResourceAssignments[index - 1]
            }

            var dependencyPresence = false
            if (batchAssignmentName != null && resourceAssignment.dependencies != null) {
                dependencyPresence = CollectionUtils.containsAny(batchAssignmentName, resourceAssignment.dependencies)
            }

            log.trace("({}) -> Checking ({}), with ({}), result ({})", resourceAssignment.name,
                    batchAssignmentName, resourceAssignment.dependencies, dependencyPresence)

            if (previousResourceAssignment != null && resourceAssignment.dictionarySource != null
                    && resourceAssignment.dictionarySource!!.equals(previousResourceAssignment.dictionarySource, true)
                    && !dependencyPresence) {
                batchResourceAssignment!!.add(resourceAssignment)
                batchAssignmentName!!.add(resourceAssignment.name)
            } else {
                if (batchResourceAssignment != null) {
                    sequenceBatchResourceAssignment.add(batchResourceAssignment!!)
                    log.trace("Created old Set ({})", batchAssignmentName)
                }
                batchResourceAssignment = arrayListOf()
                batchResourceAssignment!!.add(resourceAssignment)

                batchAssignmentName = arrayListOf()
                batchAssignmentName!!.add(resourceAssignment.name)
            }

            if (index == sequencedResourceAssignments.size - 1) {
                log.trace("Created old Set ({})", batchAssignmentName)
                sequenceBatchResourceAssignment.add(batchResourceAssignment!!)
            }
        }
        log.info("Batched Sequence : ({})", sequenceBatchResourceAssignment)

        return sequenceBatchResourceAssignment
    }

}