package info.archinnov.achilles.wrapper;

import info.archinnov.achilles.entity.context.PersistenceContext;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import info.archinnov.achilles.entity.operations.EntityProxifier;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * AbstractWrapper
 * 
 * @author DuyHai DOAN
 * 
 */
public abstract class AbstractWrapper<ID, K, V>
{
	protected Map<Method, PropertyMeta<?, ?>> dirtyMap;
	protected Method setter;
	protected PropertyMeta<K, V> propertyMeta;
	protected EntityProxifier proxifier;
	protected PersistenceContext<ID> context;

	public Map<Method, PropertyMeta<?, ?>> getDirtyMap()
	{
		return dirtyMap;
	}

	public void setDirtyMap(Map<Method, PropertyMeta<?, ?>> dirtyMap)
	{
		this.dirtyMap = dirtyMap;
	}

	public void setSetter(Method setter)
	{
		this.setter = setter;
	}

	public void setPropertyMeta(PropertyMeta<K, V> propertyMeta)
	{
		this.propertyMeta = propertyMeta;
	}

	protected void markDirty()
	{
		if (!dirtyMap.containsKey(this.setter))
		{
			dirtyMap.put(this.setter, this.propertyMeta);
		}
	}

	public void setProxifier(EntityProxifier proxifier)
	{
		this.proxifier = proxifier;
	}

	protected boolean isJoin()
	{
		return this.propertyMeta.type().isJoinColumn();
	}

	public void setContext(PersistenceContext<ID> context)
	{
		this.context = context;
	}

	protected PersistenceContext<?> joinContext(V joinEntity)
	{
		return context.newPersistenceContext(propertyMeta.joinMeta(), joinEntity);
	}
}