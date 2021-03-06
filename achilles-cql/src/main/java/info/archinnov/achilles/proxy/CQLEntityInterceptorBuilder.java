package info.archinnov.achilles.proxy;

import info.archinnov.achilles.context.CQLPersistenceContext;
import info.archinnov.achilles.entity.metadata.EntityMeta;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import info.archinnov.achilles.validation.Validator;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ThriftEntityInterceptorBuilder
 * 
 * @author DuyHai DOAN
 * 
 */
public class CQLEntityInterceptorBuilder<T> {
    private static final Logger log = LoggerFactory.getLogger(CQLEntityInterceptorBuilder.class);

    private T target;
    private Set<Method> lazyLoaded = new HashSet<Method>();
    private CQLPersistenceContext context;

    public static <T> CQLEntityInterceptorBuilder<T> builder(CQLPersistenceContext context, T entity) {
        return new CQLEntityInterceptorBuilder<T>(context, entity);
    }

    public CQLEntityInterceptorBuilder(CQLPersistenceContext context, T entity) {
        Validator.validateNotNull(context, "PersistenceContext for interceptor should not be null");
        Validator.validateNotNull(entity, "Target entity for interceptor should not be null");
        this.context = context;
        this.target = entity;
    }

    public CQLEntityInterceptor<T> build() {
        log.debug("Build interceptor for entity of class {}", context.getEntityMeta().getClassName());

        CQLEntityInterceptor<T> interceptor = new CQLEntityInterceptor<T>();

        EntityMeta entityMeta = context.getEntityMeta();

        Validator.validateNotNull(target, "Target object for interceptor of '"
                + context.getEntityClass().getCanonicalName() + "' should not be null");
        Validator.validateNotNull(entityMeta.getGetterMetas(), "Getters metadata for interceptor of '"
                + context.getEntityClass().getCanonicalName() + "' should not be null");
        Validator.validateNotNull(entityMeta.getSetterMetas(), "Setters metadata for interceptor of '"
                + context.getEntityClass().getCanonicalName() + "'should not be null");

        Validator.validateNotNull(entityMeta.getIdMeta(), "Id metadata for '"
                + context.getEntityClass().getCanonicalName() + "' should not be null");

        interceptor.setTarget(target);
        interceptor.setContext(context);
        interceptor.setGetterMetas(entityMeta.getGetterMetas());
        interceptor.setSetterMetas(entityMeta.getSetterMetas());
        interceptor.setIdGetter(entityMeta.getIdMeta().getGetter());
        interceptor.setIdSetter(entityMeta.getIdMeta().getSetter());

        if (context.isLoadEagerFields()) {
            lazyLoaded.addAll(entityMeta.getEagerGetters());
        }
        interceptor.setAlreadyLoaded(lazyLoaded);
        interceptor.setDirtyMap(new HashMap<Method, PropertyMeta<?, ?>>());
        interceptor.setKey(context.getPrimaryKey());

        return interceptor;
    }
}
