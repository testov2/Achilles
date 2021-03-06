package info.archinnov.achilles.context;

import static info.archinnov.achilles.serializer.ThriftSerializerUtils.COMPOSITE_SRZ;
import static org.fest.assertions.api.Assertions.assertThat;
import info.archinnov.achilles.consistency.ThriftConsistencyLevelPolicy;
import info.archinnov.achilles.dao.ThriftCounterDao;
import info.archinnov.achilles.dao.ThriftGenericEntityDao;
import info.archinnov.achilles.dao.ThriftGenericWideRowDao;
import info.archinnov.achilles.entity.metadata.EntityMeta;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import info.archinnov.achilles.entity.metadata.PropertyType;
import info.archinnov.achilles.type.Counter;
import info.archinnov.achilles.type.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mapping.entity.CompleteBean;
import mapping.entity.UserBean;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import testBuilders.PropertyMetaTestBuilder;

/**
 * ThriftDaoContextBuilderTest
 * 
 * @author DuyHai DOAN
 * 
 */

@RunWith(MockitoJUnitRunner.class)
public class ThriftDaoContextBuilderTest
{

	@InjectMocks
	private ThriftDaoContextBuilder builder;

	@Mock
	private Cluster cluster;

	@Mock
	private Keyspace keyspace;

	@Mock
	private ThriftConsistencyLevelPolicy consistencyPolicy;

	private ConfigurationContext configContext = new ConfigurationContext();

	private Map<Class<?>, EntityMeta> entityMetaMap = new HashMap<Class<?>, EntityMeta>();

	@Before
	public void setUp()
	{
		configContext.setConsistencyPolicy(consistencyPolicy);
		entityMetaMap.clear();
	}

	@Test
	public void should_build_counter_dao() throws Exception
	{
		ThriftDaoContext context = builder.buildDao(cluster, keyspace, entityMetaMap,
				configContext, true);

		ThriftCounterDao thriftCounterDao = context.getCounterDao();
		assertThat(thriftCounterDao).isNotNull();
		assertThat(Whitebox.getInternalState(thriftCounterDao, "policy")).isSameAs(
				consistencyPolicy);
		assertThat(Whitebox.getInternalState(thriftCounterDao, "cluster")).isSameAs(cluster);
		assertThat(Whitebox.getInternalState(thriftCounterDao, "keyspace")).isSameAs(keyspace);
		assertThat(Whitebox.getInternalState(thriftCounterDao, "columnNameSerializer")).isSameAs(
				COMPOSITE_SRZ);
		Pair<Class<Composite>, Class<Long>> rowAndValueClases = Whitebox.getInternalState(
				thriftCounterDao, "rowkeyAndValueClasses");
		assertThat(rowAndValueClases.left).isSameAs(Composite.class);
		assertThat(rowAndValueClases.right).isSameAs(Long.class);
	}

	@Test
	public void should_build_entity_dao() throws Exception
	{
		PropertyMeta<Void, Long> idMeta = PropertyMetaTestBuilder //
				.completeBean(Void.class, Long.class)
				.field("id")
				.build();

		EntityMeta entityMeta = new EntityMeta();
		entityMeta.setWideRow(false);
		entityMeta.setTableName("cf");
		entityMeta.setIdMeta(idMeta);
		entityMeta.setIdClass(Long.class);
		entityMeta.setPropertyMetas(new HashMap<String, PropertyMeta<?, ?>>());

		entityMetaMap.put(CompleteBean.class, entityMeta);

		ThriftDaoContext context = builder.buildDao(cluster, keyspace, entityMetaMap,
				configContext, false);

		ThriftGenericEntityDao entityDao = context.findEntityDao("cf");

		assertThat(entityDao).isNotNull();
		assertThat(entityDao.getColumnFamily()).isEqualTo("cf");
		assertThat(Whitebox.getInternalState(entityDao, "policy")).isSameAs(consistencyPolicy);
		assertThat(Whitebox.getInternalState(entityDao, "cluster")).isSameAs(cluster);
		assertThat(Whitebox.getInternalState(entityDao, "keyspace")).isSameAs(keyspace);
		assertThat(Whitebox.getInternalState(entityDao, "columnNameSerializer")).isSameAs(
				COMPOSITE_SRZ);

		Pair<Class<Long>, Class<String>> rowAndValueClases = Whitebox.getInternalState(entityDao,
				"rowkeyAndValueClasses");
		assertThat(rowAndValueClases.left).isSameAs(Long.class);
		assertThat(rowAndValueClases.right).isSameAs(String.class);
	}

	@Test
	public void should_build_wide_row_dao() throws Exception
	{
		PropertyMeta<UUID, String> geoPositionsMeta = PropertyMetaTestBuilder //
				.completeBean(UUID.class, String.class)
				.field("id")
				.externalTable("externalCf")
				.type(PropertyType.WIDE_MAP)
				.build();

		HashMap<String, PropertyMeta<?, ?>> propertyMetas = new HashMap<String, PropertyMeta<?, ?>>();
		propertyMetas.put("geoPositions", geoPositionsMeta);

		EntityMeta entityMeta = new EntityMeta();
		entityMeta.setWideRow(true);
		entityMeta.setTableName("cf");
		entityMeta.setIdClass(Long.class);
		entityMeta.setPropertyMetas(propertyMetas);

		entityMetaMap.put(CompleteBean.class, entityMeta);

		ThriftDaoContext context = builder.buildDao(cluster, keyspace, entityMetaMap,
				configContext, false);

		ThriftGenericWideRowDao columnFamilyDao = context.findWideRowDao("externalCf");

		assertThat(columnFamilyDao).isNotNull();
		assertThat(columnFamilyDao.getColumnFamily()).isEqualTo("externalCf");
		assertThat(Whitebox.getInternalState(columnFamilyDao, "policy"))
				.isSameAs(consistencyPolicy);
		assertThat(Whitebox.getInternalState(columnFamilyDao, "cluster")).isSameAs(cluster);
		assertThat(Whitebox.getInternalState(columnFamilyDao, "keyspace")).isSameAs(keyspace);
		assertThat(Whitebox.getInternalState(columnFamilyDao, "columnNameSerializer")).isSameAs(
				COMPOSITE_SRZ);

		Pair<Class<Long>, Class<String>> rowAndValueClases = Whitebox.getInternalState(
				columnFamilyDao, "rowkeyAndValueClasses");
		assertThat(rowAndValueClases.left).isSameAs(Long.class);
		assertThat(rowAndValueClases.right).isSameAs(String.class);
	}

	@Test
	public void should_build_wide_row_dao_with_object_value_type() throws Exception
	{
		PropertyMeta<UUID, UserBean> geoPositionsMeta = PropertyMetaTestBuilder //
				.completeBean(UUID.class, UserBean.class)
				.field("friendsWideMap")
				.externalTable("externalCf")
				.type(PropertyType.WIDE_MAP)
				.build();

		HashMap<String, PropertyMeta<?, ?>> propertyMetas = new HashMap<String, PropertyMeta<?, ?>>();
		propertyMetas.put("friendsWideMap", geoPositionsMeta);

		EntityMeta entityMeta = new EntityMeta();
		entityMeta.setWideRow(true);
		entityMeta.setTableName("cf");
		entityMeta.setIdClass(Long.class);
		entityMeta.setPropertyMetas(propertyMetas);

		entityMetaMap.put(CompleteBean.class, entityMeta);

		ThriftDaoContext context = builder.buildDao(cluster, keyspace, entityMetaMap,
				configContext, false);

		ThriftGenericWideRowDao columnFamilyDao = context.findWideRowDao("externalCf");

		assertThat(columnFamilyDao).isNotNull();
		assertThat(columnFamilyDao.getColumnFamily()).isEqualTo("externalCf");
		assertThat(Whitebox.getInternalState(columnFamilyDao, "policy"))
				.isSameAs(consistencyPolicy);
		assertThat(Whitebox.getInternalState(columnFamilyDao, "cluster")).isSameAs(cluster);
		assertThat(Whitebox.getInternalState(columnFamilyDao, "keyspace")).isSameAs(keyspace);
		assertThat(Whitebox.getInternalState(columnFamilyDao, "columnNameSerializer")).isSameAs(
				COMPOSITE_SRZ);

		Pair<Class<Long>, Class<String>> rowAndValueClases = Whitebox.getInternalState(
				columnFamilyDao, "rowkeyAndValueClasses");
		assertThat(rowAndValueClases.left).isSameAs(Long.class);
		assertThat(rowAndValueClases.right).isSameAs(String.class);
	}

	@Test
	public void should_build_wide_row_dao_with_counter_type() throws Exception
	{
		PropertyMeta<String, Counter> geoPositionsMeta = PropertyMetaTestBuilder //
				.completeBean(String.class, Counter.class)
				.field("popularTopics")
				.externalTable("externalCf")
				.type(PropertyType.COUNTER_WIDE_MAP)
				.build();

		HashMap<String, PropertyMeta<?, ?>> propertyMetas = new HashMap<String, PropertyMeta<?, ?>>();
		propertyMetas.put("popularTopics", geoPositionsMeta);

		EntityMeta entityMeta = new EntityMeta();
		entityMeta.setWideRow(true);
		entityMeta.setTableName("cf");
		entityMeta.setIdClass(Long.class);
		entityMeta.setPropertyMetas(propertyMetas);

		entityMetaMap.put(CompleteBean.class, entityMeta);

		ThriftDaoContext context = builder.buildDao(cluster, keyspace, entityMetaMap,
				configContext, false);

		ThriftGenericWideRowDao columnFamilyDao = context.findWideRowDao("externalCf");

		assertThat(columnFamilyDao).isNotNull();
		assertThat(columnFamilyDao.getColumnFamily()).isEqualTo("externalCf");
		assertThat(Whitebox.getInternalState(columnFamilyDao, "policy"))
				.isSameAs(consistencyPolicy);
		assertThat(Whitebox.getInternalState(columnFamilyDao, "cluster")).isSameAs(cluster);
		assertThat(Whitebox.getInternalState(columnFamilyDao, "keyspace")).isSameAs(keyspace);
		assertThat(Whitebox.getInternalState(columnFamilyDao, "columnNameSerializer")).isSameAs(
				COMPOSITE_SRZ);

		Pair<Class<Long>, Class<Long>> rowAndValueClases = Whitebox.getInternalState(
				columnFamilyDao, "rowkeyAndValueClasses");
		assertThat(rowAndValueClases.left).isSameAs(Long.class);
		assertThat(rowAndValueClases.right).isSameAs(Long.class);
	}

	@Test
	public void should_build_wide_row_dao_for_join_entity() throws Exception
	{
		EntityMeta joinMeta = new EntityMeta();
		joinMeta.setIdClass(Long.class);

		PropertyMeta<Long, UserBean> joinUsersMeta = PropertyMetaTestBuilder //
				.completeBean(Long.class, UserBean.class)
				.field("joinUsers")
				.externalTable("externalCf")
				.joinMeta(joinMeta)
				.type(PropertyType.JOIN_WIDE_MAP)
				.idClass(Long.class)
				.build();

		PropertyMeta<Void, Long> idMeta = PropertyMetaTestBuilder //
				.completeBean(Void.class, Long.class)
				.field("id")
				.build();

		HashMap<String, PropertyMeta<?, ?>> propertyMetas = new HashMap<String, PropertyMeta<?, ?>>();
		propertyMetas.put("joinUsers", joinUsersMeta);

		EntityMeta entityMeta = new EntityMeta();
		entityMeta.setWideRow(true);
		entityMeta.setTableName("cf");
		entityMeta.setIdMeta(idMeta);
		entityMeta.setPropertyMetas(propertyMetas);

		entityMetaMap.put(CompleteBean.class, entityMeta);

		ThriftDaoContext context = builder.buildDao(cluster, keyspace, entityMetaMap,
				configContext, false);

		ThriftGenericWideRowDao columnFamilyDao = context.findWideRowDao("externalCf");

		assertThat(columnFamilyDao).isNotNull();
		assertThat(columnFamilyDao.getColumnFamily()).isEqualTo("externalCf");
		assertThat(Whitebox.getInternalState(columnFamilyDao, "policy"))
				.isSameAs(consistencyPolicy);
		assertThat(Whitebox.getInternalState(columnFamilyDao, "cluster")).isSameAs(cluster);
		assertThat(Whitebox.getInternalState(columnFamilyDao, "keyspace")).isSameAs(keyspace);
		assertThat(Whitebox.getInternalState(columnFamilyDao, "columnNameSerializer")).isSameAs(
				COMPOSITE_SRZ);

		Pair<Class<Long>, Class<Long>> rowAndValueClases = Whitebox.getInternalState(
				columnFamilyDao, "rowkeyAndValueClasses");
		assertThat(rowAndValueClases.left).isSameAs(Long.class);
		assertThat(rowAndValueClases.right).isSameAs(Long.class);
	}
}
