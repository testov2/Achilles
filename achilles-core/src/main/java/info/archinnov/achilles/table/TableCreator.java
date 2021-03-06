package info.archinnov.achilles.table;

import info.archinnov.achilles.context.ConfigurationContext;
import info.archinnov.achilles.entity.metadata.EntityMeta;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import java.util.Map;
import java.util.Map.Entry;

/**
 * TableCreator
 * 
 * @author DuyHai DOAN
 * 
 */
public abstract class TableCreator {
    public static final String TABLE_PATTERN = "[a-zA-Z0-9_]+";

    public void validateOrCreateColumnFamilies(Map<Class<?>, EntityMeta> entityMetaMap,
            ConfigurationContext configContext, boolean hasCounter) {
        for (Entry<Class<?>, EntityMeta> entry : entityMetaMap.entrySet()) {

            EntityMeta entityMeta = entry.getValue();
            for (Entry<String, PropertyMeta<?, ?>> entryMeta : entityMeta.getPropertyMetas().entrySet()) {
                PropertyMeta<?, ?> propertyMeta = entryMeta.getValue();

                if (propertyMeta.type().isWideMap()) {
                    validateOrCreateCFForWideMap(propertyMeta, entityMeta.getIdMeta().getValueClass(),
                            configContext.isForceColumnFamilyCreation(), propertyMeta.getExternalTableName(),
                            entityMeta.getClassName());
                }
            }

            validateOrCreateCFForEntity(entityMeta, configContext.isForceColumnFamilyCreation());
        }

        if (hasCounter) {
            validateOrCreateCFForCounter(configContext.isForceColumnFamilyCreation());
        }
    }

    protected abstract void validateOrCreateCFForWideMap(PropertyMeta<?, ?> propertyMeta, Class<?> keyClass,
            boolean forceColumnFamilyCreation, String externalColumnFamilyName, String entityName);

    protected abstract void validateOrCreateCFForEntity(EntityMeta entityMeta, boolean forceColumnFamilyCreation);

    protected abstract void validateOrCreateCFForCounter(boolean forceColumnFamilyCreation);

}
