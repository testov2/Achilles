package info.archinnov.achilles.proxy;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import info.archinnov.achilles.context.AchillesPersistenceContext;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import info.archinnov.achilles.entity.metadata.PropertyType;
import info.archinnov.achilles.entity.operations.EntityLoader;
import info.archinnov.achilles.entity.operations.EntityPersister;
import info.archinnov.achilles.entity.operations.EntityProxifier;
import info.archinnov.achilles.proxy.wrapper.ListWrapper;
import info.archinnov.achilles.proxy.wrapper.MapWrapper;
import info.archinnov.achilles.proxy.wrapper.SetWrapper;
import info.archinnov.achilles.type.Counter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import mapping.entity.CompleteBean;
import mapping.entity.UserBean;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import testBuilders.CompleteBeanTestBuilder;
import testBuilders.PropertyMetaTestBuilder;

/**
 * AchillesEntityInterceptorTest
 * 
 * @author DuyHai DOAN
 * 
 */

@RunWith(MockitoJUnitRunner.class)
public class EntityInterceptorTest
{
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock
	private EntityInterceptor<AchillesPersistenceContext, CompleteBean> interceptor;

	@Mock
	private EntityLoader<AchillesPersistenceContext> loader;

	@Mock
	private EntityPersister<AchillesPersistenceContext> persister;

	@Mock
	private EntityProxifier<AchillesPersistenceContext> proxifier;

	@Mock
	private MethodProxy proxy;

	@Mock
	private AchillesPersistenceContext context;

	@Mock
	private AchillesPersistenceContext joinContext;

	private Object[] args = new Object[] {};

	private Map<Method, PropertyMeta<?, ?>> getterMetas = new HashMap<Method, PropertyMeta<?, ?>>();
	private Map<Method, PropertyMeta<?, ?>> setterMetas = new HashMap<Method, PropertyMeta<?, ?>>();
	private Set<Method> alreadyLoaded = new HashSet<Method>();
	private Map<Method, PropertyMeta<?, ?>> dirtyMap = new HashMap<Method, PropertyMeta<?, ?>>();
	private CompleteBean bean;
	private Long key = RandomUtils.nextLong();
	private Object rawValue = "raw";

	@Before
	public void setUp() throws Throwable
	{
		doCallRealMethod().when(interceptor).setTarget(any(CompleteBean.class));
		doCallRealMethod().when(interceptor).intercept(any(), any(Method.class),
				any(Object[].class), any(MethodProxy.class));
		doCallRealMethod().when(interceptor).setGetterMetas(getterMetas);
		doCallRealMethod().when(interceptor).setSetterMetas(setterMetas);

		getterMetas.clear();
		setterMetas.clear();

		interceptor.setGetterMetas(getterMetas);
		interceptor.setSetterMetas(setterMetas);

		doCallRealMethod().when(interceptor).setLoader(loader);
		interceptor.setLoader(loader);

		bean = CompleteBeanTestBuilder.builder().id(key).buid();
		interceptor.setTarget(bean);

		doCallRealMethod().when(interceptor).setKey(key);
		interceptor.setKey(key);

		doCallRealMethod().when(interceptor).setContext(context);
		interceptor.setContext(context);

		alreadyLoaded.clear();
		doCallRealMethod().when(interceptor).setAlreadyLoaded(alreadyLoaded);
		interceptor.setAlreadyLoaded(alreadyLoaded);

		dirtyMap.clear();
		doCallRealMethod().when(interceptor).setDirtyMap(dirtyMap);
		interceptor.setDirtyMap(dirtyMap);

		doCallRealMethod().when(interceptor).setPersister(persister);
		interceptor.setPersister(persister);

		doCallRealMethod().when(interceptor).setProxifier(proxifier);
		interceptor.setProxifier(proxifier);
	}

	@Test
	public void should_return_key_when_invoking_id_getter() throws Throwable
	{
		PropertyMeta<Void, Long> idMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, Long.class)
				.field("id")
				.accessors()
				.build();

		doCallRealMethod().when(interceptor).setIdGetter(idMeta.getGetter());
		interceptor.setIdGetter(idMeta.getGetter());

		Object id = interceptor.intercept(bean, idMeta.getGetter(), args, null);

		assertThat(id).isEqualTo(key);
	}

	@Test
	public void should_exception_when_setting_id() throws Throwable
	{
		PropertyMeta<Void, Long> idMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, Long.class)
				.field("id")
				.accessors()
				.build();

		exception.expect(IllegalAccessException.class);
		exception.expectMessage("Cannot change id value for existing entity ");

		doCallRealMethod().when(interceptor).setIdSetter(idMeta.getSetter());
		interceptor.setIdSetter(idMeta.getSetter());

		interceptor.intercept(null, idMeta.getSetter(), args, null);
	}

	@Test
	public void should_load_lazy_property_and_return_it() throws Throwable
	{

		PropertyMeta<Void, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class)
				.field("name")
				.accessors()
				.type(PropertyType.LAZY_SIMPLE)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		when(proxy.invoke(bean, args)).thenReturn(rawValue);
		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isEqualTo(rawValue);
		verify(loader).loadPropertyIntoObject(bean, key, context, propertyMeta);
	}

	@Test
	public void should_return_lazy_property_already_loaded() throws Throwable
	{

		PropertyMeta<Void, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class)
				.field("name")
				.accessors()
				.type(PropertyType.LAZY_SIMPLE)
				.build();

		alreadyLoaded.add(propertyMeta.getGetter());
		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		when(proxy.invoke(bean, args)).thenReturn(rawValue);
		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isEqualTo(rawValue);
		verifyZeroInteractions(loader);
	}

	@Test
	public void should_return_simple_property() throws Throwable
	{
		PropertyMeta<Void, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class)
				.field("name")
				.accessors()
				.type(PropertyType.SIMPLE)
				.build();

		alreadyLoaded.add(propertyMeta.getGetter());
		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		when(proxy.invoke(bean, args)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isEqualTo(rawValue);
		verifyZeroInteractions(loader);
	}

	@Test
	public void should_build_counter_wrapper() throws Throwable
	{
		PropertyMeta<Void, Counter> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, Counter.class)
				.field("count")
				.accessors()
				.type(PropertyType.COUNTER)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		when(interceptor.buildCounterWrapper(propertyMeta)).thenReturn(rawValue);
		when(proxy.invoke(bean, args)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isSameAs(rawValue);
	}

	@Test
	public void should_return_join_simple() throws Throwable
	{
		PropertyMeta<Void, UserBean> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, UserBean.class)
				.field("user")
				.accessors()
				.type(PropertyType.JOIN_SIMPLE)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		when(proxy.invoke(bean, args)).thenReturn(rawValue);
		when(context.newPersistenceContext(null, rawValue)).thenReturn(joinContext);
		when(proxifier.buildProxy(rawValue, joinContext)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isSameAs(rawValue);
	}

	@Test
	public void should_return_null_for_join_simple() throws Throwable
	{
		PropertyMeta<Void, UserBean> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, UserBean.class)
				.field("user")
				.accessors()
				.type(PropertyType.JOIN_SIMPLE)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		when(proxy.invoke(bean, args)).thenReturn(null);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isNull();
	}

	@Test
	public void should_return_list_wrapper() throws Throwable
	{
		PropertyMeta<Void, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class)
				.field("friends")
				.accessors()
				.type(PropertyType.LIST)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		rawValue = new ArrayList<String>();
		when(proxy.invoke(bean, args)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isInstanceOf(ListWrapper.class);
	}

	@Test
	public void should_return_lazy_list_wrapper() throws Throwable
	{
		PropertyMeta<Void, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class)
				.field("friends")
				.accessors()
				.type(PropertyType.LAZY_LIST)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		rawValue = new ArrayList<String>();
		when(proxy.invoke(bean, args)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isInstanceOf(ListWrapper.class);
	}

	@Test
	public void should_return_join_list_wrapper() throws Throwable
	{
		PropertyMeta<Void, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class)
				.field("friends")
				.accessors()
				.type(PropertyType.JOIN_LIST)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		rawValue = new ArrayList<String>();
		when(proxy.invoke(bean, args)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isInstanceOf(ListWrapper.class);
	}

	@Test
	public void should_return_null_for_list_property() throws Throwable
	{
		PropertyMeta<Void, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class)
				.field("friends")
				.accessors()
				.type(PropertyType.LIST)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		when(proxy.invoke(bean, args)).thenReturn(null);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isNull();
	}

	@Test
	public void should_return_set_wrapper() throws Throwable
	{
		PropertyMeta<Void, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class)
				.field("followers")
				.accessors()
				.type(PropertyType.SET)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		rawValue = new HashSet<String>();
		when(proxy.invoke(bean, args)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isInstanceOf(SetWrapper.class);
	}

	@Test
	public void should_return_lazy_set_wrapper() throws Throwable
	{
		PropertyMeta<Void, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class)
				.field("followers")
				.accessors()
				.type(PropertyType.LAZY_SET)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		rawValue = new HashSet<String>();
		when(proxy.invoke(bean, args)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isInstanceOf(SetWrapper.class);
	}

	@Test
	public void should_return_join_set_wrapper() throws Throwable
	{
		PropertyMeta<Void, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class)
				.field("followers")
				.accessors()
				.type(PropertyType.JOIN_SET)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		rawValue = new HashSet<String>();
		when(proxy.invoke(bean, args)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isInstanceOf(SetWrapper.class);
	}

	@Test
	public void should_return_null_for_set_property() throws Throwable
	{
		PropertyMeta<Void, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class)
				.field("followers")
				.accessors()
				.type(PropertyType.SET)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		when(proxy.invoke(bean, args)).thenReturn(null);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isNull();
	}

	@Test
	public void should_return_map_wrapper() throws Throwable
	{
		PropertyMeta<Integer, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Integer.class, String.class)
				.field("preferences")
				.accessors()
				.type(PropertyType.MAP)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		rawValue = new HashMap<Integer, String>();
		when(proxy.invoke(bean, args)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isInstanceOf(MapWrapper.class);
	}

	@Test
	public void should_return_lazy_map_wrapper() throws Throwable
	{
		PropertyMeta<Integer, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Integer.class, String.class)
				.field("preferences")
				.accessors()
				.type(PropertyType.LAZY_MAP)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		rawValue = new HashMap<Integer, String>();
		when(proxy.invoke(bean, args)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isInstanceOf(MapWrapper.class);
	}

	@Test
	public void should_return_join_map_wrapper() throws Throwable
	{
		PropertyMeta<Integer, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Integer.class, String.class)
				.field("preferences")
				.accessors()
				.type(PropertyType.JOIN_MAP)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		rawValue = new HashMap<Integer, String>();
		when(proxy.invoke(bean, args)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isInstanceOf(MapWrapper.class);
	}

	@Test
	public void should_return_null_for_map_property() throws Throwable
	{
		PropertyMeta<Integer, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Integer.class, String.class)
				.field("preferences")
				.accessors()
				.type(PropertyType.MAP)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		when(proxy.invoke(bean, args)).thenReturn(null);

		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isNull();
	}

	@Test
	public void should_return_wide_row_wrapper() throws Throwable
	{
		PropertyMeta<UUID, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(UUID.class, String.class)
				.field("tweets")
				.accessors()
				.type(PropertyType.WIDE_MAP)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		when(context.isWideRow()).thenReturn(true);
		when(interceptor.buildWideRowWrapper(propertyMeta)).thenReturn(rawValue);
		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isSameAs(rawValue);
	}

	@Test
	public void should_return_wide_map_wrapper() throws Throwable
	{
		PropertyMeta<UUID, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(UUID.class, String.class)
				.field("tweets")
				.accessors()
				.type(PropertyType.WIDE_MAP)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		when(context.isWideRow()).thenReturn(false);
		when(interceptor.buildWideMapWrapper(propertyMeta)).thenReturn(rawValue);
		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isSameAs(rawValue);
	}

	@Test
	public void should_return_counter_wide_map_wrapper() throws Throwable
	{
		PropertyMeta<String, Counter> propertyMeta = PropertyMetaTestBuilder
				.completeBean(String.class, Counter.class)
				.field("popularTopics")
				.accessors()
				.type(PropertyType.COUNTER_WIDE_MAP)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		when(context.isWideRow()).thenReturn(false);
		when(interceptor.buildCounterWideMapWrapper(propertyMeta)).thenReturn(rawValue);
		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isSameAs(rawValue);
	}

	@Test
	public void should_return_join_wide_map_wrapper() throws Throwable
	{
		PropertyMeta<Long, UserBean> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Long.class, UserBean.class)
				.field("joinUsers")
				.accessors()
				.type(PropertyType.JOIN_WIDE_MAP)
				.build();

		getterMetas.put(propertyMeta.getGetter(), propertyMeta);
		when(context.isWideRow()).thenReturn(false);
		when(interceptor.buildJoinWideMapWrapper(propertyMeta)).thenReturn(rawValue);
		Object actual = interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

		assertThat(actual).isSameAs(rawValue);
	}

	@Test
	public void should_exception_when_calling_setter_on_counter() throws Throwable
	{
		PropertyMeta<Void, Counter> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, Counter.class)
				.field("count")
				.accessors()
				.type(PropertyType.COUNTER)
				.build();

		// No setter, use getter to simulate setter
		setterMetas.put(propertyMeta.getGetter(), propertyMeta);
		exception.expect(UnsupportedOperationException.class);
		exception
				.expectMessage("Cannot set value directly to a Counter type. Please call the getter first to get handle on the wrapper");
		interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);

	}

	@Test
	public void should_exception_when_calling_setter_on_wide_map() throws Throwable
	{
		PropertyMeta<UUID, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(UUID.class, String.class)
				.field("tweets")
				.accessors()
				.type(PropertyType.WIDE_MAP)
				.build();

		// No setter, use getter to simulate setter
		setterMetas.put(propertyMeta.getGetter(), propertyMeta);
		exception.expect(UnsupportedOperationException.class);
		exception
				.expectMessage("Cannot set value directly to a WideMap structure. Please call the getter first to get handle on the wrapper");
		interceptor.intercept(bean, propertyMeta.getGetter(), args, proxy);
	}

	@Test
	public void should_set_simple_value() throws Throwable
	{
		PropertyMeta<Void, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class)
				.field("name")
				.accessors()
				.type(PropertyType.SIMPLE)
				.build();
		setterMetas.put(propertyMeta.getSetter(), propertyMeta);
		when(proxy.invoke(bean, args)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getSetter(), args, proxy);

		assertThat(alreadyLoaded).isEmpty();
		assertThat(dirtyMap).containsKey(propertyMeta.getSetter());
		assertThat(dirtyMap).containsValue(propertyMeta);
		assertThat(actual).isSameAs(rawValue);
	}

	@Test
	public void should_set_lazy_value() throws Throwable
	{
		PropertyMeta<Void, String> propertyMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class)
				.field("name")
				.accessors()
				.type(PropertyType.LAZY_SIMPLE)
				.build();
		setterMetas.put(propertyMeta.getSetter(), propertyMeta);
		when(proxy.invoke(bean, args)).thenReturn(rawValue);

		Object actual = interceptor.intercept(bean, propertyMeta.getSetter(), args, proxy);

		assertThat(alreadyLoaded).contains(propertyMeta.getGetter());
		assertThat(dirtyMap).containsKey(propertyMeta.getSetter());
		assertThat(dirtyMap).containsValue(propertyMeta);
		assertThat(actual).isSameAs(rawValue);
	}
}
