package info.archinnov.achilles.entity.metadata;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * MultiKeyPropertiesTest
 * 
 * @author DuyHai DOAN
 * 
 */
public class MultiKeyPropertiesTest
{
	@Test
	public void should_to_string() throws Exception
	{
		List<Class<?>> componentClasses = Arrays.<Class<?>> asList(Integer.class, String.class);
		MultiKeyProperties props = new MultiKeyProperties();
		props.setComponentClasses(componentClasses);
		props.setComponentNames(Arrays.asList("id", "age"));

		StringBuilder toString = new StringBuilder();
		toString.append("MultiKeyProperties [componentClasses=[");
		toString.append("java.lang.Integer,java.lang.String], componentNames=[id, age]]");

		assertThat(props.toString()).isEqualTo(toString.toString());
	}

	@Test
	public void should_get_cql_component_names() throws Exception
	{
		MultiKeyProperties props = new MultiKeyProperties();
		props.setComponentNames(Arrays.asList("Id", "aGe"));

		assertThat(props.getCQLComponentNames()).containsExactly("id", "age");
	}

	@Test
	public void should_get_cql_ordering_component() throws Exception
	{
		MultiKeyProperties props = new MultiKeyProperties();
		props.setComponentNames(Arrays.asList("id", "age", "label"));

		assertThat(props.getCQLOrderingComponent()).isEqualTo("age");
	}

	@Test
	public void should_return_null_if_no_cql_ordering_component() throws Exception
	{
		MultiKeyProperties props = new MultiKeyProperties();
		props.setComponentNames(Arrays.asList("id"));

		assertThat(props.getCQLOrderingComponent()).isNull();
	}
}
