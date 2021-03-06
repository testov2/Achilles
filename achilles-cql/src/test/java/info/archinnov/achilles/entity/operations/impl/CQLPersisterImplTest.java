package info.archinnov.achilles.entity.operations.impl;

import static info.archinnov.achilles.type.ConsistencyLevel.*;
import static org.mockito.Mockito.*;
import info.archinnov.achilles.context.CQLPersistenceContext;
import info.archinnov.achilles.entity.metadata.EntityMeta;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import info.archinnov.achilles.entity.metadata.PropertyType;
import info.archinnov.achilles.entity.operations.CQLEntityPersister;
import info.archinnov.achilles.proxy.MethodInvoker;
import info.archinnov.achilles.type.ConsistencyLevel;
import info.archinnov.achilles.type.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;

import mapping.entity.CompleteBean;
import mapping.entity.UserBean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import testBuilders.CompleteBeanTestBuilder;
import testBuilders.PropertyMetaTestBuilder;

/**
 * CQLPersisterImplTest
 * 
 * @author DuyHai DOAN
 * 
 */

@RunWith(MockitoJUnitRunner.class)
public class CQLPersisterImplTest
{
	@InjectMocks
	private CQLPersisterImpl persisterImpl = new CQLPersisterImpl();

	@Mock
	private MethodInvoker invoker;

	@Mock
	private CQLEntityPersister entityPersister;

	@Mock
	private CQLPersistenceContext context;

	@Mock
	private CQLPersistenceContext joinContext;

	@Mock
	private EntityMeta entityMeta;

	@Mock
	private EntityMeta joinMeta;

	private List<PropertyMeta<?, ?>> allMetas = new ArrayList<PropertyMeta<?, ?>>();
	private Set<PropertyMeta<?, ?>> joinMetas = new HashSet<PropertyMeta<?, ?>>();

	private CompleteBean entity = CompleteBeanTestBuilder.builder().randomId().buid();

	@Before
	public void setUp()
	{
		when(context.getEntity()).thenReturn(entity);
		when(context.getEntityMeta()).thenReturn(entityMeta);

		when(entityMeta.getAllMetas()).thenReturn(allMetas);
		allMetas.clear();
		joinMetas.clear();
	}

	@Test
	public void should_persist() throws Exception
	{
		persisterImpl.persist(context);

		verify(context).bindForInsert();
	}

	@Test
	public void should_cascade_persist() throws Exception
	{
		PropertyMeta<?, ?> joinSimpleMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, UserBean.class)
				.field("user")
				.type(PropertyType.JOIN_SIMPLE)
				.accessors()
				.joinMeta(joinMeta)
				.cascadeType(CascadeType.ALL)
				.build();
		joinMetas.add(joinSimpleMeta);

		UserBean user = new UserBean();
		entity.setUser(user);
		when(invoker.getValueFromField(entity, joinSimpleMeta.getGetter())).thenReturn(user);

		when(context.newPersistenceContext(joinMeta, user)).thenReturn(joinContext);

		persisterImpl.cascadePersist(entityPersister, context, joinMetas);

		verify(entityPersister).persist(joinContext);
	}

	@Test
	public void should_check_for_entity_existence() throws Exception
	{
		PropertyMeta<?, ?> joinSimpleMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, UserBean.class)
				.field("user")
				.type(PropertyType.JOIN_SIMPLE)
				.accessors()
				.joinMeta(joinMeta)
				.cascadeType(CascadeType.ALL)
				.build();

		joinMetas.add(joinSimpleMeta);
		UserBean user = new UserBean();
		entity.setUser(user);
		when(invoker.getValueFromField(entity, joinSimpleMeta.getGetter())).thenReturn(user);

		when(context.newPersistenceContext(joinMeta, user)).thenReturn(joinContext);
		when(joinContext.checkForEntityExistence()).thenReturn(true);
		persisterImpl.ensureEntitiesExist(context, joinMetas);

	}

	@Test
	public void should_remove() throws Exception
	{
		when(entityMeta.getTableName()).thenReturn("table");
		when(entityMeta.getWriteConsistencyLevel()).thenReturn(EACH_QUORUM);

		persisterImpl.remove(context);

		verify(context).bindForRemoval("table", EACH_QUORUM);
	}

	@Test
	public void should_remove_linked_tables() throws Exception
	{
		PropertyMeta<?, ?> wideMapMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, UserBean.class)
				.field("user")
				.type(PropertyType.WIDE_MAP)
				.externalTable("external_table")
				.consistencyLevels(new Pair<ConsistencyLevel, ConsistencyLevel>(ALL, EACH_QUORUM))
				.build();

		allMetas.add(wideMapMeta);

		persisterImpl.removeLinkedTables(context);

		verify(context).bindForRemoval("external_table", EACH_QUORUM);
	}
}
