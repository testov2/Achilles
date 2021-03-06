package info.archinnov.achilles.proxy.wrapper.builder;

import static org.fest.assertions.api.Assertions.assertThat;
import info.archinnov.achilles.context.ThriftPersistenceContext;
import info.archinnov.achilles.dao.ThriftAbstractDao;
import info.archinnov.achilles.proxy.wrapper.ThriftCounterWrapper;
import info.archinnov.achilles.type.ConsistencyLevel;
import me.prettyprint.hector.api.beans.Composite;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

/**
 * ThriftCounterWrapperBuilderTest
 * 
 * @author DuyHai DOAN
 * 
 */

@RunWith(MockitoJUnitRunner.class)
public class ThriftCounterWrapperBuilderTest
{

	private Long key = RandomUtils.nextLong();

	@Mock
	private Composite columnName;

	@Mock
	private ThriftAbstractDao counterDao;

	@Mock
	private ThriftPersistenceContext context;

	private ConsistencyLevel readLevel = ConsistencyLevel.ALL;
	private ConsistencyLevel writeLevel = ConsistencyLevel.ANY;

	@Test
	public void should_build() throws Exception
	{
		ThriftCounterWrapper built = ThriftCounterWrapperBuilder.builder(context) //
				.columnName(columnName)
				.key(key)
				.counterDao(counterDao)
				.readLevel(readLevel)
				.writeLevel(writeLevel)
				.build();

		assertThat(Whitebox.getInternalState(built, "key")).isSameAs(key);
		assertThat(Whitebox.getInternalState(built, "columnName")).isSameAs(columnName);
		assertThat(Whitebox.getInternalState(built, "context")).isSameAs(context);
		assertThat(Whitebox.getInternalState(built, "counterDao")).isSameAs(counterDao);
		assertThat(Whitebox.getInternalState(built, "readLevel")).isSameAs(readLevel);
		assertThat(Whitebox.getInternalState(built, "writeLevel")).isSameAs(writeLevel);
	}
}
