/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.modelix.model.client

import org.modelix.model.api.IIdGenerator

actual class IdGenerator internal actual constructor(clientId: Int) : IIdGenerator {
    private var idSequence: Long
    private val clientId: Long = clientId.toLong()
    actual override fun generate(): Long {
        return generate(1).first
    }
    actual fun generate(quantity: Int): LongRange {
        require(quantity >= 1)
        idSequence += quantity.toLong()
        val lastId = idSequence
        if (lastId ushr 32 != clientId) {
            throw RuntimeException("End of ID range")
        }
        val firstId = lastId - quantity + 1
        return LongRange(firstId, lastId)
    }

    init {
        idSequence = this.clientId shl 32
    }

    actual companion object {
        private val instances: MutableMap<Int, IdGenerator> = HashMap()
        actual fun getInstance(clientId: Int): IdGenerator {
            return instances.getOrPut(clientId) { IdGenerator(clientId) }
        }
        actual fun newInstance(clientId: Int): IdGenerator = IdGenerator(clientId)
    }
}
