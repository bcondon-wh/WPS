/**
 * ﻿Copyright (C) 2010
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.wps.server.r.syntax;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.n52.wps.io.data.IData;

/**
 * Defines Syntax and Semantics for Annotations in R Skripts
 * 
 * Syntax in (raw) BNF: <RAnnotation> ::= <StartKey> <AttributeSequence> <EndKey> <StartKey> <Attributequence>
 * ::= <RAnnotationTypeInstance>.getStartKey() <RAnnotationTypeInstance>.getAttributeSequence() <EndKey> ::=
 * RSeparator.ANNOTATION_END.getKey() <AttributeSequence> ::= {<RAttributeInstance>.getKey()
 * ATRIBUTE_VALUE_SEPARATOR} <Attributevalue> {ATTRIBUTE_SEPARATOR <RAttributeSequence>}
 * 
 * @author Matthias Hinz
 */
public class RAnnotation {

    private RAnnotationType type;
    private HashMap<RAttribute, Object> attributeHash;
    static Logger LOGGER = Logger.getLogger(RAnnotation.class);
    //public static String WPS_OFF_START = "wps.off:";
    //public static String WPS_OFF_END = "wps.off.end;";

    /**
     * 
     * @param type
     * @param attributeHash
     * @throws IOException
     *         if AttributHash is not valid for any cause
     * @throws RAnnotationException
     */
    public RAnnotation(RAnnotationType type, HashMap<RAttribute, Object> attributeHash) throws IOException,
            RAnnotationException {
        super();
        this.type = type;    
        this.attributeHash = attributeHash;
        type.validDescription(this);
        LOGGER.debug("NEW " + toString());
    }

    public RAnnotationType getType() {
        return type;
    }

	/**
	 * 
	 * @param attr
	 * @return Returns Attribute value as Java Object in case it is more complex
	 * @throws RAnnotationException
	 */
    public Object getObjectValue(RAttribute attr) throws RAnnotationException {
        Object out = attributeHash.get(attr);

        if (out == null && attr.getDefValue() != null)
        	out = attr.getDefValue();
        else if (attr == RAttribute.ENCODING)
            return getRDataType().encoding;
        if (attr == RAttribute.SCHEMA)
            return getRDataType().schema;
        return out;
    }

    /**
     * 
     * @param attr
     * @return Returns an attribute value as string. Suits for most literal data types
     * @throws RAnnotationException
     */
    public String getStringValue(RAttribute attr) throws RAnnotationException {
    	Object value = getObjectValue(attr);
    	if(value == null) 
    		return null;
    	else
    	return getObjectValue(attr).toString();
    }

    public static List<RAnnotation> filterAnnotations(List<RAnnotation> annotations,
                                                      RAnnotationType type,
                                                      RAttribute attribute,
                                                      String value) throws RAnnotationException {
        LinkedList<RAnnotation> out = new LinkedList<RAnnotation>();
        for (RAnnotation annotation : annotations) {
            // type filter:
            if (type == null || annotation.getType() == type) {
                // attribute - value filter:
                if (attribute == null || value == null || annotation.getStringValue(attribute).equalsIgnoreCase(value)) {
                    out.add(annotation);
                }
            }
        }
        return out;
    }

    public static List<RAnnotation> filterAnnotations(List<RAnnotation> annotations, RAttribute attribute, String value) throws RAnnotationException {
        return filterAnnotations(annotations, null, attribute, value);
    }

    public static List<RAnnotation> filterAnnotations(List<RAnnotation> annotations, RAnnotationType type) throws RAnnotationException {
        return filterAnnotations(annotations, type, null, null);
    }

    /**
     * 
     * @param rClass
     *        - value referring to RAttribute.TYPE
     * @return null or supported IData class for rClass - string
     * @throws RAnnotationException
     */
    public static Class< ? extends IData> getDataClass(String rClass) throws RAnnotationException {
        RTypeDefinition rType = RDataType.getType(rClass);
        return rType.getIDataClass();
    }

    public Class< ? extends IData> getDataClass() throws RAnnotationException {
        String rClass = getStringValue(RAttribute.TYPE);
        return getDataClass(rClass);
    }

    /**
     * Checks if the type - argument of an annotation refers to complex data
     * 
     * @return
     * @throws RAnnotationException
     */
    public static boolean isComplex(String rClass) throws RAnnotationException {
        return RDataType.getType(rClass).isComplex();

    }

    public RDataType getRDataType() throws RAnnotationException {
        return RDataType.getType(getStringValue(RAttribute.TYPE));
    }

    /**
     * @return true, if the type attribute of an Annotation refers to a complex data type
     * @throws RAnnotationException
     */
    public boolean isComplex() throws RAnnotationException {
        return isComplex(this.getStringValue(RAttribute.TYPE));
    }

    /**
     * 
     * @return null or supported ProcessdescriptionType
     * @throws RAnnotationException
     */
    public String getProcessDescriptionType() throws RAnnotationException {
        String type = getStringValue(RAttribute.TYPE);
        RTypeDefinition rdt = RDataType.getType(type);
        if (rdt != null)
            return rdt.getProcessKey();
        else
            return null;

    }

    static HashMap<String, RDataType> rDataTypeKeys = new HashMap<String, RDataType>();

    @Override
    public String toString() {
        return "RAnnotation [" + this.type + "][" + Arrays.toString(this.attributeHash.entrySet().toArray()) + "]";
    }

	public boolean containsKey(RAttribute key) {
		return attributeHash.containsKey(key);
	}

	public void setAttribute(RAttribute key, Object value) {
		attributeHash.put(key, value);
		
	}
}
