package parser.entity;

import info.archinnov.achilles.annotations.Key;
import info.archinnov.achilles.type.MultiKey;

/**
 * MultiKeyNotInstantiable
 * 
 * @author DuyHai DOAN
 * 
 */
public class MultiKeyNotInstantiable implements MultiKey
{
	@Key(order = 1)
	private String name;

	@Key(order = 2)
	private int rank;

	public MultiKeyNotInstantiable(String name, int rank) {
		super();
		this.name = name;
		this.rank = rank;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getRank()
	{
		return rank;
	}

	public void setRank(int rank)
	{
		this.rank = rank;
	}

}
