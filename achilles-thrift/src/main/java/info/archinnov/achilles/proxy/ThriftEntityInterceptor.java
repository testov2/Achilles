package info.archinnov.achilles.proxy;

import info.archinnov.achilles.composite.ThriftCompositeFactory;
import info.archinnov.achilles.context.ThriftPersistenceContext;
import info.archinnov.achilles.dao.ThriftGenericWideRowDao;
import info.archinnov.achilles.entity.metadata.CounterProperties;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import info.archinnov.achilles.entity.operations.ThriftEntityLoader;
import info.archinnov.achilles.entity.operations.ThriftEntityPersister;
import info.archinnov.achilles.entity.operations.ThriftEntityProxifier;
import info.archinnov.achilles.helper.ThriftPropertyHelper;
import info.archinnov.achilles.iterator.factory.ThriftIteratorFactory;
import info.archinnov.achilles.iterator.factory.ThriftKeyValueFactory;
import info.archinnov.achilles.proxy.wrapper.builder.ThriftCounterWideMapWrapperBuilder;
import info.archinnov.achilles.proxy.wrapper.builder.ThriftCounterWrapperBuilder;
import info.archinnov.achilles.proxy.wrapper.builder.ThriftJoinWideMapWrapperBuilder;
import info.archinnov.achilles.proxy.wrapper.builder.ThriftWideMapWrapperBuilder;
import info.archinnov.achilles.type.Counter;
import me.prettyprint.hector.api.beans.Composite;

/**
 * ThriftEntityInterceptor
 * 
 * @author DuyHai DOAN
 * 
 */
public class ThriftEntityInterceptor<T> extends
		EntityInterceptor<ThriftPersistenceContext, T>
{

	private ThriftPropertyHelper thriftCompositeHelper = new ThriftPropertyHelper();
	private ThriftKeyValueFactory thriftKeyValueFactory = new ThriftKeyValueFactory();
	private ThriftIteratorFactory thriftIteratorFactory = new ThriftIteratorFactory();
	private ThriftCompositeFactory thriftCompositeFactory = new ThriftCompositeFactory();
	private ThriftEntityProxifier thriftProxifier;

	public ThriftEntityInterceptor() {
		super.loader = new ThriftEntityLoader();
		super.persister = new ThriftEntityPersister();
		this.thriftProxifier = new ThriftEntityProxifier();
		super.proxifier = this.thriftProxifier;
	}

	@Override
	protected Object buildCounterWrapper(PropertyMeta<?, ?> propertyMeta)
	{
		Object result;
		ThriftPersistenceContext thriftContext = (ThriftPersistenceContext) context;
		CounterProperties counterProperties = propertyMeta.getCounterProperties();
		Composite keyComp = thriftCompositeFactory.createKeyForCounter(counterProperties.getFqcn(),
				key, counterProperties.getIdMeta());
		Composite comp = thriftCompositeFactory.createBaseForCounterGet(propertyMeta);
		result = ThriftCounterWrapperBuilder.builder(thriftContext) //
				.counterDao(thriftContext.getCounterDao())
				.columnName(comp)
				.readLevel(propertyMeta.getReadConsistencyLevel())
				.writeLevel(propertyMeta.getWriteConsistencyLevel())
				.key(keyComp)
				.build();
		return result;
	}

	@Override
	protected <K, V> Object buildWideMapWrapper(PropertyMeta<K, V> propertyMeta)
	{
		ThriftPersistenceContext thriftContext = (ThriftPersistenceContext) context;
		String columnFamilyName = context.isWideRow() ? context.getEntityMeta().getTableName()
				: propertyMeta.getExternalTableName();

		ThriftGenericWideRowDao wideRowDao = thriftContext.findWideRowDao(columnFamilyName);

		return ThriftWideMapWrapperBuilder //
				.builder(key, wideRowDao, propertyMeta)
				.context(thriftContext)
				.interceptor(this)
				.thriftPropertyHelper(thriftCompositeHelper)
				.thriftKeyValueFactory(thriftKeyValueFactory)
				.thriftIteratorFactory(thriftIteratorFactory)
				.thriftCompositeFactory(thriftCompositeFactory)
				.build();
	}

	@Override
	protected <K> Object buildCounterWideMapWrapper(PropertyMeta<K, Counter> propertyMeta)
	{
		ThriftPersistenceContext thriftContext = (ThriftPersistenceContext) context;
		ThriftGenericWideRowDao counterWideMapDao = thriftContext.findWideRowDao(propertyMeta
				.getExternalTableName());

		return ThriftCounterWideMapWrapperBuilder //
				.builder(key, counterWideMapDao, propertyMeta)
				.interceptor(this)
				.context(thriftContext)
				.thriftPropertyHelper(thriftCompositeHelper)
				.thriftKeyValueFactory(thriftKeyValueFactory)
				.thriftIteratorFactory(thriftIteratorFactory)
				.thriftCompositeFactory(thriftCompositeFactory)
				.build();
	}

	@Override
	protected <K, V> Object buildJoinWideMapWrapper(PropertyMeta<K, V> propertyMeta)
	{

		ThriftPersistenceContext thriftContext = (ThriftPersistenceContext) context;
		String columnFamilyName = context.isWideRow() ? context.getEntityMeta().getTableName()
				: propertyMeta.getExternalTableName();
		ThriftGenericWideRowDao wideRowDao = thriftContext.findWideRowDao(columnFamilyName);

		return ThriftJoinWideMapWrapperBuilder //
				.builder(key, wideRowDao, propertyMeta)
				.interceptor(this)
				.context(thriftContext)
				.thriftPropertyHelper(thriftCompositeHelper)
				.thriftCompositeFactory(thriftCompositeFactory)
				.proxifier(this.thriftProxifier)
				.thriftIteratorFactory(thriftIteratorFactory)
				.thriftKeyValueFactory(thriftKeyValueFactory)
				.loader((ThriftEntityLoader) loader)
				.persister((ThriftEntityPersister) persister)
				.build();
	}

	@Override
	protected <K, V> Object buildWideRowWrapper(PropertyMeta<K, V> propertyMeta)
	{
		ThriftPersistenceContext thriftContext = (ThriftPersistenceContext) context;
		ThriftGenericWideRowDao wideRowDao = thriftContext.findWideRowDao(context
				.getEntityMeta()
				.getTableName());

		return ThriftWideMapWrapperBuilder.builder(key, wideRowDao, propertyMeta) //
				.interceptor(this)
				.context(thriftContext)
				.thriftPropertyHelper(thriftCompositeHelper)
				.thriftKeyValueFactory(thriftKeyValueFactory)
				.thriftIteratorFactory(thriftIteratorFactory)
				.thriftCompositeFactory(thriftCompositeFactory)
				.build();
	}

}
