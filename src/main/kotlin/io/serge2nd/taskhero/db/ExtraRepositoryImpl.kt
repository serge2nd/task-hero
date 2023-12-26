package io.serge2nd.taskhero.db

import io.serge2nd.taskhero.etc.entityClass
import io.serge2nd.taskhero.etc.unwrap
import jakarta.persistence.EntityManager
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.persister.entity.EntityPersister

class ExtraRepositoryImpl(private val em: EntityManager) : ExtraRepository {

    private val mappings: (Class<*>) -> EntityPersister =
        em.entityManagerFactory.unwrap<SessionFactoryImplementor>()
            .runtimeMetamodels.mappingMetamodel::getEntityDescriptor

    override val <E : Any> E.ref: E get() = entityClass.let {
        em.getReference(it, mappings(it).identifierMapping.getIdentifier(this))
    }
}
