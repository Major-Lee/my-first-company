/*
 * Copyright 2001-2004 (C) MetaStuff, Ltd. All Rights Reserved.
 *
 * This software is open source.
 * See the bottom of this file for the licence.
 *
 * $Id: XMPPPacketReader.java 3190 2005-12-12 15:00:46Z gato $
 */

package com.smartwork.multiplexer.spi;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.dom4j.DocumentException;

/**
 * <p><code>XMPPPacketReader</code> is a Reader of DOM4J documents that
 * uses the fast
 * <a href="http://www.extreme.indiana.edu/soap/xpp/">XML Pull Parser 3.x</a>.
 * It is very fast for use in SOAP style environments.</p>
 *
 * @author <a href="mailto:pelle@neubia.com">Pelle Braendgaard</a>
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 3190 $
 */
public class StringPacketReader {

    /**
     * Last time a full Document was read or a heartbeat was received. Hearbeats
     * are represented as whitespaces received while a Document is not being parsed.
     */
    private long lastActive = System.currentTimeMillis();
    
    private BufferedReader input;

    public StringPacketReader() {
    }



    /**
     * <p>Reads a Document from the given <code>File</code></p>
     *
     * @param file is the <code>File</code> to read from.
     * @return the newly created Document instance
     * @throws DocumentException              if an error occurs during parsing.
     * @throws java.net.MalformedURLException if a URL could not be made for the given File
     */
    public String read(File file) throws IOException {
        String systemID = file.getAbsolutePath();
        return read(new BufferedReader(new FileReader(file)), systemID);
    }

    /**
     * <p>Reads a Document from the given <code>URL</code></p>
     *
     * @param url <code>URL</code> to read from.
     * @return the newly created Document instance
     * @throws DocumentException if an error occurs during parsing.
     */
    public String read(URL url) throws IOException {
        String systemID = url.toExternalForm();
        return read(createReader(url.openStream()), systemID);
    }

    /**
     * <p>Reads a Document from the given URL or filename.</p>
     * <p/>
     * <p/>
     * If the systemID contains a <code>':'</code> character then it is
     * assumed to be a URL otherwise its assumed to be a file name.
     * If you want finer grained control over this mechansim then please
     * explicitly pass in either a {@link URL} or a {@link File} instance
     * instead of a {@link String} to denote the source of the document.
     * </p>
     *
     * @param systemID is a URL for a document or a file name.
     * @return the newly created Document instance
     * @throws DocumentException              if an error occurs during parsing.
     * @throws java.net.MalformedURLException if a URL could not be made for the given File
     */
    public String read(String systemID) throws IOException {
        if (systemID.indexOf(':') >= 0) {
            // lets assume its a URL
            return read(new URL(systemID));
        }
        else {
            // lets assume that we are given a file name
            return read(new File(systemID));
        }
    }

    /**
     * <p>Reads a Document from the given stream</p>
     *
     * @param in <code>InputStream</code> to read from.
     * @return the newly created Document instance
     * @throws DocumentException if an error occurs during parsing.
     */
    public String read(InputStream in) throws IOException {
        return read(createReader(in));
    }

    /**
     * <p>Reads a Document from the given stream</p>
     *
     * @param charSet the charSet that the input is encoded in
     * @param in <code>InputStream</code> to read from.
     * @return the newly created Document instance
     * @throws DocumentException if an error occurs during parsing.
     */
    public String read(String charSet, InputStream in)
            throws IOException
    {
        return read(createReader(in, charSet));
    }

    /**
     * <p>Reads a Document from the given <code>Reader</code></p>
     *
     * @param reader is the reader for the input
     * @return the newly created Document instance
     * @throws DocumentException if an error occurs during parsing.
     */
    public String read(BufferedReader reader) throws IOException {
        //getXPPParser().setInput(reader);
        return parseContent(reader);
    }

    /**
     * <p>Reads a Document from the given array of characters</p>
     *
     * @param text is the text to parse
     * @return the newly created Document instance
     * @throws DocumentException if an error occurs during parsing.
     */
    public String read(char[] text) throws IOException {
        //getXPPParser().setInput(new CharArrayReader(text));
        return new CharArrayReader(text).toString();
    }

    /**
     * <p>Reads a Document from the given stream</p>
     *
     * @param in       <code>InputStream</code> to read from.
     * @param systemID is the URI for the input
     * @return the newly created Document instance
     * @throws DocumentException if an error occurs during parsing.
     */
    public String read(InputStream in, String systemID) throws IOException {
        return read(createReader(in), systemID);
    }

    /**
     * <p>Reads a Document from the given <code>Reader</code></p>
     *
     * @param reader   is the reader for the input
     * @param systemID is the URI for the input
     * @return the newly created Document instance
     * @throws DocumentException if an error occurs during parsing.
     */
    public String read(BufferedReader reader, String systemID) throws IOException {
        String document = read(reader);
        //document.setName(systemID);
        return document;
    }

    public String parseContent() throws IOException {
    	if(input == null) return null;
    	return parseContent(input);
    }

    // Implementation methods
    //-------------------------------------------------------------------------
    public String parseContent(BufferedReader reader) throws IOException{
    	//StringBuilder sb = new StringBuilder();
    	String tempString = null;
//        int line = 1;
        // 一次读入一行，直到读入null为文件结束
        while ((tempString = reader.readLine()) != null && tempString.length() > 0) {
        	//sb.append(tempString);
        	break;
        }
        //System.out.println("parseContent :" + sb.toString());
        return tempString;//sb.toString();
/*    	char[] buffer = new char[256];
    	try {
	    	//将字符读取为一串串流存入到buffer中
	    	int dataLen = reader.read(buffer);
	    	//可以将字符流再以字符串的形式打印出来
	    	System.out.println(new String(buffer, 0, dataLen));
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return null;
    	int charread = 0;
    	while ((charread = reader.read(buffer)) != -1) {
            // 同样屏蔽掉\r不显示
            if ((charread == buffer.length)
                    && (buffer[buffer.length - 1] != '\r')) {
                System.out.print(tempchars);
            } else {
                for (int i = 0; i < charread; i++) {
                    if (tempchars[i] == '\r') {
                        continue;
                    } else {
                        System.out.print(tempchars[i]);
                    }
                }
            }
        }*/
    }

    /**
     * Factory method to create a Reader from the given InputStream.
     */
    protected BufferedReader createReader(InputStream in) throws IOException {
        return new BufferedReader(new InputStreamReader(in));
    }

    protected BufferedReader createReader(InputStream in, String charSet) throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(in, charSet));
    }
    
    public void setInput(BufferedReader input){
    	this.input = input;
    }
    
    /**
     * Returns the last time a full Document was read or a heartbeat was received. Hearbeats
     * are represented as whitespaces or \n received while a Document is not being parsed.
     *
     * @return the time in milliseconds when the last document or heartbeat was received.
     */
    public long getLastActive() {
        long lastHeartbeat = 0;
        /*try {
            lastHeartbeat = getXPPParser().getLastHeartbeat();
        }
        catch (Exception e) {}*/
        return lastActive > lastHeartbeat ? lastActive : lastHeartbeat;
    }
}

