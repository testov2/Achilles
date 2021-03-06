package info.archinnov.achilles.integration.spring;

import static info.archinnov.achilles.configuration.ConfigurationParameters.*;
import static info.archinnov.achilles.configuration.ThriftConfigurationParameters.*;
import static org.apache.commons.lang.StringUtils.*;
import info.archinnov.achilles.entity.manager.ThriftEntityManager;
import info.archinnov.achilles.entity.manager.ThriftEntityManagerFactory;
import info.archinnov.achilles.json.ObjectMapperFactory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ThriftEntityManagerJavaConfigSample
 * 
 * @author DuyHai DOAN
 * 
 */
@Configuration
public class ThriftEntityManagerJavaConfigSample
{

	@Value("#{cassandraProperties['achilles.entity.packages']}")
	private String entityPackages;

	@Autowired(required = true)
	private Cluster cluster;

	@Autowired(required = true)
	private Keyspace keyspace;

	@Autowired
	private ObjectMapperFactory objecMapperFactory;

	@Value("#{cassandraProperties['achilles.consistency.read.default']}")
	private String consistencyLevelReadDefault;

	@Value("#{cassandraProperties['achilles.consistency.write.default']}")
	private String consistencyLevelWriteDefault;

	@Value("#{cassandraProperties['achilles.consistency.read.map']}")
	private String consistencyLevelReadMap;

	@Value("#{cassandraProperties['achilles.consistency.write.map']}")
	private String consistencyLevelWriteMap;

	@Value("#{cassandraProperties['achilles.ddl.force.column.family.creation']}")
	private String forceColumnFamilyCreation;

	@Value("#{cassandraProperties['achilles.consistency.join.check']}")
	private String ensureJoinConsistency;

	private ThriftEntityManagerFactory emf;

	@PostConstruct
	public void initialize()
	{
		Map<String, Object> configMap = extractConfigParams();
		emf = new ThriftEntityManagerFactory(configMap);
	}

	@Bean
	public ThriftEntityManager getEntityManager()
	{
		return (ThriftEntityManager) emf.createEntityManager();
	}

	private Map<String, Object> extractConfigParams()
	{
		Map<String, Object> configMap = new HashMap<String, Object>();
		configMap.put(ENTITY_PACKAGES_PARAM, entityPackages);

		configMap.put(CLUSTER_PARAM, cluster);
		configMap.put(KEYSPACE_NAME_PARAM, keyspace);

		if (objecMapperFactory != null)
		{
			configMap.put(OBJECT_MAPPER_FACTORY_PARAM, objecMapperFactory);
		}

		if (isNotBlank(consistencyLevelReadDefault))
		{
			configMap.put(CONSISTENCY_LEVEL_READ_DEFAULT_PARAM, consistencyLevelReadDefault);
		}
		if (isNotBlank(consistencyLevelWriteDefault))
		{
			configMap.put(CONSISTENCY_LEVEL_WRITE_DEFAULT_PARAM, consistencyLevelWriteDefault);
		}

		if (isNotBlank(consistencyLevelReadMap))
		{
			configMap.put(CONSISTENCY_LEVEL_READ_MAP_PARAM,
					extractConsistencyMap(consistencyLevelReadMap));
		}
		if (isNotBlank(consistencyLevelWriteMap))
		{
			configMap.put(CONSISTENCY_LEVEL_WRITE_MAP_PARAM,
					extractConsistencyMap(consistencyLevelWriteMap));
		}

		configMap.put(FORCE_CF_CREATION_PARAM, Boolean.parseBoolean(forceColumnFamilyCreation));
		configMap
				.put(ENSURE_CONSISTENCY_ON_JOIN_PARAM, Boolean.parseBoolean(ensureJoinConsistency));

		return configMap;
	}

	private Map<String, String> extractConsistencyMap(String consistencyMapProperty)
	{
		Map<String, String> consistencyMap = new HashMap<String, String>();

		for (String entry : split(consistencyMapProperty, ","))
		{
			String[] entryValue = StringUtils.split(entry, ":");
			assert entryValue.length == 2 : "Invalid map value : " + entry + " for the property : "
					+ consistencyMapProperty;
			consistencyMap.put(entryValue[0], entryValue[1]);
		}
		return consistencyMap;
	}
}
