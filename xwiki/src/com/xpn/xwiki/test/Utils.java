
package com.xpn.xwiki.test;

import com.xpn.xwiki.store.XWikiStoreInterface;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiSimpleDoc;
import com.xpn.xwiki.objects.*;
import com.xpn.xwiki.objects.classes.*;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

import junit.framework.TestCase;

/**
 * ===================================================================
 *
 * Copyright (c) 2003 Ludovic Dubost, All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details, published at 
 * http://www.gnu.org/copyleft/lesser.html or in lesser.txt in the
 * root folder of this distribution.

 * Created by
 * User: Ludovic Dubost
 * Date: 27 janv. 2004
 * Time: 15:20:13
 */

public class Utils {
    public static String name = "WebHome";
    public static String name2 = "Globals";
    public static String web = "Main";
    public static String content1 = "Hello 1\n<Hello> 2\nHello 3\n";
    public static String content3 = "Hello 1\nIntermediary line\nHello 2\nHello 3\n";
    public static String author = "VictorHugo";
    public static String author2 = "JulesVerne";
    public static String parent = "Main.WebHome";
    public static String version = "1.1";
    public static String version2 = "1.2";


    public static String rcspath = "./rcs";


    public static void setStandardData() {
        name = "WebHome";
        name2 = "Globals";
        web = "Main";
        content1 = "Hello 1\nHello 2\nHello 3\n";
        content3 = "Hello 1\nIntermediary line\nHello 2\nHello 3\n";
        author = "VirtorHugo";
        author2 = "JulesVerne";
        parent = "Main.WebHome";
        version = "1.1";
        version2 = "1.2";
    }

    public static void setMediumData() {

        setStandardData();

        while (content1.length()<1000)
                content1 += content1;


        while (author.length()<120)
                author += author;
        while (content3.length()<1000)
                content3 += content3;
    }

    public static String getData(File file) throws IOException {
        StringBuffer content = new StringBuffer();
        BufferedReader fr = new BufferedReader(new FileReader(file));
        String line;
        while ((line = fr.readLine())!=null) {
            content.append(line);
            content.append("\n");
        }
        fr.close();
        return content.toString();
    }

    public static void setBigData() throws IOException {
        setStandardData();
        while (author.length()<120)
                author += author;

        File file1 = new File(rcspath + "/" + web + "/" + name2 + ".txt.1");
        File file3 = new File(rcspath + "/" + web + "/" + name2 + ".txt.2");
        content1 = getData(file1);
        content3 = getData(file3);
    }


    public static void createDoc(XWikiStoreInterface store, String web, String name) throws XWikiException {
        createDoc(store,web, name, null, null, null);
    }

    public static void createDoc(XWikiStoreInterface store, String web, String name,
                          BaseObject bobject, BaseClass bclass) throws XWikiException {
        createDoc(store,web, name, bobject, bclass, null);

    }

    public static void createDoc(XWikiStoreInterface store, String web, String name,
                          BaseObject bobject, BaseClass bclass,
                          Map bobjects) throws XWikiException {
        XWikiSimpleDoc doc1 = new XWikiSimpleDoc(web, name);
        doc1.setContent(Utils.content1);
        doc1.setAuthor(Utils.author);
        doc1.setParent(Utils.web + "." + Utils.name);

        if (bobjects!=null)
            doc1.setxWikiObjects(bobjects);

        if (bobject!=null)
            doc1.setObject(doc1.getFullName(), 0, bobject);

        if (bclass!=null)
            doc1.setxWikiClass(bclass);

        store.saveXWikiDoc(doc1);
    }

       public static BaseObject prepareObject() throws XWikiException {
        return prepareObject("Test.TestObject");
       }

       public static BaseObject prepareObject(String name) throws XWikiException {
         BaseClass wclass = new BaseClass();
         StringClass first_name_class = new StringClass();
         first_name_class.setName("first_name");
         first_name_class.setPrettyName("First Name");
         first_name_class.setSize(80);
         first_name_class.setObject(wclass);
         StringClass last_name_class = new StringClass();
         last_name_class.setName("last_name");
         last_name_class.setPrettyName("Last Name");
         last_name_class.setSize(80);
         last_name_class.setObject(wclass);
         NumberClass age_class = new NumberClass();
         age_class.setName("age");
         age_class.setPrettyName("Age");
         age_class.setSize(5);
         age_class.setNumberType("integer");
         age_class.setObject(wclass);
         PasswordClass passwd_class = new PasswordClass();
         passwd_class.setName("password");
         passwd_class.setPrettyName("Password");
         passwd_class.setSize(10);
         passwd_class.setObject(wclass);
         TextAreaClass comment_class = new TextAreaClass();
         comment_class.setName("comment");
         comment_class.setPrettyName("Comment");
         comment_class.setSize(80);
         comment_class.setRows(10);
         comment_class.setObject(wclass);

         wclass.put("first_name", first_name_class);
         wclass.put("last_name", last_name_class);
         wclass.put("age", age_class);
         wclass.put("password", passwd_class);
         wclass.put("comment", comment_class);
         wclass.setName(name);

         BaseObject object = new BaseObject();
         object.setxWikiClass(wclass);
         object.setName(name);
         object.put("first_name", first_name_class.fromString("Ludovic"));
         object.put("last_name", last_name_class.fromString("Von Dubost"));
         object.put("age", age_class.fromString("33"));
         object.put("password", passwd_class.fromString("sesame"));
         object.put("comment",comment_class.fromString("Hello1\nHello2\nHello3\n"));
         return object;
     }

    public static IntegerProperty prepareIntegerProperty() {
         BaseObject object = new BaseObject();
         object.setName("Main.TestObject1");
         IntegerProperty prop = new IntegerProperty();
         prop.setObject(object);
         prop.setPrettyName("Age");
         prop.setName("age");
         prop.setValue(new Integer(34));
         return prop;
     }

    public static StringProperty prepareStringProperty() {
        BaseObject object = new BaseObject();
        object.setName("Main.TestObject1");
        StringProperty prop = new StringProperty();
        prop.setObject(object);
        prop.setName("name");
        prop.setPrettyName("Name");
        prop.setValue(new String("Dubost"));
        return prop;
    }    

    public static void assertEquals(XWikiSimpleDoc doc1, XWikiSimpleDoc doc2) {
        TestCase.assertEquals("Name is different", doc1.getName(), doc2.getName());
        TestCase.assertEquals("Web is different", doc1.getWeb(), doc2.getWeb());
        TestCase.assertEquals("Author is different", doc1.getAuthor(), doc2.getAuthor());
        TestCase.assertEquals("Date is different", doc1.getDate().getTime(), doc2.getDate().getTime());
        TestCase.assertEquals("Format is different", doc1.getFormat(), doc2.getFormat());
        TestCase.assertEquals("Version is different", doc1.getVersion(), doc2.getVersion());
        TestCase.assertEquals("Content is different", doc1.getContent(), doc2.getContent());
        TestCase.assertEquals("xWikiClass is different", doc1.getxWikiClass(), doc2.getxWikiClass());

        Set list1 = doc1.getxWikiObjects().keySet();
        Set list2 = doc2.getxWikiObjects().keySet();
        TestCase.assertEquals("Object list is different", list1, list2);

        for (Iterator it = list1.iterator();it.hasNext();) {
            String name = (String) it.next();
            Vector v1 = doc1.getObjects(name);
            Vector v2 = doc2.getObjects(name);
            TestCase.assertEquals("Object number for " + name + " is different", v1.size(), v2.size());

            for (int i=0;i<v1.size();i++) {
                TestCase.assertEquals("Object " + i + " for " + name + " is different", v1.get(i), v2.get(i));
            }
        }
    }

    public static void assertProperty(BaseCollection object1, BaseCollection object2, String propname) {
        BaseElement prop1 = (BaseElement)object1.safeget(propname);
        BaseElement prop2 = (BaseElement)object2.safeget(propname);
        TestCase.assertTrue("Property " + propname + " is different (" + prop1.getName() + "," + prop2.getName() + ")",
                prop1.equals(prop2));
    }

}
