/* Base constants & metadata for JPA */

@file:Embeddable
@file:SequenceGenerator(name = TASK_SEQ, allocationSize = ALLOC_CNT)
package io.serge2nd.taskhero.db

import jakarta.persistence.Embeddable
import jakarta.persistence.SequenceGenerator

const val TASK_SEQ = "task_id_seq"

const val ALLOC_CNT = 50
