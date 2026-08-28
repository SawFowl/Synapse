package sawfowl.synapse.api.utils;

public class StringUtils {

	/**
	 * <p>Counts how many times the char appears in the given string.</p>
	 *
	 * <p>A {@code null} or empty ("") String input returns {@code 0}.</p>
	 *
	 * <pre>
	 * StringUtils.countMatches(null, *)	   = 0
	 * StringUtils.countMatches("", *)		 = 0
	 * StringUtils.countMatches("abba", 0)  = 0
	 * StringUtils.countMatches("abba", 'a')   = 2
	 * StringUtils.countMatches("abba", 'b')  = 2
	 * StringUtils.countMatches("abba", 'x') = 0
	 * </pre>
	 *
	 * @param str  the CharSequence to check, may be null
	 * @param ch  the char to count
	 * @return the number of occurrences, 0 if the CharSequence is {@code null}
	 * @since 3.4
	 */
	public static int countMatches(final CharSequence str, final char ch) {
		if (isEmpty(str)) {
			return 0;
		}
		int count = 0;
		// We could also call str.toCharArray() for faster look ups but that would generate more garbage.
		for (int i = 0; i < str.length(); i++) {
			if (ch == str.charAt(i)) {
				count++;
			}
		}
		return count;
	}

	// Empty checks
	//-----------------------------------------------------------------------
	/**
	 * <p>Checks if a CharSequence is empty ("") or null.</p>
	 *
	 * <pre>
	 * StringUtils.isEmpty(null)	  = true
	 * StringUtils.isEmpty("")		= true
	 * StringUtils.isEmpty(" ")	   = false
	 * StringUtils.isEmpty("bob")	 = false
	 * StringUtils.isEmpty("  bob  ") = false
	 * </pre>
	 *
	 * <p>NOTE: This method changed in Lang version 2.0.
	 * It no longer trims the CharSequence.
	 * That functionality is available in isBlank().</p>
	 *
	 * @param cs  the CharSequence to check, may be null
	 * @return {@code true} if the CharSequence is empty or null
	 * @since 3.0 Changed signature from isEmpty(String) to isEmpty(CharSequence)
	 */
	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

}
