package info.archinnov.achilles.wrapper;

import info.archinnov.achilles.wrapper.builder.EntryIteratorWrapperBuilder;
import info.archinnov.achilles.wrapper.builder.MapEntryWrapperBuilder;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * EntrySetWrapper
 * 
 * @author DuyHai DOAN
 * 
 */
public class EntrySetWrapper<ID, K, V> extends AbstractWrapper<ID, K, V> implements
		Set<Entry<K, V>>
{

	private Set<Entry<K, V>> target;

	public EntrySetWrapper(Set<Entry<K, V>> target) {
		this.target = target;
	}

	@Override
	public boolean add(Entry<K, V> arg0)
	{
		throw new UnsupportedOperationException("This method is not supported for an Entry set");
	}

	@Override
	public boolean addAll(Collection<? extends Entry<K, V>> arg0)
	{
		throw new UnsupportedOperationException("This method is not supported for an Entry set");
	}

	@Override
	public void clear()
	{
		this.target.clear();
		this.markDirty();
	}

	@Override
	public boolean contains(Object arg0)
	{
		return this.target.contains(proxifier.unproxy(arg0));
	}

	@Override
	public boolean containsAll(Collection<?> arg0)
	{
		return this.target.containsAll(proxifier.unproxy(arg0));
	}

	@Override
	public boolean isEmpty()
	{
		return this.target.isEmpty();
	}

	@Override
	public Iterator<Entry<K, V>> iterator()
	{
		return EntryIteratorWrapperBuilder //
				.builder(context, this.target.iterator()) //
				.dirtyMap(dirtyMap) //
				.setter(setter) //
				.propertyMeta(propertyMeta) //
				.proxifier(proxifier) //
				.build();
	}

	@Override
	public boolean remove(Object arg0)
	{
		boolean result = false;
		result = this.target.remove(proxifier.unproxy(arg0));
		if (result)
		{
			this.markDirty();
		}
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> arg0)
	{
		boolean result = false;
		result = this.target.removeAll(proxifier.unproxy(arg0));
		if (result)
		{
			this.markDirty();
		}
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> arg0)
	{
		boolean result = false;
		result = this.target.retainAll(proxifier.unproxy(arg0));
		if (result)
		{
			this.markDirty();
		}
		return result;
	}

	@Override
	public int size()
	{
		return this.target.size();
	}

	@Override
	public Object[] toArray()
	{
		Object[] result = null;
		if (isJoin())
		{
			Object[] array = new MapEntryWrapper[this.target.size()];
			int i = 0;
			for (Map.Entry<K, V> entry : this.target)
			{
				array[i] = MapEntryWrapperBuilder //
						.builder(context, entry) //
						.dirtyMap(dirtyMap) //
						.setter(setter) //
						.propertyMeta(propertyMeta) //
						.proxifier(proxifier) //
						.build();
				i++;
			}
			result = array;
		}
		else
		{
			result = this.target.toArray();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] arg0)
	{
		T[] result = null;
		if (isJoin())
		{
			T[] array = this.target.toArray(arg0);

			for (int i = 0; i < array.length; i++)
			{
				array[i] = (T) MapEntryWrapperBuilder //
						.builder(context, (Entry<K, V>) array[i]) //
						.dirtyMap(dirtyMap) //
						.setter(setter) //
						.propertyMeta(propertyMeta) //
						.proxifier(proxifier) //
						.build();
			}
			result = array;
		}
		else
		{
			result = this.target.toArray(arg0);
		}
		return result;
	}

}