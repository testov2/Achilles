package info.archinnov.achilles.validation;

import static info.archinnov.achilles.helper.PropertyHelper.isSupportedType;
import info.archinnov.achilles.exception.AchillesBeanMappingException;
import info.archinnov.achilles.exception.AchillesException;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Validator
 * 
 * @author DuyHai DOAN
 * 
 */
public class Validator
{
	public static void validateNotBlank(String arg, String message)
	{
		if (StringUtils.isBlank(arg))
		{
			throw new AchillesException(message);
		}
	}

	public static void validateNotNull(Object arg, String message)
	{
		if (arg == null)
		{
			throw new AchillesException(message);
		}
	}

	public static void validateNotEmpty(Collection<?> arg, String message)
	{
		if (arg == null || arg.isEmpty())
		{
			throw new AchillesException(message);
		}
	}

	public static <T> void validateContains(Collection<T> collection, T element, String message)
	{
		if (!collection.contains(element))
		{
			throw new AchillesException(message);
		}
	}

	public static void validateBeanMappingNotEmpty(Collection<?> arg, String message)
	{
		if (arg == null || arg.isEmpty())
		{
			throw new AchillesBeanMappingException(message);
		}
	}

	public static void validateNotEmpty(Map<?, ?> arg, String label)
	{
		if (arg == null || arg.isEmpty())
		{
			throw new AchillesException(label);
		}
	}

	public static void validateSize(Map<?, ?> arg, int size, String message)
	{
		validateNotEmpty(arg, "The map should not be empty");
		if (arg.size() != size)
		{
			throw new AchillesException(message);
		}
	}

	public static void validateSerializable(Class<?> clazz, String message)
	{
		if (clazz.isPrimitive() || clazz.isEnum() || isSupportedType(clazz))
		{
			return;
		}

		if (!Serializable.class.isAssignableFrom(clazz))
		{
			throw new AchillesBeanMappingException(message);
		}
	}

	public static void validateNoargsConstructor(Class<?> clazz)
	{
		if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()))
		{
			Constructor<?>[] constructors = clazz.getConstructors();
			for (Constructor<?> constructor : constructors)
			{
				if (Modifier.isPublic(constructor.getModifiers())
						&& constructor.getParameterTypes().length == 0)
				{
					return;
				}
			}
			throw new AchillesBeanMappingException("The class '" + clazz.getCanonicalName()
					+ "' should have a public default constructor");
		}
	}

	public static void validateRegExp(String arg, String regexp, String label)
	{
		validateNotBlank(arg, "The text value '" + label + "' should not be blank");
		if (!Pattern.matches(regexp, arg))
		{
			throw new AchillesException("The property '" + label + "' should match the pattern '"
					+ regexp + "'");
		}
	}

	public static void validateInstantiable(Class<?> arg)
	{
		validateNotNull(arg, "The class should not be null");
		String canonicalName = arg.getCanonicalName();
		validateNoargsConstructor(arg);
		try
		{
			arg.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new AchillesBeanMappingException(
					"Cannot instantiate the class '"
							+ canonicalName
							+ "'. Please ensure the class is not an abstract class, an interface, an array class, a primitive type, or void and have a nullary (default) constructor and is declared public");
		}
		catch (IllegalAccessException e)
		{
			throw new AchillesBeanMappingException("Cannot instantiate the class '" + canonicalName
					+ "'. Please ensure the class has a public nullary (default) constructor");
		}
		catch (SecurityException e)
		{
			throw new AchillesBeanMappingException("Cannot instantiate the class '" + canonicalName
					+ "'");
		}
	}

	public static void validateTrue(boolean condition, String message)
	{
		if (!condition)
		{
			throw new AchillesException(message);
		}
	}

	public static void validateBeanMappingTrue(boolean condition, String message)
	{
		if (!condition)
		{
			throw new AchillesBeanMappingException(message);
		}
	}

	public static void validateFalse(boolean condition, String message)
	{
		if (condition)
		{
			throw new AchillesException(message);
		}
	}

	public static void validateBeanMappingFalse(boolean condition, String message)
	{
		if (condition)
		{
			throw new AchillesBeanMappingException(message);
		}
	}
}
