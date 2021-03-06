package info.archinnov.achilles.proxy.wrapper.builder;

import info.archinnov.achilles.composite.ThriftCompositeFactory;
import info.archinnov.achilles.context.ThriftPersistenceContext;
import info.archinnov.achilles.dao.ThriftGenericWideRowDao;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import info.archinnov.achilles.entity.operations.ThriftEntityLoader;
import info.archinnov.achilles.entity.operations.ThriftEntityPersister;
import info.archinnov.achilles.entity.operations.ThriftEntityProxifier;
import info.archinnov.achilles.helper.ThriftPropertyHelper;
import info.archinnov.achilles.iterator.factory.ThriftIteratorFactory;
import info.archinnov.achilles.iterator.factory.ThriftKeyValueFactory;
import info.archinnov.achilles.proxy.ThriftEntityInterceptor;
import info.archinnov.achilles.proxy.wrapper.ThriftJoinWideMapWrapper;

/**
 * ThriftJoinWideMapWrapperBuilder
 * 
 * @author DuyHai DOAN
 * 
 */
public class ThriftJoinWideMapWrapperBuilder<K, V>
{
	private Object id;
	private ThriftGenericWideRowDao dao;
	private PropertyMeta<K, V> joinExternalWideMapMeta;
	private ThriftEntityInterceptor<?> interceptor;
	private ThriftEntityPersister persister;
	private ThriftEntityLoader loader;
	private ThriftEntityProxifier proxifier;
	private ThriftPropertyHelper thriftPropertyHelper;
	private ThriftCompositeFactory thriftCompositeFactory;
	private ThriftKeyValueFactory thriftKeyValueFactory;
	private ThriftIteratorFactory thriftIteratorFactory;
	private ThriftPersistenceContext context;

	public ThriftJoinWideMapWrapperBuilder(Object id, ThriftGenericWideRowDao dao,
			PropertyMeta<K, V> joinExternalWideMapMeta)
	{
		this.dao = dao;
		this.id = id;
		this.joinExternalWideMapMeta = joinExternalWideMapMeta;
	}

	public static <K, V> ThriftJoinWideMapWrapperBuilder<K, V> builder(Object id,
			ThriftGenericWideRowDao dao, PropertyMeta<K, V> joinExternalWideMapMeta)
	{
		return new ThriftJoinWideMapWrapperBuilder<K, V>(id, dao, joinExternalWideMapMeta);
	}

	public ThriftJoinWideMapWrapperBuilder<K, V> interceptor(ThriftEntityInterceptor<?> interceptor)
	{
		this.interceptor = interceptor;
		return this;
	}

	public ThriftJoinWideMapWrapperBuilder<K, V> context(ThriftPersistenceContext context)
	{
		this.context = context;
		return this;
	}

	public ThriftJoinWideMapWrapperBuilder<K, V> proxifier(ThriftEntityProxifier proxifier)
	{
		this.proxifier = proxifier;
		return this;
	}

	public ThriftJoinWideMapWrapperBuilder<K, V> persister(ThriftEntityPersister persister)
	{
		this.persister = persister;
		return this;
	}

	public ThriftJoinWideMapWrapperBuilder<K, V> loader(ThriftEntityLoader loader)
	{
		this.loader = loader;
		return this;
	}

	public ThriftJoinWideMapWrapperBuilder<K, V> thriftPropertyHelper(
			ThriftPropertyHelper thriftPropertyHelper)
	{
		this.thriftPropertyHelper = thriftPropertyHelper;
		return this;
	}

	public ThriftJoinWideMapWrapperBuilder<K, V> thriftCompositeFactory(
			ThriftCompositeFactory thriftCompositeFactory)
	{
		this.thriftCompositeFactory = thriftCompositeFactory;
		return this;
	}

	public ThriftJoinWideMapWrapperBuilder<K, V> thriftKeyValueFactory(
			ThriftKeyValueFactory thriftKeyValueFactory)
	{
		this.thriftKeyValueFactory = thriftKeyValueFactory;
		return this;
	}

	public ThriftJoinWideMapWrapperBuilder<K, V> thriftIteratorFactory(
			ThriftIteratorFactory thriftIteratorFactory)
	{
		this.thriftIteratorFactory = thriftIteratorFactory;
		return this;
	}

	public ThriftJoinWideMapWrapper<K, V> build()
	{
		ThriftJoinWideMapWrapper<K, V> wrapper = new ThriftJoinWideMapWrapper<K, V>();

		wrapper.setId(id);
		wrapper.setDao(dao);
		wrapper.setExternalWideMapMeta(joinExternalWideMapMeta);
		wrapper.setInterceptor(interceptor);
		wrapper.setEntityProxifier(proxifier);
		wrapper.setCompositeHelper(thriftPropertyHelper);
		wrapper.setCompositeKeyFactory(thriftCompositeFactory);
		wrapper.setIteratorFactory(thriftIteratorFactory);
		wrapper.setKeyValueFactory(thriftKeyValueFactory);
		wrapper.setLoader(loader);
		wrapper.setPersister(persister);
		wrapper.setContext(context);
		return wrapper;
	}

}
