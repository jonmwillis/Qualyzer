/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.wizards;

import org.eclipse.osgi.util.NLS;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "ca.mcgill.cs.swevo.qualyzer.wizards.messages"; //$NON-NLS-1$
	public static String wizards_NewTranscriptWizard_alreadyExists;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
