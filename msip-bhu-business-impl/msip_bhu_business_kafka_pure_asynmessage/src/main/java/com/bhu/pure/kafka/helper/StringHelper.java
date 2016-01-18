package com.bhu.pure.kafka.helper;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

//import org.apache.commons.lang.StringUtils;

public final class StringHelper {

	public static final String CHATSET_UTF8 = "utf-8";
	public static final String CHATSET_GBK = "GBK";
	
	private static final int ALIAS_TRUNCATE_LENGTH = 10;
	public static final String WHITESPACE = " \n\r\f\t";
	public static final String NEWLINE = "\r\n"; 
	public static final String OR_STRING_GAP_4SPLIT = "\\|";
	public static final String PLUS_STRING_GAP = "+";
	public static final String PLUS_ENCODE_STRING_GAP = "%2B";
	public static final String QUES_STRING_GAP = "?";
	public static final String SPLIT_PLUS_STRING_GAP = "\\+";
	public static final String LEFT_BRACE_STRING = "{";
	public static final String MINUS_STRING_GAP = "-";
	public static final String MINUS_WHITESPACE_STRING_GAP = " - ";
	public static final String ELLIPSIS_STRING_GAP = "...";
	public static final String AT_STRING_GAP = "@";
	public static final String AND_STRING_GAP = "&";
	public static final String WELL_STRING_GAP = "#";
	public static final String COLON_STRING_GAP = ":";
	public static final String EQUAL_STRING_GAP = "=";
	public static final String EMPTY_STRING_GAP = "";
	public static final String POINT_STRING_GAP = ".";
	public static final String SPLIT_POINT_STRING_GAP = "\\.";
	public static final String PERCENT_GAP = "%";
	public static final char POINT_CHAR_GAP = '.';
	public static final char SEMICOLON_CHAR_GAP = ';';
	public static final String SEMICOLON_STRING_GAP = ";";
	public static final String CHINESESEMICOLON_STRING_GAP = "；";
	public static final String WHITESPACE_STRING_GAP = " ";
	public static final String COMMA_STRING_GAP = ",";
	public static final String ASTERISK_STRING_GAP = "*";
	
	
	public static final String COMMA_HalfAngle_STRING_GAP = "，";
	
	public static final String NULL_STRING_GAP = "null";
	public static final String WAVES_STRING_GAP = "~";
	public static final char STAR_CHAR_GAP = '*';
	//下划线
	public static final char UNDERLINE_CHAR_GAP = '_';
	public static final char PLUS_CHAR_GAP = '+';
	public static final char MINUS_CHAR_GAP = '-';
	public static final String LEFT_MEDIUM_BRACKET_STRING_GAP = "[";
	public static final String RIGHT_MEDIUM_BRACKET_STRING_GAP = "]";
	public static final char LEFT_BRACKET_CHAR_GAP = '(';
	public static final char RIGHT_BRACKET_CHAR_GAP = ')';
	public static final String UNDERLINE_STRING_GAP = "_";
	
	public static final String Special_Str_Inner_Gap = "$%";
	public static final String Special_Str_Outer_Gap = "$#";
	public static final String Split_Special_Str_Inner_Gap = "\\$%";
	public static final String Split_Special_Str_Outer_Gap = "\\$#";
	
	public static final String Special_Str_Alike_Prefix = "%";
	
	public static final String WHITESPACE_LEFT_BRACKET_STRING_GAP = " ( ";
	public static final String WHITESPACE_RIGHT_BRACKET_STRING_GAP =" ) ";
	
	public static final String DOUBLEWHITESPACE_CHAR_GAP = "  ";
	public static final String DOUBLEWHITESPACEAndPlus_CHAR_GAP = " + ";
	public static final String DOUBLEWHITESPACEAndAND_CHAR_GAP = " AND ";
	public static final String DOUBLEWHITESPACEAndOR_CHAR_GAP = " OR ";
	public static final String DOUBLEWHITESPACEAndMinus_CHAR_GAP = " - ";
	public static final String DOUBLEWHITESPACEAndPlus_CHAR_GAP_ESCAPE = " \\+ ";
	
	
	public static final String Common_Spliter_Dict_Gap = "$+";
	public static final String Common_Spliter_Dict_Patterns = "\\$\\+";
	
	public static final String Common_Spliter_Patterns = "[\\+\\-,;]+";//按空格 + - ，；字符串拆分
	public static final String COMMA_SEMICOLON_Spliter_Patterns = "[,;]+";//按空格 + - ，；字符串拆分
	public static final String COMMA_SEMICOLON_Spliter_Patterns2 = "[;]+";
	
	public static final String Special_String_Inner_Search_Gap = "卐";
	
	private static final String template_format_mac_address = "%s:%s:%s:%s:%s:%s";
	private static final String template_unformat_mac_address = "%s%s%s%s%s%s";


	public static final String TRUE = "true";
	public static final String FALSE = "false";

	public static String formatMacAddress(String unformat_address){
		if (isNullOrEmpty(unformat_address)) {
			return "";
		}
		unformat_address = unformat_address.toUpperCase();
		int addlen = unformat_address.length();
		if(addlen!=17 && addlen !=12) {
			//invalid mac address;
			return unformat_address;
		}
		if(addlen==17) return unformat_address;
		String macAddress = String.format(template_format_mac_address, 
				unformat_address.substring(0, 2),unformat_address.substring(2, 4),
				unformat_address.substring(4, 6),unformat_address.substring(6, 8),
				unformat_address.substring(8, 10),unformat_address.substring(10, 12));
		/*String macAddress = address.substring(0, 2) + ":" + address.substring(3, 5)
        + ":" + address.substring(6, 8) + ":" + address.substring(9, 11)
        + ":" + address.substring(12, 14) + ":"
        + address.substring(15, 17);*/
		return macAddress;
	}
	
	public static String unformatMacAddress(String format_address){
		if (isNullOrEmpty(format_address)) {
			return EMPTY_STRING_GAP;
		}
		format_address = format_address.toUpperCase();
		int addlen = format_address.length();
		if(addlen!=17) {
			//invalid mac format address;
			return format_address;
		}
		//if(addlen==17) return format_address;
		String macAddress = String.format(template_unformat_mac_address, 
				format_address.substring(0, 2),format_address.substring(3, 5),
				format_address.substring(6, 8),format_address.substring(9, 11),
				format_address.substring(12, 14),format_address.substring(15, 17));
		return macAddress;
	}
	
	
	/*public static boolean containsDigits(String string) {
		for ( int i=0; i<string.length(); i++ ) {
			if ( Character.isDigit( string.charAt(i) ) ) return true;
		}
		return false;
	}*/
	/**
	 * 按照分隔符分隔出来单个trim再返回
	 * @param str
	 * @param separator
	 * @return
	 */
	public static String trimsStrs(String str, String separator){
		if(StringHelper.isEmpty(str)) return str;
		
		String[] str_array = str.split(separator);
		StringBuffer str_trim = new StringBuffer();
		for(String item : str_array){
			if(str_trim.length() > 0){
				str_trim.append(separator);
			}
			str_trim.append(item.trim());
		}
		return str_trim.toString();
	}
	
	/**
	 * 去掉字符串中的空格和tab
	 * 
	 * @param string
	 *            字符串
	 * @return 去掉后的值
	 */
	public static String removeWhiteSpace(String string) {
		if (isNullOrEmpty(string)) {
			return "";
		} else {
			string = string.replace(" ", "");
			string = string.replace("\t", "");

			return string;
		}
	}
	public static boolean isNullOrEmpty(String string) {
		return !(string != null && string.trim().length() != 0);
	}
	
	public static int lastIndexOfLetter(String string) {
		for ( int i=0; i<string.length(); i++ ) {
			char character = string.charAt(i);
			if ( !Character.isLetter(character) /*&& !('_'==character)*/ ) return i-1;
		}
		return string.length()-1;
	}
	/**
	 * 判断加号和减号解析的元素的有几个
	 * @param string
	 * @return
	 */
	public static int getPlusOrMinusCount(String string){
		String[] array = string.split("[+-]+");
		return array.length;
	}

	public static String join(String seperator, String[] strings) {
		int length = strings.length;
		if ( length == 0 ) return "";
		StringBuffer buf = new StringBuffer( length * strings[0].length() )
				.append( strings[0] );
		for ( int i = 1; i < length; i++ ) {
			buf.append( seperator ).append( strings[i] );
		}
		return buf.toString();
	}

	public static String join(String seperator, Iterator<Object> objects) {
		StringBuffer buf = new StringBuffer();
		if ( objects.hasNext() ) buf.append( objects.next() );
		while ( objects.hasNext() ) {
			buf.append( seperator ).append( objects.next() );
		}
		return buf.toString();
	}

	public static String[] add(String[] x, String sep, String[] y) {
		String[] result = new String[x.length];
		for ( int i = 0; i < x.length; i++ ) {
			result[i] = x[i] + sep + y[i];
		}
		return result;
	}

	public static String repeat(String string, int times) {
		StringBuffer buf = new StringBuffer( string.length() * times );
		for ( int i = 0; i < times; i++ ) buf.append( string );
		return buf.toString();
	}

	public static String reverse(String words){
		if(isEmpty(words)) return words;
		return new StringBuffer(words).reverse().toString();
	}
	
	public static String replace(String template, Map<String,String> replacement){
		String ret = template;
		Set<String> keys = replacement.keySet();
		for(String key : keys){
			ret = ret.replaceAll(key, replacement.get(key));
		}
		return ret;
	}
	
	public static String replace(String template, String placeholder, String replacement) {
		return replace( template, placeholder, replacement, false );
	}

	public static String[] replace(String templates[], String placeholder, String replacement) {
		String[] result = new String[templates.length];
		for ( int i =0; i<templates.length; i++ ) {
			result[i] = replace( templates[i], placeholder, replacement );;
		}
		return result;
	}

	public static String replace(String template, String placeholder, String replacement, boolean wholeWords) {
		int loc = template == null ? -1 : template.indexOf( placeholder );
		if ( loc < 0 ) {
			return template;
		}
		else {
			final boolean actuallyReplace = !wholeWords ||
					loc + placeholder.length() == template.length() ||
					!Character.isJavaIdentifierPart( template.charAt( loc + placeholder.length() ) );
			String actualReplacement = actuallyReplace ? replacement : placeholder;
			return new StringBuffer( template.substring( 0, loc ) )
					.append( actualReplacement )
					.append( replace( template.substring( loc + placeholder.length() ),
							placeholder,
							replacement,
							wholeWords ) ).toString();
		}
	}


	public static String replaceOnce(String template, String placeholder, String replacement) {
        int loc = template == null ? -1 : template.indexOf( placeholder );
		if ( loc < 0 ) {
			return template;
		}
		else {
			return new StringBuffer( template.substring( 0, loc ) )
					.append( replacement )
					.append( template.substring( loc + placeholder.length() ) )
					.toString();
		}
	}

	public static String unqualify(String qualifiedName) {
		int loc = qualifiedName.lastIndexOf(".");
		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( qualifiedName.lastIndexOf(".") + 1 );
	}

	public static String qualifier(String qualifiedName) {
		int loc = qualifiedName.lastIndexOf(".");
		return ( loc < 0 ) ? "" : qualifiedName.substring( 0, loc );
	}

	public static String[] suffix(String[] columns, String suffix) {
		if ( suffix == null ) return columns;
		String[] qualified = new String[columns.length];
		for ( int i = 0; i < columns.length; i++ ) {
			qualified[i] = suffix( columns[i], suffix );
		}
		return qualified;
	}

	private static String suffix(String name, String suffix) {
		return ( suffix == null ) ? name : name + suffix;
	}

	public static String root(String qualifiedName) {
		int loc = qualifiedName.indexOf( "." );
		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( 0, loc );
	}

	public static String unroot(String qualifiedName) {
		int loc = qualifiedName.indexOf( "." );
		return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc+1, qualifiedName.length() );
	}

	public static boolean booleanValue(String tfString) {
		String trimmed = tfString.trim().toLowerCase();
		return trimmed.equals( "true" ) || trimmed.equals( "t" );
	}

	public static String toString(Object[] array) {
		int len = array.length;
		if ( len == 0 ) return "";
		StringBuffer buf = new StringBuffer( len * 12 );
		for ( int i = 0; i < len - 1; i++ ) {
			buf.append( array[i] ).append(", ");
		}
		return buf.append( array[len - 1] ).toString();
	}
	
	public static String toString(Object[] array, String split) {
		int len = array.length;
		if ( len == 0 ) return "";
		StringBuffer buf = new StringBuffer( len * 12 );
		for ( int i = 0; i < len - 1; i++ ) {
			buf.append( array[i] ).append(split);
		}
		return buf.append( array[len - 1] ).toString();
	}

	public static String[] multiply(String string, Iterator<String> placeholders, Iterator<String[]> replacements) {
		String[] result = new String[]{string};
		while ( placeholders.hasNext() ) {
			result = multiply( result, ( String ) placeholders.next(), ( String[] ) replacements.next() );
		}
		return result;
	}

	private static String[] multiply(String[] strings, String placeholder, String[] replacements) {
		String[] results = new String[replacements.length * strings.length];
		int n = 0;
		for ( int i = 0; i < replacements.length; i++ ) {
			for ( int j = 0; j < strings.length; j++ ) {
				results[n++] = replaceOnce( strings[j], placeholder, replacements[i] );
			}
		}
		return results;
	}

	public static int countUnquoted(String string, char character) {
		if ( '\'' == character ) {
			throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
		}
		if (string == null)
			return 0;
		// Impl note: takes advantage of the fact that an escpaed single quote
		// embedded within a quote-block can really be handled as two seperate
		// quote-blocks for the purposes of this method...
		int count = 0;
		int stringLength = string.length();
		boolean inQuote = false;
		for ( int indx = 0; indx < stringLength; indx++ ) {
			char c = string.charAt( indx );
			if ( inQuote ) {
				if ( '\'' == c ) {
					inQuote = false;
				}
			}
			else if ( '\'' == c ) {
				inQuote = true;
			}
			else if ( c == character ) {
				count++;
			}
		}
		return count;
	}
	
	private static final String NULL1_String = "(null)";
	private static final String NULL2_String = "null";
	public static boolean isNotEmpty(String string) {
		return !isEmpty(string);//string != null && string.length() > 0;
	}

	public static boolean isEmpty(String string) {
		return string == null || 
				string.length() == 0 || 
				NULL1_String.equalsIgnoreCase(string) ||
				NULL2_String.equalsIgnoreCase(string)
				;
	}

	public static boolean hasLeastOneNotEmpty(String... strs){
		if(strs == null || strs.length == 0) return false;
		for(String str : strs){
			if(isNotEmpty(str)) return true;
		}
		return false;
	}
	public static String qualify(String prefix, String name) {
		if ( name == null || prefix == null ) {
			throw new NullPointerException();
		}
		return new StringBuffer( prefix.length() + name.length() + 1 )
				.append(prefix)
				.append('.')
				.append(name)
				.toString();
	}

	public static String[] qualify(String prefix, String[] names) {
		if ( prefix == null ) return names;
		int len = names.length;
		String[] qualified = new String[len];
		for ( int i = 0; i < len; i++ ) {
			qualified[i] = qualify( prefix, names[i] );
		}
		return qualified;
	}

	public static int firstIndexOfChar(String sqlString, String string, int startindex) {
		int matchAt = -1;
		for ( int i = 0; i < string.length(); i++ ) {
			int curMatch = sqlString.indexOf( string.charAt( i ), startindex );
			if ( curMatch >= 0 ) {
				if ( matchAt == -1 ) { // first time we find match!
					matchAt = curMatch;
				}
				else {
					matchAt = Math.min( matchAt, curMatch );
				}
			}
		}
		return matchAt;
	}

	public static String truncate(String string, int length) {
		if ( string.length() <= length ) {
			return string;
		}
		else {
			return string.substring( 0, length );
		}
	}

	public static String generateAlias(String description) {
		return generateAliasRoot(description) + '_';
	}

	/**
	 * Generate a nice alias for the given class name or collection role
	 * name and unique integer. Subclasses of Loader do <em>not</em> have 
	 * to use aliases of this form.
	 * @return an alias of the form <tt>foo1_</tt>
	 */
	public static String generateAlias(String description, int unique) {
		return generateAliasRoot(description) +
			Integer.toString(unique) +
			'_';
	}

	/**
	 * Generates a root alias by truncating the "root name" defined by
	 * the incoming decription and removing/modifying any non-valid
	 * alias characters.
	 *
	 * @param description The root name from which to generate a root alias.
	 * @return The generated root alias.
	 */
	private static String generateAliasRoot(String description) {
		String result = truncate( unqualifyEntityName(description), ALIAS_TRUNCATE_LENGTH )
				.toLowerCase()
		        .replace( '/', '_' ) // entityNames may now include slashes for the representations
				.replace( '$', '_' ); //classname may be an inner class
		result = cleanAlias( result );
		if ( Character.isDigit( result.charAt(result.length()-1) ) ) {
			return result + "x"; //ick!
		}
		else {
			return result;
		}
	}

	/**
	 * Clean the generated alias by removing any non-alpha characters from the
	 * beginning.
	 *
	 * @param alias The generated alias to be cleaned.
	 * @return The cleaned alias, stripped of any leading non-alpha characters.
	 */
	private static String cleanAlias(String alias) {
		char[] chars = alias.toCharArray();
		// short cut check...
		if ( !Character.isLetter( chars[0] ) ) {
			for ( int i = 1; i < chars.length; i++ ) {
				// as soon as we encounter our first letter, return the substring
				// from that position
				if ( Character.isLetter( chars[i] ) ) {
					return alias.substring( i );
				}
			}
		}
		return alias;
	}

	public static String unqualifyEntityName(String entityName) {
		String result = unqualify(entityName);
		int slashPos = result.indexOf( '/' );
		if ( slashPos > 0 ) {
			result = result.substring( 0, slashPos - 1 );
		}
		return result;
	}
	
	public static String toUpperCase(String str) {
		return str==null ? null : str.toUpperCase();
	}
	
	public static String toLowerCase(String str) {
		return str==null ? null : str.toLowerCase();
	}

	/**
	 * 字符串首字母大写，其余小写
	 * @return
	 */
	/*public static String toFirstCharUpperCase(String str){
		if(str == null) return null;
		if(StringUtils.isEmpty(str)) return StringUtils.EMPTY;
		str.charAt(0);
		byte[] items = str.getBytes();
		items[0] =  (byte)((char)items[0]-'a'+'A');;
		return new String(items);
	}*/
	/** 
     * 将一个字符串的首字母改为大写或者小写 
     * 	true字符串首字母大写，其余小写
     *  false字符串首字母小写，其余大写
     * @param srcString 源字符串 
     * @param flag            大小写标识，ture小写，false大些 
     * @return 改写后的新字符串 
     */ 
    public static String toFirstUpAndOtherLowerCase(String srcString) { 
    	
      int strLen;
      if (srcString == null || (strLen = srcString.length()) == 0) {
           return srcString;
      }
      return new StringBuffer(strLen)
            .append(Character.toTitleCase(srcString.charAt(0)))
           .append(srcString.substring(1).toLowerCase())
          .toString();
    }
    	/*if(srcString == null) return null;
		if(StringUtils.isEmpty(srcString)) return StringUtils.EMPTY;
		//StringUtils.
        StringBuilder sb = new StringBuilder(); 
        if (flag) { 
                sb.append(Character.toLowerCase(srcString.charAt(0))); 
        } else { 
                sb.append(Character.toUpperCase(srcString.charAt(0))); 
        } 
        sb.append(srcString.substring(1)); 
        return sb.toString(); */
	public static String moveAndToBeginning(String filter) {
		if ( filter.trim().length()>0 ){
			filter += " and ";
			if ( filter.startsWith(" and ") ) filter = filter.substring(4);
		}
		return filter;
	}
	

  public  static String html2Text(String inputString) {
      String htmlStr = inputString; //含html标签的字符串
      String textStr ="";
      Pattern p_script;
      Matcher m_script;
      Pattern p_style;
      Matcher m_style;
      Pattern p_html;
      Matcher m_html;

      try {
          String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
          String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }
          String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

          p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
          m_script = p_script.matcher(htmlStr);
          htmlStr = m_script.replaceAll(""); //过滤script标签

          p_style = Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
          m_style = p_style.matcher(htmlStr);
          htmlStr = m_style.replaceAll(""); //过滤style标签

          p_html = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
          m_html = p_html.matcher(htmlStr);
          htmlStr = m_html.replaceAll(""); //过滤html标签

          textStr = htmlStr;

     }catch(Exception e) {
          System.err.println("Html2Text: " + e.getMessage());
     }
     return textStr;//返回文本字符串
  }    
  
  public final static char SEPARATOR = '\n';
  /**
   * 把字符串数组按照自定义的格式连接成String
   * @return
   */
  /*public static String changeString(String[] ss,String tableAndField) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ss.length; i++) {
        sb.append(tableAndField + " like ");
        sb.append("'"+ss[i]+"%'");
      if (i != (ss.length - 1)) {
        sb.append(" or ");
      }
    }
    return sb.toString();
  }*/
  
/*  public static final BigInteger KB_bt = new BigInteger("1024");//1024
  public static final BigInteger MB_bt = new BigInteger("1048576");//1024 * 1024
  public static final BigInteger GB_bt = new BigInteger("1073741824");//1024 * 1024 * 1024
  public static final BigInteger TB_bt = new BigInteger("1099511627776");//1024 * 1024 * 1024 * 1024
  
  public static String formateBytes(String bytes){
	  if(StringHelper.isEmpty(bytes)) return "0B";
	  int length = bytes.length();
	  if(length <= 4){
		  return bytes.concat("B");
	  }
	  if(length > 4 && length <= 9){
		  return ((new BigInteger(bytes).divide(KB_bt)).toString()).concat("KB");
	  }
	  if(length > 9 && length <= 13){
		  return ((new BigInteger(bytes).divide(MB_bt)).toString()).concat("MB");
	  }
	  if(length > 14 && length <= 17){
		  return ((new BigInteger(bytes).divide(GB_bt)).toString()).concat("GB");
	  }
	  return ((new BigInteger(bytes).divide(TB_bt)).toString()).concat("TB");
  }*/
  /**
   * 此方法将给出的字符串source使用delim划分为单词数组。
   * @param source 需要进行划分的原字符串
   * @param delim 单词的分隔字符串
   * @return 划分以后的数组，如果source为null的时候返回以source为唯一元素的数组，
   *         如果delim为null则使用逗号作为分隔字符串。
   * @since  0.1
   */
  public static String[] split(String source, String delim) {
//    String[] wordLists;
//    if (source == null) {
//      wordLists = new String[1];
//      wordLists[0] = source;
//      return wordLists;
//    }
//    if (delim == null) {
//      delim = ",";
//    }
//    java.util.StringTokenizer st = new java.util.StringTokenizer(source, delim);
//    int total = st.countTokens();
//    wordLists = new String[total];
//    for (int i = 0; i < total; i++) {
//      wordLists[i] = st.nextToken();
//    }
//    return wordLists;
	  if(source == null){
		  return new String[0];
	  }
	  return source.split(delim);
  }

  /**
   * 此方法将给出的字符串source使用delim划分为单词数组。
   * @param source 需要进行划分的原字符串
   * @param delim 单词的分隔字符
   * @return 划分以后的数组，如果source为null的时候返回以source为唯一元素的数组。
   * @since  0.2
   */
  public static String[] split(String source, char delim) {
    return split(source, String.valueOf(delim));
  }

  /**
   * 此方法将给出的字符串source使用逗号划分为单词数组。
   * @param source 需要进行划分的原字符串
   * @return 划分以后的数组，如果source为null的时候返回以source为唯一元素的数组。
   * @since  0.1
   */
  public static String[] split(String source) {
    return split(source, ",");
  }

/**
 * This is a string replacement method.
 */
public static String replaceString(String source, String oldStr, String newStr) {

    StringBuffer sb = new StringBuffer();
    int sind = 0;
    int cind = 0;
    while ((cind=source.indexOf(oldStr, sind)) != -1) {
        sb.append(source.substring(sind, cind));
        sb.append(newStr);
        sind = cind + oldStr.length();
    }
    sb.append(source.substring(sind));
    return sb.toString();
}

  /**
   * 将给定的字符串转换为中文GBK编码的字符串。
   *
   * @param str 输入字符串
   * @return 经GBK编码后的字符串，如果有异常，则返回原编码字符串
   */
  public static String toChinese(final String str) {
//    return str;
    if (isNullorBlank(str)) {
      return str;
    }
    String retVal = str;
    try {
      retVal = new String(str.getBytes("ISO8859_1"), "GBK");
    }
    catch (Exception e) {
      //log...
    }
    return retVal;
  } //end toChinese()
  public static String toISO(final String str) {
//    return str;
//    java.net.URLEncoder.encode(str);
    if (isNullorBlank(str)) {
      return str;
    }
    String retVal = str;
    try {
      retVal = new String(str.getBytes("GBK"), "ISO8859_1");
    }
    catch (Exception e) {
      //log...
    }
    return retVal;
  } //end toChinese()
  /**
   * 给定字符串是否为空。
   *
   * @param str 需要检查的字符串
   * @return 如果为null或""，则返回true,否则为false
   */
  public static boolean isNullorBlank(final String str) {
    boolean isNorB = false;
    if (null == str || 0 >= str.length() || str.equals("null")) {
      isNorB = true;
    }
    return isNorB;
  } //end isNullorBlank()
  /**
   * This method is used to insert HTML block dynamically
   *
   * @param source the HTML code to be processes
   * @param bReplaceNl if true '\n' will be replaced by <br>
   * @param bReplaceTag if true '<' will be replaced by &lt; and
   *                          '>' will be replaced by &gt;
   * @param bReplaceQuote if true '\"' will be replaced by &quot;
   */
  public static String formatHtml(String source,boolean bReplaceNl,boolean bReplaceTag,boolean bReplaceQuote) {
      StringBuffer sb = new StringBuffer();
      int len = source.length();
      for (int i=0; i<len; i++) {
          char c = source.charAt(i);
          switch (c) {
          case '\"':
              if (bReplaceQuote)sb.append("&quot;");
              else sb.append(c);
              break;
          case '<':
              if (bReplaceTag) sb.append("&lt;");
              else sb.append(c);
              break;
          case '>':
              if (bReplaceTag) sb.append("&gt;");
              else sb.append(c);
              break;
          case '\n':
              if (bReplaceNl) {
                  if (bReplaceTag) sb.append("&lt;br&gt;");
                  else sb.append("<br>");
              } else {
                  sb.append(c);
              }
              break;
          case '\r':
              break;
          case '&':
              sb.append("&amp;");
              break;
          default:
              sb.append(c);
              break;
          }
      }
      return sb.toString();
  }



  /**
   * Get hex string from byte array
   */
  public static String toHexString(byte[] res) {
      StringBuffer sb = new StringBuffer(res.length << 1);
      for(int i=0; i<res.length; i++) {
          String digit = Integer.toHexString(0xFF & res[i]);
          if (digit.length() == 1) {
              digit = '0' + digit;
          }
          sb.append(digit);
      }
      return sb.toString().toUpperCase();
  }

  /**
   * Get byte array from hex string
   */
  public static byte[] toByteArray(String hexString) {
      int arrLength = hexString.length() >> 1;
      byte buff[] = new byte[arrLength];
      for(int i=0; i<arrLength; i++) {
          int index = i << 1;
          String digit = hexString.substring(index, index+2);
          buff[i] = (byte)Integer.parseInt(digit, 16);
      }
      return buff;
  }

  /**
   * 将字符串转成整形。
   *
   * @param param 需要转化的字串
   * @return int 转化后的值，有异常则为-1。
   */
  public static int parseInt(final String param) {
    int i = -1;
    try {
      i = Integer.parseInt(param);
    }
    catch (Exception e) {
      //i = (int)parseFloat(param);
    }
    return i;
  } //end parseInt()

  /**
   * 将字符串转化成boolean值。
   *
   * @param param 需要转化的字符串
   * @return boolean值,如果param begin with(1,y,Y,t,T) return true,
   * on exception return false.
   */
  public static boolean parseBoolean(final String param) {
    if (isNullorBlank(param))
      return false;
    switch (param.charAt(0)) {
      case '1':
      case 'y':
      case 'Y':
      case 't':
      case 'T':
        return true;
    }
    return false;
  } //end parseBoolean()


  /**
   * 按给定汉字长度截断字符串。两个字母或数字算一个汉字
   * @param line 给定字符串
   * @param nchars 给定长度
   * @param append 截断后添加到截断字符串的串，如"..."
   * @return 按给定汉字长度截断字符串并添加append字符串。
   */
  public static String chopString(String line,int nchars,String append){
	  int length = realStringCharlength(line);
      if(line == null || length <= nchars) return line;
      int n = 0;
      //int max = nchars;
      for(int i=0;i<line.length();i++){
          char c = line.charAt(i);
          if(c > 128) n+=2;//汉字
          else n++;
          if(n >= nchars && i < line.length()-1){
              String s = line.substring(0,i+1);
              if(append != null && append.length()>0) {
            	s = s+append;
              }
              return s;
          }
      }
      return line;
  }
  
  /**
   * 按给定汉字长度截断字符串。两个字母或数字算一个汉字
   * 截取两头的字符串 在中间部分添加append
   * @param line 给定字符串
   * @param nchars 给定长度
   * @param append 截断后添加到截断字符串的串，如"..."
   * @return 按给定汉字长度截断字符串并添加append字符串。
   */
  public static String chopMiddleString(String line,int nchars,String append){
      if(line == null) return line;
      int length = realStringCharlength(line);
      if(length <= nchars) return line;
      
      int half_nchars = nchars/2;
      String left_string = chopString(line, half_nchars, null);
      if(line.equals(left_string)){
    	  return line;
      }
      String right_string = reverseString(chopString(reverseString(line), half_nchars, null));
      return left_string.concat(append).concat(right_string);
  }
  
	/**
	 * 倒转字符串，输入abc，返回cba
	 * @param string 
	 * @return 倒转后的值
	 */
	public static String reverseString(String string) {
		if (isEmpty(string)) {
			return EMPTY_STRING_GAP;
		} else {
//			StringBuffer connect = new StringBuffer();
//			connect.append(b)
			StringBuffer sb = new StringBuffer();
			for (int i = 1; i <= string.length(); i++) {
				sb.append(string.charAt(string.length() - i));
			}
			return sb.toString();
		}
	}
	
  /**
   * uuid验证 32位
   * @param uuid
   * @return
   */
  //6f5bcd0e-a6c3-4d3b-8c78-e09ad03c7b53
  public static boolean isValidUUIDCharacter(final String uuid) {
	    String namePattern = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";//"/^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$/";
	    Pattern p = Pattern.compile(namePattern);
	    Matcher m = p.matcher(uuid);
	    return m.find();
  }
  
  /**
   * 验证是否是mac地址
   * @param uuid
   * @return
   */
  public static boolean isValidMac(final String mac) {
	  if(isEmpty(mac)) return false;
      Pattern pa= Pattern.compile(MacPattern);
      return pa.matcher(mac).find();
  }
  
  public static boolean isValidMacs(final List<String> macs){
	  if(macs == null || macs.isEmpty()) return false;
	  for(String mac : macs){
		  if(!isValidMac(mac)) return false;
	  }
	  return true;
  }
  private static String MacPattern = "^[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}$";
  /*public static boolean isValidMacCharacter(final String mac){
  String namePattern = "^[A-F0-9]{2}(-[A-F0-9]{2}){5}$";
  Pattern p = Pattern.compile(namePattern);
  Matcher m = p.matcher(mac);
  return m.find();
}*/
  
  //^[\u4E00-\u9FA5A-Za-z0-9]+$
  //只能由大小写英文字母、中文、数字组成 
  
  /**
   * 只能由大小写英文字母、中文、数字组成  ^[\u4E00-\u9FA5A-Za-z0-9]+$
   * 如果还要支持下划线及横杠 则  ^[\u4E00-\u9FA5A-Za-z0-9_-]+$
   */
  private static String NicknamePattern = "^[\u4E00-\u9FA5A-Za-z0-9_]+$";
  public static boolean isValidNicknameCharacter(final String nickname) {
	    //String namePattern = "^[\u4E00-\u9FA5A-Za-z0-9_]+$";
	    Pattern p = Pattern.compile(NicknamePattern);
	    Matcher m = p.matcher(nickname);
	    return m.find();
  }
  
  /**
   *  现有手机号段:
   *移动： 139   138   137   136   135   134   147   150   151   152   157   158  159   182   183   184   187   188  
   *联通： 130   131   132   155   156   185   186   145  
   *电信： 133   153   180   181   189
   *(0|86|17951) 带有长途，国家区号 IP电话
   * @param mobileno
   * @return
   */
  private static String MobilenoPattern =  "^(0|86|17951)?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[57])[0-9]{8}$";//"^0?(13[0-9]|15[012356789]|18[0-9]|14[57])[0-9]{8}$";
  public static boolean isValidMobilenoCharacter(final String mobileno) {
	  //String namePattern = "^(0|86|17951)?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[57])[0-9]{8}$";//"^0?(13[0-9]|15[012356789]|18[0-9]|14[57])[0-9]{8}$";
	  Pattern p = Pattern.compile(MobilenoPattern);
	  Matcher m = p.matcher(mobileno);
	  return m.find();
  }
  
  /**
   * 
	1.如果存在'+'号，必须在第一位
	2.如果存在 '(' ，必须存在  ')'
	3.'-'可以存在任意位置，但不能是第一位和最后一位
	4.可以存在空格
	5.必须有数字
	6.长度不限
	7.出现其他符号不能通过验证
   * @param phoneno
   * @return
   */
  private static String InternationalPhonenoattern = "^\\+?[0-9. ()-]{10,25}$";
  //private static String InternationalPhonenoattern = "^\\+(?:[0-9] ?){6,14}[0-9]$";
  //"^(\\s*\\+?\\s*(?<leftp>\\()?\\s*\\d+\\s*(?(leftp)\\)))(\\s*-\\s*(\\(\\s*\\d+\\s*\\)|\\s*\\d+\\s*))*\\s*$";
  public static boolean isValidInternationalPhonenoCharacter(final String phoneno) {
	  Pattern p = Pattern.compile(InternationalPhonenoattern);
	  Matcher m = p.matcher(phoneno);
	  return m.find();
  }
  
  
  //匹配中文字符的正则表达式： [\u4e00-\u9fa5]
  public static boolean isValidChineseCharacter(final String userName) {
    String namePattern = "[\u4e00-\u9fa5]";
    Pattern p = Pattern.compile(namePattern);
    Matcher m = p.matcher(userName);
    return m.find();
  }
  //匹配双字节字符(包括汉字在内)：[^\x00-\xff]
  public static boolean isValidDoubleBytesCharacter(final String value) {
    String namePattern = "[^\\x00-\\xff]";
    Pattern p = Pattern.compile(namePattern);
    Matcher m = p.matcher(value);
    return m.find();
  }
  //匹配空行的正则表达式：\n[\s| ]*\r
  public static boolean isValidBlankCharacter(final String value) {
    String namePattern = "\n[\\s| ]*\r";
    Pattern p = Pattern.compile(namePattern);
    Matcher m = p.matcher(value);
    return m.find();
  }
  //匹配HTML标记的正则表达式：/<(.*)>.*<\/\1>|<(.*) \/>/
  public static boolean isValidHtmlCharacter(final String value) {
    String namePattern = "/<(.*)>.*<\\/\\1>|<(.*) \\/>/";
    Pattern p = Pattern.compile(namePattern);
    Matcher m = p.matcher(value);
    return m.find();
  }
  //匹配首尾空格的正则表达式：(^\s*)|(\s*$)
  public static boolean isValidStartOrEndBlankCharacter(final String value) {
    String namePattern = "(^\\s*)|(\\s*$)";
    Pattern p = Pattern.compile(namePattern);
    Matcher m = p.matcher(value);
    return m.find();
  }
  //匹配Email地址的正则表达式：\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*
  public static boolean isValidEmailCharacter(final String value) {
    String namePattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
    Pattern p = Pattern.compile(namePattern);
    Matcher m = p.matcher(value);
    return m.find();
  }
  //^[A-Za-z0-9]+$
  public static boolean isValidPermalinkCharacter(final String value){
	//String namePattern = "[^(0-9\\s\\-\\_)+][a-z0-9\\-\\_]+";
	String namePattern = "[^(0-9\\s\\-\\_)+][A-Za-z0-9\\-\\_]+$";
	Pattern p = Pattern.compile(namePattern);
	Matcher m = p.matcher(value);
	return m.find();
  }
  
  //匹配网址URL的正则表达式：http://([\w-]+\.)+[\w-]+(/[\w- ./?%&=]*)?
  public static boolean isValidURLCharacter(final String value) {
    String namePattern = "http://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
    Pattern p = Pattern.compile(namePattern);
    Matcher m = p.matcher(value);
    return m.find();
  }
  /*
   这里是判断YYYY-MM-DD这种格式的，基本上把闰年和2月等的情况都考虑进去了，不过我已经忘了在哪里找到的。
   ^((((1[6-9]|[2-9]\d)\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\d|3[01]))|(((1[6-9]|[2-9]\d)\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\d|30))|(((1[6-9]|[2-9]\d)\d{2})-0?2-(0?[1-9]|1\d|2[0-8]))|(((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$
   */
  public static boolean isValidDateCharacter(final String value){
    String namePattern = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$";
    Pattern p = Pattern.compile(namePattern);
    Matcher m = p.matcher(value);
    return m.find();
  }
  /*
   这里是判断YYYY-MM-DD hh:mm:ss这种格式的，基本上把闰年和2月等的情况都考虑进去了，不过我已经忘了在哪里找到的。
   ^((((1[6-9]|[2-9]\d)\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\d|3[01]))|(((1[6-9]|[2-9]\d)\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\d|30))|(((1[6-9]|[2-9]\d)\d{2})-0?2-(0?[1-9]|1\d|2[0-8]))|(((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\d):[0-5]?\d:[0-5]?\d$
   */
  public static boolean isValidDateTimeCharacter(final String value){
    String namePattern = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";
    Pattern p = Pattern.compile(namePattern);
    Matcher m = p.matcher(value);
    return m.find();
  }
  
/*
     利用正则表达式限制网页表单里的文本框输入内容：



  用正则表达式限制只能输入中文：onkeyup="value=value.replace(/[^\u4E00-\u9FA5]/g,'')" onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\u4E00-\u9FA5]/g,''))"



  用正则表达式限制只能输入全角字符： onkeyup="value=value.replace(/[^\uFF00-\uFFFF]/g,'')" onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\uFF00-\uFFFF]/g,''))"



  用正则表达式限制只能输入数字：onkeyup="value=value.replace(/[^\d]/g,'') "onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))"



  用正则表达式限制只能输入数字和英文：onkeyup="value=value.replace(/[\W]/g,'') "onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))"
 ^\d+$　　//匹配非负整数（正整数 + 0）
 ^[0-9]*[1-9][0-9]*$　　//匹配正整数
 ^((-\d+)|(0+))$　　//匹配非正整数（负整数 + 0）
 ^-[0-9]*[1-9][0-9]*$　　//匹配负整数
 ^-?\d+$　　　　//匹配整数
 ^\d+(\.\d+)?$　　//匹配非负浮点数（正浮点数 + 0）
 ^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$　　//匹配正浮点数
 ^((-\d+(\.\d+)?)|(0+(\.0+)?))$　　//匹配非正浮点数（负浮点数 + 0）
 ^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$　　//匹配负浮点数
 ^(-?\d+)(\.\d+)?$　　//匹配浮点数
 ^[A-Za-z]+$　　//匹配由26个英文字母组成的字符串
 ^[A-Z]+$　　//匹配由26个英文字母的大写组成的字符串
 ^[a-z]+$　　//匹配由26个英文字母的小写组成的字符串
 ^[A-Za-z0-9]+$　　//匹配由数字和26个英文字母组成的字符串
 ^\w+$　　//匹配由数字、26个英文字母或者下划线组成的字符串
 ^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$　　　　//匹配email地址
 ^[a-zA-z]+://匹配(\w+(-\w+)*)(\.(\w+(-\w+)*))*(\?\S*)?$　　//匹配url




   */
  //主要是区分汉字和字母，不然一个全是字母，一个全是汉字的两条记录排列在一起时会比较难看，全字符的长度只有全汉字 的一半就显示...号了
  public static String viewString1Format(String str,int n){
    ///
    ///格式化字符串长度，超出部分显示省略号,区分汉字跟字母。汉字2个字节，字母数字一个字节
    ///
    String temp="";
    if(str.getBytes().length<=n){//如果长度比需要的长度n小,返回原字符串
      return str;
    }else{
      int t=0;
      char[] q=str.toCharArray();;
      for(int i=0;i<q.length&&t<n;i++){
        if((int)q[i]>=0x4E00 && (int)q[i]<=0x9FA5){//是否汉字
          temp+=q[i];
          t+=2;
        }else if((int)q[i]>=0xFF00 && (int)q[i]<=0xFFFF){//是否全角字符
          temp+=q[i];
          t+=2;
        }else{
          temp+=q[i];
          t++;
        }
      }
      return (temp+"...");
    }
  }
  public static String viewStringFormat(String str,int n){
    ///
    ///格式化字符串长度，超出部分显示省略号,区分汉字跟字母。汉字2个字节，字母数字一个字节
    ///
    if(isNullorBlank(str)) return str;
    String temp="";
    if(str.getBytes().length<=n){//如果长度比需要的长度n小,返回原字符串
      return str;
    }else{
      int strLen = str.length();
      int returnSize = 0;
      for(int i=0;i<strLen&&returnSize<n;i++){
        String nowStr = str.substring(i,i+1);
        if(isGB2312(nowStr)){
          temp +=nowStr;
          returnSize+=2;
        }else{
          temp +=nowStr;
          returnSize+=1;
        }
      }
      //System.out.println(str.substring(0,returnSize));
      return (temp+"...");
    }
  }

  public static boolean isGB2312( String str )
  {
    char[] chars = str.toCharArray();
    //boolean isGB2312 = false;
    for (char c : chars) {
      byte[] bytes = ("" + c).getBytes();
      if (bytes.length == 2) {
        int[] ints = new int[2];
        ints[0] = bytes[0] & 0xff;
        ints[1] = bytes[1] & 0xff;
        if (ints[0] >= 0x81 && ints[0] <= 0xFE && ints[1] >= 0x40 && ints[1] <= 0xFE)
          return true;
        else return false;
      }else return false;
    }
    return false;
  }


  private static NumberFormat cash = new DecimalFormat("$#,##0.00");
  private static NumberFormat number = new DecimalFormat("#,##0.00");
  public static String formatCurrency(int cents) {
    return cash.format((double)(cents/100)); 
  }
  public static String formatNumber(double cents) {
	return number.format((double)(cents));
  }  
  
  /**
   * java中判断字段真实长度（中文2个字符，英文1个字符）的方法
   * 1、判断字符串是否为连续的中文字符(不包含英文及其他任何符号和数字)：
		Regex.IsMatch("中文","^[/u4e00-/u9fa5]")；
	 2、判断字符串是否为中文字符串(仅不包含英文但可以包含其他符号及数字)：
		！Regex.IsMatch("中文",@"[a-zA-Z]")；
   * @param value
   * @return
   */
  public static int realStringCharlength(String value) {
	  int valueLength = 0;
	  String chinese = "[\u4e00-\u9fa5]";
	  for (int i = 0; i < value.length(); i++) {
	   String temp = value.substring(i, i + 1);
	   if (temp.matches(chinese)) {
	    valueLength += 2;
	   } else {
	    valueLength += 1;
	   }
	  }
	  return valueLength;
  }
  

  /**
   * 取时间戳的唯一ID值串
   * @return
   */
/*  public static synchronized String GetGUID()
     {
         Date NewDate;
         try
         {
             StringBuffer stringbuffer = new StringBuffer();
             StringBuffer stringbuffer1 = new StringBuffer();
             SecureRandom seeder = new SecureRandom();
             int i = 0;
             int j = 24;
             try
             {
                 InetAddress inetaddress = InetAddress.getLocalHost();
                 byte abyte0[] = inetaddress.getAddress();
                 for(int k = 0; j >= 0; k++)
                 {
                     int l = abyte0[k] & 0xff;
                     i += l << j;
                     j -= 8;
                 }

             }
             catch(Exception exception)
             {
                 i = seeder.nextInt();
             }
             String s = ToHex(i, 8);
             String s1 = ToHex(Integer.toString(i).hashCode(), 8);
             stringbuffer1.append(s.substring(0, 4));
             stringbuffer1.append(s.substring(4));
             stringbuffer1.append(s1.substring(0, 4));
             stringbuffer1.append(s1.substring(4));
             stringbuffer.append("-");
             stringbuffer.append(s.substring(0, 4));
             stringbuffer.append("-");
             stringbuffer.append(s.substring(4));
             stringbuffer.append("-");
             stringbuffer.append(s1.substring(0, 4));
             stringbuffer.append("-");
             stringbuffer.append(s1.substring(4));
             String midValue = stringbuffer.toString();
             long l = System.currentTimeMillis();
             int i1 = (int)l & -1;
             int j1 = seeder.nextInt();
             midValue = String.valueOf(String.valueOf((new StringBuffer("{")).append(ToHex(i1, 8)).append(midValue).append(ToHex(j1, 8)).append("}")));
             String midValueUnformated = stringbuffer1.toString();
             int i2 = (int)l & -1;
             int j2 = seeder.nextInt();
             midValueUnformated = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(ToHex(i2, 8))))).append(midValueUnformated).append(ToHex(j2, 8))));
             int GUIDHashCode = midValue.hashCode();
             if(GUIDHashCode < 0)
                 GUIDHashCode = -GUIDHashCode;
             String s3 = Integer.toString(GUIDHashCode);
             return s3;
         }
         catch(Exception exception)
         {
             NewDate = new Date();
         }
         //Calendar.getInstance().setTime(date);
         int Year = NewDate.getYear() + 1900;
         String GUID = String.valueOf(Year);
         int Month = NewDate.getMonth();
         if(Month < 10)
             GUID = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(GUID)))).append("0").append(String.valueOf(Month))));
         else
             GUID = String.valueOf(GUID) + String.valueOf(String.valueOf(Month));
         int Day = NewDate.getDate();
         if(Day < 10)
             GUID = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(GUID)))).append("0").append(String.valueOf(Day))));
         else
             GUID = String.valueOf(GUID) + String.valueOf(String.valueOf(Day));
         int Week = NewDate.getDay();
         GUID = String.valueOf(GUID) + String.valueOf(String.valueOf(Week));
         int Hours = NewDate.getHours();
         if(Hours < 10)
             GUID = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(GUID)))).append("0").append(String.valueOf(Hours))));
         else
             GUID = String.valueOf(GUID) + String.valueOf(String.valueOf(Hours));
         int Minutes = NewDate.getMinutes();
         if(Minutes < 10)
             GUID = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(GUID)))).append("0").append(String.valueOf(Minutes))));
         else
             GUID = String.valueOf(GUID) + String.valueOf(String.valueOf(Minutes));
         int Seconds = NewDate.getSeconds();
         if(Seconds < 10)
             GUID = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(GUID)))).append("0").append(String.valueOf(Seconds))));
         else
             GUID = String.valueOf(GUID) + String.valueOf(String.valueOf(Seconds));
         long Time = NewDate.getTime();
         if(Time < (long)10)
             GUID = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(GUID)))).append("0").append(String.valueOf(Time))));
         else
             GUID = String.valueOf(GUID) + String.valueOf(String.valueOf(Time));
         for(long l = System.currentTimeMillis(); System.currentTimeMillis() == l; l = System.currentTimeMillis());
         String s2 = GUID;
         return s2;
     }*/

     public static String ToHex(int i, int j)
     {
         String s = Integer.toHexString(i);
         StringBuffer stringbuffer = new StringBuffer();
         if(s.length() < j)
         {
             for(int k = 0; k < j - s.length(); k++)
                 stringbuffer.append("0");

             return String.valueOf(stringbuffer.toString()) + String.valueOf(s);
         } else
         {
             return s.substring(0, j);
         }
     }

     public static String ToHex(String str)
     {
         if(str == null || "".equals(str))
             return "";
         //String HexArray = "0123456789abcdef";
         String hex = "";
         int charAscNum = 0;
         byte bytes[] = str.getBytes();
         for(int i = 0; i < bytes.length; i++)
         {
             charAscNum = bytes[i];
             hex = String.valueOf(hex) + String.valueOf(Integer.toHexString(charAscNum));
         }

         return hex;
     }
     /**
      * null转换为空字串
      * @param str
      * @return
      * @throws java.lang.Exception
      */
     public static String NullAsBlank(String str)
             throws Exception
         {
             if(str == null)
                 return "";
             else
                 return str;
         }
     
     
     
     public final static String getValue( final String str ) {
         return getValue(str, '=');
     }
     
     /**
      * getValue: get the value portion from a key/value <code>java.lang.String</code> <br>
      * pair e.g "myKey=myValue" using the delim <code>char</code> as the divider  <br>
      * returns value portion of the <code>java.lang.String</code> <br>
      *
      * <p><dl><dt><b>Usage: </b><dd><code>org.jutil.TString.getValue(String str, '=');</code><br></dl><p>
      */
     public final static String getValue( final String str, final char delim ) {
         if(str == null){
             throw new java.lang.NullPointerException("String str passed to getValue( String str, char delim ) is null");
         }
         
         int pos = str.indexOf(delim);
         if (pos == -1) {
             return "";
         }else{
             return str.substring(pos+1);
         }
     }
     
     /**
      * getValue: get the key portion from a key/value <code>java.lang.String</code> pair <br>
      * e.g "myKey=myValue" using the default delim <code>char</code> '=' as the divider <br>
      * returns key portion of the <code>java.lang.String</code>
      *
      */
     public final static String getKey( final String str ) {
         return getKey( str, '=' );
     }
     
     /**
      * get the key portion from a key/value <code>String</code> pair e.g "myKey=myValue" <br>
      * using the delim <code>char</code> as the divider <br>
      * returns key portion of the <code>String</code> <br>
      *
      */
     public final static String getKey( final String str, final char delim ) {
         if(str == null){
             throw new java.lang.NullPointerException("String str passed to getKey( String str, char delim ) is null");
         }
         
         int pos = str.indexOf(delim);
         if (pos == -1) {
             return "";
         }else{
             return str.substring(0,pos);
         }
     }
     public final static String[] toArray( final String str) {
         return toArray(str, ',');
     }
     
     /**
      * toArray converts a <code>java.lang.String</code> to a <code>java.lang.String[]</code> <br>
      * splitting on the delim <code>char</code> <br>
      *
      */
     
     public final static  String[] toArray( final String input, final char delim ) {
         if(input == null) {
             return new String[0];
         }
         final String str = input.trim();
         if(str.length() < 1) {
             return new  String[0];
         }
         int n = 1;
         int index = -1;
         String[] strs = null;
         
         while ( true ) {
             index = str.indexOf( delim, index + 1 );
             if(index == -1) {
                 break;
             }
             ++n;
         }
         strs = new String[n];
         index = -1;
         for ( int i = 0; i < n - 1; ++i ) {
             int nextIndex = str.indexOf( delim, index + 1 );
             strs[i] = str.substring( index + 1, nextIndex );
             index = nextIndex;
         }
         strs[n - 1] = str.substring( index + 1 );
         return strs;
     }
     
     /**
      * 正则表达式去掉字符串中间的空格及回车换行符
      * @param str
      * @return
      */
     public static String replaceBlankAndLowercase(String str){  
    	   return replaceBlank(str).toLowerCase();   
     }
     
     public static String replaceBlank(String str){  
  	   Pattern p=Pattern.compile("\\s*|\t|\r|\n");  
	   //String str="I am a, I am Hello ok, \n new line ffdsa!";   
	   // System.out.println("before:"+str);  
	   Matcher m = p.matcher(str);  
	   String after = m.replaceAll("");  
	   // System.out.println("after:"+after);  
	   return after;  
	  	  // System.out.println("after:"+after);   
	 }
     
     //去除换行及回车符号
     public static String replaceEnterAndOtherLineChar(String str){  
  	   Pattern p=Pattern.compile("\t|\r|\n"); 
  	   //String str="I am a, I am Hello ok, \n new line ffdsa!";   
  	  // System.out.println("before:"+str);  
  	   Matcher m = p.matcher(str);  
  	   String after = m.replaceAll("");  
  	  // System.out.println("after:"+after);  
  	   return after;  
  	  // System.out.println("after:"+after);   
     }
  // 过滤特殊字符  
     public static String removeSpecialChar(String str){
        // 只允许字母和数字       
        // String   regEx  =  "[^a-zA-Z0-9]";                     
        // 清除掉所有特殊字符  
          String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？-]";  
          Pattern   p   =   Pattern.compile(regEx);     
          Matcher   m   =   p.matcher(str);     
          return   m.replaceAll("").trim();     
     }
     /**
      * 去除字符串首尾空格
      * @param str
      * @return
      */
     public static String removeHeadAndTailPlusChar(String str){
    	 String nowstr = str.trim();
    	 if(nowstr.charAt(0) == '+'){
    		 nowstr = nowstr.substring(1,nowstr.length());
    	 }
    	 if(nowstr.charAt(nowstr.length()-1) == '+'){
    		 nowstr= nowstr.substring(0,nowstr.length()-1);
    	 }
    	 return nowstr.trim();
     }
     
     public static boolean containsSpecialBusinessSplitChar(String str){
    	 Pattern p=Pattern.compile("[\\＋\\－＆\\+\\-&]+");  
    	 Matcher m = p.matcher(str);  
    	 return m.find();
     }
     
     public final static String replaceAllWhiteSpace(String str){
    	 return str.replaceAll(" ", "");
     }
     
     public static boolean  containsPlus(String str){  
    	   Pattern p=Pattern.compile("[+]+");  
    	   Matcher m = p.matcher(str);  
    	   return m.find();
     }
     public static boolean  containsPlusOrMinus(String str){  
  	   Pattern p=Pattern.compile("[+-]+");  
  	   Matcher m = p.matcher(str);  
  	   return m.find();
     }
     
     
     
     /**
      * 中文转unicode
      * @param str
      * @return 反回unicode编码
      */
      public static String GBK2Unicode(String str){
             StringBuffer result = new StringBuffer();
             for (int i = 0; i < str.length(); i++){
                    char chr1 = (char)str.charAt(i);
                    if(!isNeedConvert(chr1)){
                           result.append(chr1); 
                           continue;
                    }
                    result.append("//u" + Integer.toHexString((int)chr1));           
             }
             return result.toString();
      }

       /**
    * unicode转中文
    * @param  str
    * @return 中文
    */
      public static String Unicode2GBK(String dataStr) {
             int index = 0;
             StringBuffer buffer = new StringBuffer();
      
             while(index<dataStr.length()) {
                    if(!"//u".equals(dataStr.substring(index,index+2))){
                     buffer.append(dataStr.charAt(index));
                     index++;
                     continue;
              }
             String charStr = "";
           charStr = dataStr.substring(index+2,index+6);
          
             char letter = (char) Integer.parseInt(charStr, 16 );
              buffer.append(letter);
                    index+=6;
             }
             return buffer.toString();
      }
      public static boolean isNeedConvert(char para){
             return ((para&(0x00FF))!=para);
      }
      /**
       * 解析混淆过的密码
       * @param confusionPwd 混淆规则 每个真实字符后面加一个随机字符
       * @return
       */
      public static String parserConfusionPwd(String confusionPwd){
    	  StringBuffer pstr = new StringBuffer();
    	  int length = confusionPwd.length();
    	  int parsecount = length/2;
    	  String temp = null;
    	  int start = 0;
    	  
    	  for(int i = 0;i<parsecount;i++){
    		  temp = confusionPwd.substring(start, start+1);
    		  pstr.append(temp);
    		  start+=2;
    	  }
    	  return pstr.toString();
      }
      public static String[] parserITuneMediaFilename(String filename){
  		int index = filename.indexOf(StringHelper.WHITESPACE_STRING_GAP);
  		if(index ==-1){
  			return new String[]{"01",filename};
  		}else{
  			return new String[]{filename.substring(0, index),filename.substring(index).trim()};
  		}
  	}
     /**
      * 过滤掉4个字节的utf字符
      * @param text
      * @return
      * @throws UnsupportedEncodingException
      */
  	public static  String filterOffUtf8Mb4(String text) throws UnsupportedEncodingException {
        byte[] bytes = text.getBytes("utf-8");
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        int i = 0;
        while (i < bytes.length) {
            short b = bytes[i];
            if (b > 0) {
                buffer.put(bytes[i++]);
                continue;
            }
            b += 256;
            if ((b ^ 0xC0) >> 4 == 0) {
                buffer.put(bytes, i, 2);
                i += 2;
            }
            else if ((b ^ 0xE0) >> 4 == 0) {
                buffer.put(bytes, i, 3);
                i += 3;
            }
            else if ((b ^ 0xF0) >> 4 == 0) {
                i += 4;
            }
        }
        buffer.flip();
        return new String(buffer.array(), "utf-8");
    } 
  	
	/**
	 * 根据url解析出对应的完整域名
	 * @param url
	 * @return
	 */
	public static String parseDomain(String url){
		if(StringHelper.isEmpty(url)) return null;
		try{
			//获取完整的域名
			Pattern p =Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv|me)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = p.matcher(url);
			matcher.find();
			return matcher.group();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static int compareVersion(String version1, String version2) {
		if(StringUtils.isEmpty(version1)) return -1;
		if(StringUtils.isEmpty(version2)) return 1;
		
        if (version1.equals(version2)) {
            return 0;
        }

        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");

        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;

        while (index < minLen && (diff = Integer.parseInt(version1Array[index]) - Integer.parseInt(version2Array[index])) == 0) {
            index ++;
        }

        if (diff == 0) {
            for (int i = index; i < version1Array.length; i ++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i ++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }

            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }
      /**
           *  java中正则表达式提取字符串中日期实现代码
           * @param text 待提取的字符串
           * @return 返回日期
           * @author: oschina
           * @Createtime: Jan 21, 2013
           */
         /* @SuppressWarnings("unchecked")
          public static String catchDate(String text) {
              String dateStr = text.replaceAll("r?n", " ");
              try {
                  List matches = null;
                  //Pattern p = Pattern.compile("(\\d{1,4}[-|\\/]\\d{1,2}[-|\\/]\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2})", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
                  Pattern p = Pattern.compile("(\\d{1,4}[-|\\/]\\d{1,2}[-|\\/]\\d{1,2})", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
                  Matcher matcher = p.matcher(dateStr);
                  if (matcher.find() && matcher.groupCount() >= 1) {
                      matches = new ArrayList();
                      for (int i = 1; i <= matcher.groupCount(); i++) {
                          String temp = matcher.group(i);
                          matches.add(temp);
                      }
                  } else {
                      matches = Collections.EMPTY_LIST;
                  }           
                  if (matches.size() > 0) {
                      return ((String) matches.get(0)).trim();
                  } else {
                      return "";
                  }
                   
              } catch (Exception e) {
                  return "";
              }
          }*/

      
      
      public static void main(String[] argv) throws UnsupportedEncodingException{
    	  /*StringBuilder sb = new StringBuilder("sfasf");
    	  sb.append(StringHelper.UNDERLINE_CHAR_GAP);
    	  sb.append("af");
    	  System.out.println(sb.toString());*/
    	  /*System.out.println(StringHelper.isValidNicknameCharacter("asdfdsf-com"));
    	  //System.out.println(StringHelper.isValidNicknameCharacter("asdfdsf.com"));
    	  System.out.println(StringHelper.isValidNicknameCharacter("asdfdsf_com"));
    	  
    	  System.out.println(StringHelper.realStringCharlength("z中国人dbc"));
    	  System.out.println("中国人".getBytes("GBK").length);
    	  
    	  String str=" I am a, I am Hello ok, \n new line ff dsa! "; 
    	  System.out.println(StringHelper.replaceBlankAndLowercase(str));
    	  
    	  //String ss = "1156158$%215567$%Andy Tubman$^1155279$%202875$%Flo Rida$^1154062$%203207$%Maria Arredondo$^1142514$%215005$%Tenth Avenue North$^1137743$%202155$%宇多田光";
    	  String ss = "1156158$%215567$%Andy Tubman$#1155279$%202875$%Flo Rida$#1154062$%203207$%Maria Arredondo$#1142514$%215005$%Tenth Avenue North$#1137743$%202155$%宇多田光";
    	  String[] array = ss.split("\\"+StringHelper.Special_Str_Outer_Gap);
    	  for(String str1:array){
    		  String[] array1 = str1.split("\\"+StringHelper.Special_Str_Inner_Gap);
    		  for(String str2:array1){
    			  System.out.println(str2);
    		  }
    	  }*/
    	  
        //StringUtils.conjunctionSelectMenu("DICT_GRADE");
//        System.out.println(StringUtils.isGB2312("ｆ"));
//        System.out.println(StringUtils.viewStringFormat("中国  人abcdas民解sad放军永远永远",15));
//        System.out.println(StringUtils.viewStringFormat("中国人民ｆｆ军sdf！！！永远",15));
//        System.out.println(StringUtils.viewStringFormat("关于112举办！“全国民sdf办中小学管理干部论",15));
//        System.out.println(StringUtils.viewStringFormat("关于联合举办 “国际华语小学语文优秀",15));
//        System.out.println(StringUtils.viewStringFormat("关于开展“十五”中小学校长培训工作",15));
//        System.out.println(new String(" 阿 速度发    送飞 洒飞 ").trim());
//        System.out.println(StringUtils.isValidHtmlCharacter("aｆa"));
//        System.out.println(StringUtils.isValidDateCharacter("2002-1-1"));
//        System.out.println(StringUtils.isValidDateCharacter("2002-2-29"));
//        System.out.println(StringUtils.isValidDateTimeCharacter("2002-02-09 12:09:09"));
//        Dict dict = DictFactory.getDefaultInstance().getDict("DICT_RESTYPE");
//        java.util.Collection col = dict.getRootNodes();
//        System.out.println(StringUtils.getSelOptionElement(col,"  "));
    	  //System.out.println(StringUtils.formatNumber(111234.4567));
        //System.out.println(org.apache.struts.action.Action.MESSAGES_KEY);
    	  //System.out.println(StringHelper.isValidNicknameCharacter("_我心飘扬"));
    	  
    /*	  String tests ="s撒旦法df";// "ssfs+fss三点-多";
    	  System.out.println(containsPlusOrMinus(tests));
    	  
    	  String argss = removeHeadAndTailPlusChar("+ssfs+fss三点-多_+");
    	  System.out.println(argss);
    	  
    	  System.out.println(containsPlus("ssfs-fss三点多"));*/
    	  
    /*	  String jingle = "苹果NANO-霓";
    	  String[] array_js = jingle.split(StringHelper.Common_Spliter_Patterns);
    	  for(String sss:array_js){
    		  System.out.println(sss);
    	  }
    	  
    	  System.out.println(StringHelper.isValidDateCharacter("2012-09-09"));
    	  System.out.println(StringHelper.isValidDateCharacter("2012-9-31"));*/
    	  
    	  /*System.out.println("------"+StringHelper.containsSpecialBusinessSplitChar("sd@fs+s-.net"));
    	  
    	  
    	  String sssss = "1s_小布。";
    	  System.out.println("------"+StringHelper.removeSpecialChar(sssss));
    	  
    	  String abc = "this is \r me \n who \t !";
    	  System.out.println(abc);
    	  Pattern p = Pattern.compile("\t|\r|\n");
    	  Matcher m = p.matcher(abc);
    	  String abcd = m.replaceAll("");
    	  System.out.println(abcd);
    	  
    	  
    	  String ss_sss= "Sony";
    	  System.out.println(ss_sss);
    	  System.out.println(StringHelper.toFirstUpAndOtherLowerCase(ss_sss));
    	  //System.out.println(StringHelper.toFirstCharUpperCase(ss_sss));
    	  
    	  
    	  
    	  String mac = "f0:CB:A1:C9:30:E7";
    	  System.out.println(mac);
    	  System.out.println(StringHelper.formatMacAddress(mac));*/
    	  /*
    	  中文：/[\u4e00-\u9fa5]/

    	  日文：/[\u0800-\u4e00]/

    	  韩文：/[\uac00-\ud7ff]/
    	  */
    	  //String s1 = "凛として時雨";
    	 /* String s1 = "時";
    	  System.out.println(s1.matches("[\\u4e00-\\u9fa5]+"));
    	  System.out.println(s1.matches("[\\u0800-\\u4e00]+"));
    	  System.out.println(s1.matches("[\\uac00-\\ud7ff]+"));
    	  
    	  System.out.println(new String("\u7eb8\u7247\u6218\u673aW".getBytes(),"utf-8"));
    	  
    	  System.out.println(new String("TBS\u7cfb\u5217\u2018\u3072\u308b\u304a\u3073!\u20197\u6708\u5ea6".getBytes(),"utf-8"));
    	  System.out.println(containsSpecialBusinessSplitChar("英文＆女"));*/
    	  //String s = "唐.子.超-哈哈,唐.子.超-哈哈,唐.子.超-哈哈,";
    	  //System.out.println(s.substring(0, 14));
    	 // StringBuffer sb = new StringBuffer();
  	      /*String namePattern = "[\u4e00-\u9fa5]";//"^[\u4E00-\u9FA5A-Za-z0-9]+$";
	      Pattern p = Pattern.compile(namePattern,Pattern.CANON_EQ.CASE_INSENSITIVE);
	      Matcher m = p.matcher(s);
	      if(m.find())
	    	  System.out.println(m.replaceAll(""));*/
    	  //System.out.println(removeSpecialChar(s));
	      /*StringBuffer sb = new StringBuffer();
	      m.appendReplacement(sb, m.group(1)+"0");
	      //if(m.find())
	    	 // System.out.println(m.replaceAll(""));
	      System.out.println(sb);*/
    	  
    	 /* String str = "户号:0468773993 户名:李xx  时间:2013年-1月-20日 15:00:00 剩余金额不足,已超过警戒点B,请速续费.";
    	          str = catchDate(str);
    	          System.out.print(str);
          Pattern p=Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");  
          Matcher m=p.matcher("x20xxx1984年-10-20xxx19852x");  
            
          if(m.find()){  
              System.out.println("日期:"+m.group());  
              System.out.println("年:"+m.group(1));  
              System.out.println("月:"+m.group(2));  
              System.out.println("日:"+m.group(3));  
          }  */
    	  /*System.out.println(StringHelper.parserConfusionPwd("152t3z475R6Z7J859x0w"));
    	  System.out.println(BCryptHelper.checkpw("1234567890","$2a$10$4i39476JZMmidX/hbmUITefC/9y5qEZR/eRdx.mrsC0BE68YzReKi"));
    	  
    	  System.out.println(StringHelper.isValidUUIDCharacter("936BB8D6-E266-4FCE-8379-5AD7AD8B3CA4"));
    	  System.out.println(StringHelper.isValidUUIDCharacter( UUID.randomUUID().toString()));
    	  System.out.println(UUID.randomUUID().toString());
    	  System.out.println(StringHelper.isValidUUIDCharacter(" "));*/
    	  
    	  
    	  //System.out.println(StringHelper.isValidMobilenoCharacter("17211145736"));
    	  
    	  //System.out.println(StringHelper.isValidNicknameCharacter("ddadf-s"));
    	  
    	  
    	  /*System.out.println(StringHelper.isValidInternationalPhonenoCharacter("+861721114577"));
    	  System.out.println(StringHelper.isValidInternationalPhonenoCharacter("1812897-8978"));
    	  System.out.println(StringHelper.isValidInternationalPhonenoCharacter("812897-8978"));
    	  System.out.println(StringHelper.isValidInternationalPhonenoCharacter("17093556163"));
    	  System.out.println(StringHelper.isValidInternationalPhonenoCharacter("18612272825"));*/
    	  //System.out.println(StringHelper.realStringCharlength("哈哈哈哈哈"));
    	  
//    	  String patternRegx = "^/((noauth)|(statistics)|(device)|(ping)|(common))";
//    	 // String patternRegx = "(^/noauth)|(^/statistics)|(^/device)|(^/ping)|(^/common)";
//    	  //String patternRegx = "^[(/device)|(noauth)]";
//    	  Pattern pattern = Pattern.compile(patternRegx);
//    	  Matcher matcher = pattern.matcher("/user/device/fetchbinded?uid=306");
//          //Matcher matcher = pattern.matcher("/statistics/fetch_wifidevices_by_registerat");
//          System.out.println(matcher.find());
          /*if (matcher.find()) {// matcher.matchers() {
              String fqdnId = matcher.group();
          }*/
    	  String name = "panpan的手机设备啊哈哈哈";
    	  System.out.println(chopMiddleString(name, 15, "..."));
      }
}
