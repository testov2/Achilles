package info.archinnov.achilles.entity.metadata.util;

import static info.archinnov.achilles.entity.metadata.PropertyType.*;
import static org.fest.assertions.api.Assertions.assertThat;
import info.archinnov.achilles.entity.metadata.PropertyMeta;

import java.util.Arrays;

import org.junit.Test;

import testBuilders.PropertyMetaTestBuilder;

import com.google.common.collect.Collections2;

/**
 * PropertyTypeExcludeTest
 * 
 * @author DuyHai DOAN
 * 
 */
public class PropertyTypeExcludeTest
{

	@Test
	public void should_exclude_by_types() throws Exception
	{
		PropertyTypeExclude exclude = new PropertyTypeExclude(COUNTER, SIMPLE);

		PropertyMeta<Void, String> pm1 = PropertyMetaTestBuilder
				.valueClass(String.class)
				.entityClassName("entity")
				.field("pm1")
				.type(SET)
				.build();

		PropertyMeta<Void, String> pm2 = PropertyMetaTestBuilder
				.valueClass(String.class)
				.entityClassName("entity")
				.field("pm2")
				.type(SIMPLE)
				.build();

		PropertyMeta<Void, String> pm3 = PropertyMetaTestBuilder
				.valueClass(String.class)
				.entityClassName("entity")
				.field("pm3")
				.type(WIDE_MAP)
				.build();

		assertThat(Collections2.filter(Arrays.asList(pm1, pm2), exclude)).containsOnly(pm1);
		assertThat(Collections2.filter(Arrays.asList(pm1, pm3), exclude)).containsOnly(pm1, pm3);
	}
}
