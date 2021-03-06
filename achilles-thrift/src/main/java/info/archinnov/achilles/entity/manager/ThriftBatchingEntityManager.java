package info.archinnov.achilles.entity.manager;

import info.archinnov.achilles.context.ConfigurationContext;
import info.archinnov.achilles.context.ThriftBatchingFlushContext;
import info.archinnov.achilles.context.ThriftDaoContext;
import info.archinnov.achilles.context.ThriftPersistenceContext;
import info.archinnov.achilles.context.execution.SafeExecutionContext;
import info.archinnov.achilles.entity.metadata.EntityMeta;
import info.archinnov.achilles.exception.AchillesException;
import info.archinnov.achilles.type.ConsistencyLevel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * ThriftBatchingEntityManager
 * 
 * @author DuyHai DOAN
 * 
 */
public class ThriftBatchingEntityManager extends ThriftEntityManager
{
	private static final Logger log = LoggerFactory.getLogger(ThriftBatchingEntityManager.class);

	private ThriftBatchingFlushContext flushContext;

	ThriftBatchingEntityManager(AchillesEntityManagerFactory entityManagerFactory,
			Map<Class<?>, EntityMeta> entityMetaMap, ThriftDaoContext thriftDaoContext,
			ConfigurationContext configContext)
	{
		super(entityManagerFactory, entityMetaMap, thriftDaoContext, configContext);
		this.flushContext = new ThriftBatchingFlushContext(thriftDaoContext, consistencyPolicy,
				Optional.<ConsistencyLevel> absent(), Optional.<ConsistencyLevel> absent(),
				Optional.<Integer> absent());
	}

	/**
	 * Start a batch session using a Hector mutator.
	 */
	public void startBatch()
	{
		log.debug("Starting batch mode");
		flushContext.startBatch();
	}

	/**
	 * Start a batch session with read/write consistency levels using a Hector mutator.
	 */
	public void startBatch(ConsistencyLevel readLevel, ConsistencyLevel writeLevel)
	{
		log.debug("Starting batch mode with write consistency level {}", writeLevel.name());
		startBatch();
		flushContext.setReadConsistencyLevel(Optional.fromNullable(readLevel));
		flushContext.setWriteConsistencyLevel(Optional.fromNullable(writeLevel));

	}

	/**
	 * End an existing batch and flush all the mutators.
	 * 
	 * All join entities will be flushed through their own mutator.
	 * 
	 * Do nothing if no batch mutator was started
	 * 
	 */
	public void endBatch()
	{
		log.debug("Ending batch mode");
		flushContext.endBatch();
	}

	/**
	 * Cleaning all pending mutations for the current batch session.
	 */
	public void cleanBatch()
	{
		log.debug("Cleaning all pending mutations");
		flushContext.cleanUp();
	}

	@Override
	public void persist(final Object entity, ConsistencyLevel writeLevel)
	{
		flushContext.cleanUp();
		throw new AchillesException(
				"Runtime custom Consistency Level cannot be set for batch mode. Please set the Consistency Levels at batch start with 'startBatch(readLevel,writeLevel)'");
	}

	@Override
	public void persist(final Object entity, int ttl, ConsistencyLevel writeLevel)
	{
		flushContext.cleanUp();
		throw new AchillesException(
				"Runtime custom Consistency Level cannot be set for batch mode. Please set the Consistency Levels at batch start with 'startBatch(readLevel,writeLevel)'");
	}

	@Override
	public <T> T merge(final T entity, ConsistencyLevel writeLevel)
	{
		flushContext.cleanUp();
		throw new AchillesException(
				"Runtime custom Consistency Level cannot be set for batch mode. Please set the Consistency Levels at batch start with 'startBatch(readLevel,writeLevel)'");
	}

	@Override
	public <T> T merge(final T entity, int ttl, ConsistencyLevel writeLevel)
	{
		flushContext.cleanUp();
		throw new AchillesException(
				"Runtime custom Consistency Level cannot be set for batch mode. Please set the Consistency Levels at batch start with 'startBatch(readLevel,writeLevel)'");
	}

	@Override
	public void remove(final Object entity, ConsistencyLevel writeLevel)
	{
		flushContext.cleanUp();
		throw new AchillesException(
				"Runtime custom Consistency Level cannot be set for batch mode. Please set the Consistency Levels at batch start with 'startBatch(readLevel,writeLevel)'");
	}

	@Override
	public <T> T find(final Class<T> entityClass, final Object primaryKey,
			ConsistencyLevel readLevel)
	{
		flushContext.cleanUp();
		throw new AchillesException(
				"Runtime custom Consistency Level cannot be set for batch mode. Please set the Consistency Levels at batch start with 'startBatch(readLevel,writeLevel)'");
	}

	@Override
	public <T> T getReference(final Class<T> entityClass, final Object primaryKey,
			ConsistencyLevel readLevel)
	{
		flushContext.cleanUp();
		throw new AchillesException(
				"Runtime custom Consistency Level cannot be set for batch mode. Please set the Consistency Levels at batch start with 'startBatch(readLevel,writeLevel)'");
	}

	@Override
	public <T> void initialize(final T entity)
	{
		reinitConsistencyLevelsOnError(new SafeExecutionContext<Void>()
		{
			@Override
			public Void execute()
			{
				ThriftBatchingEntityManager.super.initialize(entity);
				return null;
			}
		});
	}

	@Override
	public <T> void initialize(final Collection<T> entities)
	{
		reinitConsistencyLevelsOnError(new SafeExecutionContext<Void>()
		{
			@Override
			public Void execute()
			{
				ThriftBatchingEntityManager.super.initialize(entities);
				return null;
			}
		});
	}

	@Override
	public void refresh(final Object entity, ConsistencyLevel readLevel)
	{
		throw new AchillesException(
				"Runtime custom Consistency Level cannot be set for batch mode. Please set the Consistency Levels at batch start with 'startBatch(readLevel,writeLevel)'");
	}

	@Override
	protected ThriftPersistenceContext initPersistenceContext(Class<?> entityClass,
			Object primaryKey, Optional<ConsistencyLevel> readLevelO,
			Optional<ConsistencyLevel> writeLevelO, Optional<Integer> ttlO)
	{
		log.trace("Initializing new persistence context for entity class {} and primary key {}",
				entityClass.getCanonicalName(), primaryKey);

		EntityMeta entityMeta = entityMetaMap.get(entityClass);
		return new ThriftPersistenceContext(entityMeta, configContext, thriftDaoContext,
				flushContext, entityClass, primaryKey, new HashSet<String>());
	}

	@Override
	protected ThriftPersistenceContext initPersistenceContext(Object entity,
			Optional<ConsistencyLevel> readLevelO, Optional<ConsistencyLevel> writeLevelO,
			Optional<Integer> ttlO)
	{
		log.trace("Initializing new persistence context for entity {}", entity);

		EntityMeta entityMeta = this.entityMetaMap.get(proxifier.deriveBaseClass(entity));
		return new ThriftPersistenceContext(entityMeta, configContext, thriftDaoContext,
				flushContext, entity, new HashSet<String>());
	}

	private <T> T reinitConsistencyLevelsOnError(SafeExecutionContext<T> context)
	{
		try
		{
			return context.execute();
		}
		catch (Exception e)
		{
			this.flushContext.cleanUp();
			throw new AchillesException(e);
		}
	}
}
