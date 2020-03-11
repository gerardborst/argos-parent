/*
 * Copyright (C) 2019 - 2020 Rabobank Nederland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rabobank.argos.yaml;

import org.apache.commons.lang3.StringUtils;

public class RefUtil {
    public static final String REF = "$ref";
    public static final String SINGLE_QUOTE = "'";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String HASH_SLASH = "#/";
    public static final String COLON = ":";
    public static final String DOT = ".";

    private RefUtil() {
    }

    /**
     * This checks if the line without indentation starts with $ref
     *
     * @param line
     * @return
     */
    public static boolean isStartWithRef(String line) {
        return StringUtils.trim(line).startsWith(REF);
    }

    /**
     * This returns the indentation of the $ref line.
     *
     * @param line
     * @return
     */
    public static String getIndentation(String line) {
        return StringUtils.substringBefore(line, REF);
    }

    /**
     * this method checks if the line is remote path or local path
     * remote path starts with ./ and local path starts with #
     * path could have single or double quotes also.
     * ex
     * remote path
     * $ref: './info/index.yaml'
     * <p>
     * local path
     * $ref: '#/definitions/Info'
     *
     * @param refLine
     * @return true if it is remote path
     */
    public static boolean isRemoteFilepath(String refLine) {
        String refPath = StringUtils.substringAfter(refLine, COLON);
        String refPathWithoutQuotes = removeQuotes(refPath);
        return !StringUtils.startsWith(refPathWithoutQuotes, HASH_SLASH);

    }

    /**
     * Ref path example could be
     * $ref: './baseobject.yaml'
     * $ref: "/baseobject.yaml"
     * $ref: /baseobject.yaml
     *
     * @param refPathValue
     * @return
     */
    private static String removeQuotes(String refPathValue) {
        String ret;
        String trimRefValue = StringUtils.trim(refPathValue);
        if (StringUtils.startsWith(trimRefValue, SINGLE_QUOTE)) {
            //get the string between "'"
            ret = StringUtils.substringBetween(trimRefValue, SINGLE_QUOTE);
        } else if (StringUtils.startsWith(trimRefValue, DOUBLE_QUOTE)) {
            ret = StringUtils.substringBetween(trimRefValue, DOUBLE_QUOTE);
        } else {
            ret = trimRefValue;
        }

        return ret;
    }


    //Get the remote
    public static String getRemoteRelativeFilePath(String refLine) {
        String relFileString;
        String refPath = StringUtils.substringAfter(refLine, COLON);
        relFileString = removeQuotes(refPath);
        if (StringUtils.startsWith(relFileString, DOT)) {
            relFileString = StringUtils.stripStart(relFileString, DOT);
        }

        return relFileString;
    }


}
