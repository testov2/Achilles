package info.archinnov.achilles.entity.metadata;

import info.archinnov.achilles.entity.metadata.util.CascadeMergeFilter;
import info.archinnov.achilles.entity.metadata.util.CascadePersistFilter;
import info.archinnov.achilles.entity.metadata.util.CascadeRefreshFilter;
import info.archinnov.achilles.exception.AchillesBeanMappingException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;

import org.apache.commons.lang.StringUtils;

/**
 * JoinProperties
 * 
 * @author DuyHai DOAN
 * 
 */
public class JoinProperties
{
	public static CascadePersistFilter hasCascadePersist = new CascadePersistFilter();
	public static CascadeMergeFilter hasCascadeMerge = new CascadeMergeFilter();
	public static CascadeRefreshFilter hasCascadeRefresh = new CascadeRefreshFilter();

	private EntityMeta entityMeta;
	private Set<CascadeType> cascadeTypes = new HashSet<CascadeType>();

	public EntityMeta getEntityMeta()
	{
		return entityMeta;
	}

	public void setEntityMeta(EntityMeta entityMeta)
	{
		this.entityMeta = entityMeta;
	}

	public Set<CascadeType> getCascadeTypes()
	{
		return cascadeTypes;
	}

	public void setCascadeTypes(Set<CascadeType> cascadeTypes)
	{
		this.cascadeTypes = cascadeTypes;
	}

	public void addCascadeType(CascadeType cascadeType)
	{
		this.cascadeTypes.add(cascadeType);
	}

	public void addCascadeType(Collection<CascadeType> cascadeTypesCollection)
	{
		if (cascadeTypesCollection.contains(CascadeType.REMOVE))
		{
			throw new AchillesBeanMappingException(
					"CascadeType.REMOVE is not supported for join columns");
		}
		this.cascadeTypes.addAll(cascadeTypesCollection);
	}

	@Override
	public String toString()
	{
		return "JoinProperties [entityMeta=" + entityMeta.getClassName() + ", cascadeTypes=["
				+ StringUtils.join(cascadeTypes, ",") + "]]";
	}
}
