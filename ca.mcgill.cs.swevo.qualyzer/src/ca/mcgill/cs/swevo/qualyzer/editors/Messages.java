/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.editors;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class Messages
{
	private static final String BUNDLE_NAME = "ca.mcgill.cs.swevo.qualyzer.editors.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages()
	{
	}

	public static String getString(String key)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e)
		{
			return '!' + key + '!';
		}
	}
}
