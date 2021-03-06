package info.archinnov.achilles.entity.operations;

import info.archinnov.achilles.context.AchillesPersistenceContext;
import info.archinnov.achilles.entity.metadata.PropertyMeta;

/**
 * EntityLoader
 * 
 * @author DuyHai DOAN
 * 
 */
public interface EntityLoader<CONTEXT extends AchillesPersistenceContext> {

    public <T> T load(CONTEXT context, Class<T> entityClass);

    public <V> void loadPropertyIntoObject(Object realObject, Object key, CONTEXT context,
            PropertyMeta<?, V> propertyMeta);
}
