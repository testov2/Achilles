package info.archinnov.achilles.helper;

import info.archinnov.achilles.dao.ThriftGenericEntityDao;
import info.archinnov.achilles.entity.ThriftEntityMapper;
import info.archinnov.achilles.entity.metadata.EntityMeta;
import info.archinnov.achilles.exception.AchillesException;
import info.archinnov.achilles.proxy.MethodInvoker;
import info.archinnov.achilles.type.Pair;
import info.archinnov.achilles.validation.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.prettyprint.hector.api.beans.Composite;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ThriftJoinEntityHelper
 * 
 * @author DuyHai DOAN
 * 
 */
public class ThriftJoinEntityHelper
{
	private static final Logger log = LoggerFactory.getLogger(ThriftJoinEntityHelper.class);

	private ThriftEntityMapper mapper = new ThriftEntityMapper();
	private MethodInvoker invoker = new MethodInvoker();

	public <T, ID> Map<ID, T> loadJoinEntities(Class<T> entityClass, List<ID> keys,
			EntityMeta entityMeta, ThriftGenericEntityDao joinEntityDao)
	{
		if (log.isTraceEnabled())
		{
			log.trace("Load join entities of class {} with primary keys {}",
					entityClass.getCanonicalName(), StringUtils.join(keys, ","));
		}

		Validator.validateNotNull(entityClass, "Entity class should not be null");
		Validator.validateNotEmpty(keys, "List of join primary keys '" + keys
				+ "' should not be empty");
		Validator.validateNotNull(entityMeta, "Entity meta for '" + entityClass.getCanonicalName()
				+ "' should not be null");

		Map<ID, T> entitiesByKey = new HashMap<ID, T>();
		Map<ID, List<Pair<Composite, String>>> rows = joinEntityDao.eagerFetchEntities(keys);

		for (Entry<ID, List<Pair<Composite, String>>> entry : rows.entrySet())
		{
			T entity;
			try
			{
				entity = entityClass.newInstance();

				ID key = entry.getKey();
				List<Pair<Composite, String>> columns = entry.getValue();
				if (columns.size() > 0)
				{
					mapper.setEagerPropertiesToEntity(key, columns, entityMeta, entity);
					invoker.setValueToField(entity, entityMeta.getIdMeta().getSetter(), key);
					entitiesByKey.put(key, entity);
				}
			}
			catch (Exception e)
			{
				throw new AchillesException("Error when instantiating class '"
						+ entityClass.getCanonicalName() + "' ", e);
			}
		}
		return entitiesByKey;
	}
}
