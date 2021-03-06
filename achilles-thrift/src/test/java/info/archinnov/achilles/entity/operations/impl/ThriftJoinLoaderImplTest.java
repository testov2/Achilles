package info.archinnov.achilles.entity.operations.impl;

import static me.prettyprint.hector.api.beans.AbstractComposite.ComponentEquality.*;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import info.archinnov.achilles.composite.ThriftCompositeFactory;
import info.archinnov.achilles.consistency.ThriftConsistencyLevelPolicy;
import info.archinnov.achilles.context.ThriftImmediateFlushContext;
import info.archinnov.achilles.context.ThriftPersistenceContext;
import info.archinnov.achilles.dao.ThriftCounterDao;
import info.archinnov.achilles.dao.ThriftGenericEntityDao;
import info.archinnov.achilles.entity.context.ThriftPersistenceContextTestBuilder;
import info.archinnov.achilles.entity.metadata.EntityMeta;
import info.archinnov.achilles.entity.metadata.JoinProperties;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import info.archinnov.achilles.entity.metadata.PropertyType;
import info.archinnov.achilles.helper.ThriftJoinEntityHelper;
import info.archinnov.achilles.type.KeyValue;
import info.archinnov.achilles.type.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mapping.entity.CompleteBean;
import mapping.entity.UserBean;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.mutation.Mutator;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import testBuilders.CompleteBeanTestBuilder;
import testBuilders.PropertyMetaTestBuilder;

import com.google.common.collect.ImmutableMap;

/**
 * ThriftJoinLoaderImplTest
 * 
 * @author DuyHai DOAN
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class ThriftJoinLoaderImplTest
{

	@InjectMocks
	private ThriftJoinLoaderImpl thriftJoinLoader;

	@Mock
	private ThriftJoinEntityHelper joinHelper;

	@Mock
	private ThriftCompositeFactory thriftCompositeFactory;

	@Mock
	private EntityMeta entityMeta;

	@Mock
	private ThriftGenericEntityDao entityDao;

	@Mock
	private ThriftCounterDao thriftCounterDao;

	@Mock
	private Mutator<Object> mutator;

	@Mock
	private ThriftConsistencyLevelPolicy policy;

	@Mock
	private Map<String, ThriftGenericEntityDao> entityDaosMap;

	@Mock
	private ThriftImmediateFlushContext thriftImmediateFlushContext;

	@Mock
	private ThriftGenericEntityDao joinEntityDao;

	private CompleteBean entity = CompleteBeanTestBuilder.builder().randomId().buid();

	private ThriftPersistenceContext context;

	@Captor
	private ArgumentCaptor<List<Long>> listCaptor;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Before
	public void setUp()
	{
		context = ThriftPersistenceContextTestBuilder
				.context(entityMeta, thriftCounterDao, policy, CompleteBean.class, entity.getId())
				.entity(entity)
				.thriftImmediateFlushContext(thriftImmediateFlushContext)
				.entityDao(entityDao)
				.entityDaosMap(entityDaosMap)
				.build();
		when(entityMeta.getTableName()).thenReturn("cf");
		when(thriftImmediateFlushContext.getEntityMutator("cf")).thenReturn(mutator);
		when(entityDaosMap.get("join_cf")).thenReturn(joinEntityDao);

	}

	@Test
	public void should_load_join_list() throws Exception
	{
		EntityMeta joinMeta = new EntityMeta();
		PropertyMeta<Void, Long> joinIdMeta = PropertyMetaTestBuilder //
				.of(UserBean.class, Void.class, Long.class)
				.field("userId")
				.accessors()
				.type(PropertyType.SIMPLE)
				.build();

		joinMeta.setIdMeta(joinIdMeta);
		joinMeta.setTableName("join_cf");
		JoinProperties joinProperties = new JoinProperties();
		joinProperties.setEntityMeta(joinMeta);

		PropertyMeta<Void, UserBean> propertyMeta = new PropertyMeta<Void, UserBean>();
		propertyMeta.setJoinProperties(joinProperties);
		propertyMeta.setValueClass(UserBean.class);

		Composite start = new Composite();
		Composite end = new Composite();

		when(thriftCompositeFactory.createBaseForQuery(propertyMeta, EQUAL)).thenReturn(start);
		when(thriftCompositeFactory.createBaseForQuery(propertyMeta, GREATER_THAN_EQUAL))
				.thenReturn(end);

		List<Pair<Composite, Object>> columns = new ArrayList<Pair<Composite, Object>>();
		columns.add(new Pair<Composite, Object>(start, "11"));
		columns.add(new Pair<Composite, Object>(end, "12"));
		when(entityDao.findColumnsRange(entity.getId(), start, end, false, Integer.MAX_VALUE))
				.thenReturn(columns);

		UserBean user1 = new UserBean();
		UserBean user2 = new UserBean();
		Map<Long, UserBean> joinEntitiesMap = ImmutableMap.of(11L, user1, 12L, user2);

		when(
				joinHelper.loadJoinEntities(eq(UserBean.class), listCaptor.capture(), eq(joinMeta),
						eq(joinEntityDao))).thenReturn(joinEntitiesMap);

		List<UserBean> actual = thriftJoinLoader.loadJoinListProperty(context, propertyMeta);

		assertThat(actual).containsExactly(user1, user2);
		assertThat(listCaptor.getValue()).containsExactly(11L, 12L);
	}

	@Test
	public void should_load_join_set() throws Exception
	{
		EntityMeta joinMeta = new EntityMeta();
		PropertyMeta<Void, Long> joinIdMeta = PropertyMetaTestBuilder //
				.of(UserBean.class, Void.class, Long.class)
				.field("userId")
				.accessors()
				.type(PropertyType.SIMPLE)
				.build();

		joinMeta.setIdMeta(joinIdMeta);
		joinMeta.setTableName("join_cf");
		JoinProperties joinProperties = new JoinProperties();
		joinProperties.setEntityMeta(joinMeta);

		PropertyMeta<Void, UserBean> propertyMeta = new PropertyMeta<Void, UserBean>();
		propertyMeta.setJoinProperties(joinProperties);
		propertyMeta.setValueClass(UserBean.class);

		Composite start = new Composite();
		Composite end = new Composite();

		when(thriftCompositeFactory.createBaseForQuery(propertyMeta, EQUAL)).thenReturn(start);
		when(thriftCompositeFactory.createBaseForQuery(propertyMeta, GREATER_THAN_EQUAL))
				.thenReturn(end);

		List<Pair<Composite, Object>> columns = new ArrayList<Pair<Composite, Object>>();
		columns.add(new Pair<Composite, Object>(start, "11"));
		columns.add(new Pair<Composite, Object>(end, "12"));
		when(entityDao.findColumnsRange(entity.getId(), start, end, false, Integer.MAX_VALUE))
				.thenReturn(columns);

		UserBean user1 = new UserBean();
		UserBean user2 = new UserBean();
		Map<Long, UserBean> joinEntitiesMap = ImmutableMap.of(11L, user1, 12L, user2);

		when(
				joinHelper.loadJoinEntities(eq(UserBean.class), listCaptor.capture(), eq(joinMeta),
						eq(joinEntityDao))).thenReturn(joinEntitiesMap);

		Set<UserBean> actual = thriftJoinLoader.loadJoinSetProperty(context, propertyMeta);

		assertThat(actual).contains(user1, user2);
		assertThat(listCaptor.getValue()).containsExactly(11L, 12L);
	}

	@Test
	public void should_load_join_map() throws Exception
	{
		EntityMeta joinMeta = new EntityMeta();
		PropertyMeta<Void, Long> joinIdMeta = PropertyMetaTestBuilder //
				.of(UserBean.class, Void.class, Long.class)
				.field("userId")
				.accessors()
				.type(PropertyType.SIMPLE)
				.build();

		joinMeta.setIdMeta(joinIdMeta);
		joinMeta.setTableName("join_cf");
		JoinProperties joinProperties = new JoinProperties();
		joinProperties.setEntityMeta(joinMeta);

		PropertyMeta<Integer, UserBean> propertyMeta = new PropertyMeta<Integer, UserBean>();
		propertyMeta.setJoinProperties(joinProperties);
		propertyMeta.setKeyClass(Integer.class);
		propertyMeta.setValueClass(UserBean.class);
		propertyMeta.setObjectMapper(objectMapper);

		Composite start = new Composite();
		Composite end = new Composite();

		when(thriftCompositeFactory.createBaseForQuery(propertyMeta, EQUAL)).thenReturn(start);
		when(thriftCompositeFactory.createBaseForQuery(propertyMeta, GREATER_THAN_EQUAL))
				.thenReturn(end);

		List<Pair<Composite, Object>> columns = new ArrayList<Pair<Composite, Object>>();
		columns.add(new Pair<Composite, Object>(start, writeString(new KeyValue<Integer, String>(
				11, "11"))));
		columns.add(new Pair<Composite, Object>(end, writeString(new KeyValue<Integer, String>(12,
				"12"))));
		when(entityDao.findColumnsRange(entity.getId(), start, end, false, Integer.MAX_VALUE))
				.thenReturn(columns);

		UserBean user1 = new UserBean();
		UserBean user2 = new UserBean();
		Map<Long, UserBean> joinEntitiesMap = ImmutableMap.of(11L, user1, 12L, user2);
		when(
				joinHelper.loadJoinEntities(eq(UserBean.class), listCaptor.capture(), eq(joinMeta),
						eq(joinEntityDao))).thenReturn(joinEntitiesMap);

		Map<Integer, UserBean> actual = thriftJoinLoader.loadJoinMapProperty(context, propertyMeta);

		assertThat(actual.get(11)).isSameAs(user1);
		assertThat(actual.get(12)).isSameAs(user2);
		assertThat(listCaptor.getValue()).containsExactly(11L, 12L);
	}

	private String writeString(Object value) throws Exception
	{
		return objectMapper.writerWithType(KeyValue.class).writeValueAsString(value);
	}
}
