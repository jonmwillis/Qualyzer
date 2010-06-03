/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.dialogs;

import org.eclipse.osgi.util.NLS;

/**
 * @author Jonathan Faubert (jonfaub@gmail.com)
 *
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"; //$NON-NLS-1$
	public static String dialogs_RenameDialog_AlreadyUsed;
	public static String dialogs_RenameDialog_AudioToo;
	public static String dialogs_RenameDialog_NewName;
	public static String dialogs_RenameDialog_RenameTheTranscript;
	public static String dialogs_RenameDialog_RenameTranscript;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
