package info.archinnov.achilles.entity.metadata.builder;

import info.archinnov.achilles.dao.Pair;
import info.archinnov.achilles.entity.metadata.EntityMeta;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import info.archinnov.achilles.entity.type.ConsistencyLevel;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import me.prettyprint.hector.api.Serializer;

/**
 * EntityMetaTestBuilder
 * 
 * @author DuyHai DOAN
 * 
 */
public class EntityMetaTestBuilder<ID>
{

	private PropertyMeta<Void, ID> idMeta;
	private String classname;
	private String columnFamilyName;
	private Long serialVersionUID;
	private Serializer<ID> idSerializer;
	private Map<String, PropertyMeta<?, ?>> propertyMetas = new HashMap<String, PropertyMeta<?, ?>>();
	private Map<Method, PropertyMeta<?, ?>> getterMetas = new HashMap<Method, PropertyMeta<?, ?>>();
	private Map<Method, PropertyMeta<?, ?>> setterMetas = new HashMap<Method, PropertyMeta<?, ?>>();
	private boolean columnFamilyDirectMapping = false;
	private Pair<ConsistencyLevel, ConsistencyLevel> consistencyLevels;

	public static <ID> EntityMetaTestBuilder<ID> builder(PropertyMeta<Void, ID> idMeta)
	{
		return new EntityMetaTestBuilder<ID>(idMeta);
	}

	public EntityMeta<ID> build()
	{
		EntityMeta<ID> meta = new EntityMeta<ID>();
		meta.setIdMeta(idMeta);
		meta.setClassName(classname);
		meta.setColumnFamilyName(columnFamilyName);
		meta.setSerialVersionUID(serialVersionUID);
		meta.setIdSerializer(idMeta.getValueSerializer());
		meta.setPropertyMetas(propertyMetas);
		meta.setGetterMetas(getterMetas);
		meta.setColumnFamilyDirectMapping(columnFamilyDirectMapping);
		meta.setConsistencyLevels(consistencyLevels);

		return meta;
	}

	public EntityMetaTestBuilder(PropertyMeta<Void, ID> idMeta) {
		this.idMeta = idMeta;
	}

	public EntityMetaTestBuilder<ID> classname(String classname)
	{
		this.classname = classname;
		return this;
	}

	public EntityMetaTestBuilder<ID> columnFamilyName(String columnFamilyName)
	{
		this.columnFamilyName = columnFamilyName;
		return this;
	}

	public EntityMetaTestBuilder<ID> serialVersionUID(Long serialVersionUID)
	{
		this.serialVersionUID = serialVersionUID;
		return this;
	}

	public EntityMetaTestBuilder<ID> propertyMetas(Map<String, PropertyMeta<?, ?>> propertyMetas)
	{
		this.propertyMetas = propertyMetas;
		return this;
	}

	public EntityMetaTestBuilder<ID> columnFamilyDirectMapping(boolean columnFamilyDirectMapping)
	{
		this.columnFamilyDirectMapping = columnFamilyDirectMapping;
		return this;
	}

	public EntityMetaTestBuilder<ID> consistencyLevels(
			Pair<ConsistencyLevel, ConsistencyLevel> consistencyLevels)
	{
		this.consistencyLevels = consistencyLevels;
		return this;
	}

	public <K, V> EntityMetaTestBuilder<ID> addPropertyMeta(PropertyMeta<K, V> propertyMeta)
	{
		this.propertyMetas.put(propertyMeta.getPropertyName(), propertyMeta);
		return this;
	}

	public <T, K, V> EntityMetaTestBuilder<ID> addGetter(Class<T> targetClass, String getter,
			PropertyMeta<K, V> propertyMeta) throws SecurityException, NoSuchMethodException
	{
		Method getterMethod = targetClass.getDeclaredMethod(getter);
		getterMetas.put(getterMethod, propertyMeta);
		return this;
	}

	public <T, S, K, V> EntityMetaTestBuilder<ID> addSetter(Class<T> targetClass, String setter,
			Class<S> type, PropertyMeta<K, V> propertyMeta) throws SecurityException,
			NoSuchMethodException
	{
		Method setterMethod = targetClass.getDeclaredMethod(setter, type);
		setterMetas.put(setterMethod, propertyMeta);
		return this;
	}

}