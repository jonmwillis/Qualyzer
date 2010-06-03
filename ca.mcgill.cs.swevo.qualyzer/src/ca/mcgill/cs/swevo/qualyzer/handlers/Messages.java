/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.handlers;

import org.eclipse.osgi.util.NLS;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "ca.mcgill.cs.swevo.qualyzer.handlers.messages"; //$NON-NLS-1$
	public static String handlers_ImportAudioFileHandler_audio;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
