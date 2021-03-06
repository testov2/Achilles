package parser.entity;

import info.archinnov.achilles.annotations.Key;
import info.archinnov.achilles.type.MultiKey;

/**
 * CorrectMultiKeyUnorderedKeys
 * 
 * @author DuyHai DOAN
 * 
 */
public class CorrectMultiKeyUnorderedKeys implements MultiKey
{
	@Key(order = 2)
	private int rank;

	@Key(order = 1)
	private String name;

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
