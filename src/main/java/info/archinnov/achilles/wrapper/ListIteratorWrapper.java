package info.archinnov.achilles.wrapper;

import java.util.ListIterator;

/**
 * ListIteratorWrapper
 * 
 * @author DuyHai DOAN
 * 
 */
public class ListIteratorWrapper<ID, V> extends AbstractWrapper<ID, Void, V> implements
		ListIterator<V>
{

	private ListIterator<V> target;

	public ListIteratorWrapper(ListIterator<V> target) {
		this.target = target;
	}

	@Override
	public void add(V e)
	{
		this.target.add(proxifier.unproxy(e));
		this.markDirty();
	}

	@Override
	public boolean hasNext()
	{
		return this.target.hasNext();
	}

	@Override
	public boolean hasPrevious()
	{
		return this.target.hasPrevious();
	}

	@Override
	public V next()
	{
		V entity = this.target.next();
		if (isJoin())
		{
			return proxifier.buildProxy(entity, joinContext(entity));
		}
		else
		{
			return entity;
		}
	}

	@Override
	public int nextIndex()
	{
		return this.target.nextIndex();
	}

	@Override
	public V previous()
	{
		V entity = this.target.previous();
		if (isJoin())
		{
			return proxifier.buildProxy(entity, joinContext(entity));
		}
		else
		{
			return entity;
		}
	}

	@Override
	public int previousIndex()
	{
		return this.target.previousIndex();
	}

	@Override
	public void remove()
	{
		this.target.remove();
		this.markDirty();
	}

	@Override
	public void set(V e)
	{
		this.target.set(proxifier.unproxy(e));
		this.markDirty();
	}

}