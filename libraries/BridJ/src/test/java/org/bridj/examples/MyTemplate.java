/*
 * BridJ - Dynamic and blazing-fast native interop for Java.
 * http://bridj.googlecode.com/
 *
 * Copyright (c) 2010-2015, Olivier Chafik (http://ochafik.com/)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Olivier Chafik nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY OLIVIER CHAFIK AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.bridj.examples;
import org.bridj.ann.*;
import org.bridj.cpp.*;
import org.bridj.*;
import java.lang.reflect.Type;
import java.nio.*;
import java.util.*;

/**

mvn package -DskipTests=true -o && java -cp target/bridj-0.4-SNAPSHOT-shaded.jar org.bridj.examples.MyTemplate

template <int n, typename T>
class MyTemplate {
public:
	MyTemplate(int arg);
	T someMethod();
}
 
 */ 
@Template({ Integer.class, Class.class })
public class MyTemplate<T> extends CPPObject {
    static {
		BridJ.register();
	}
	
	public final int n;
    
	@Constructor(0)
	public MyTemplate(int n, Type t, int arg) {
		super((Void)null, 0, n, t, arg);
		this.n = n;
	}
	
	public native T someMethod();

    public static void main(String[] args) throws CloneNotSupportedException {
    		Type cppt = CPPType.getCPPType(new Object[] { MyTemplate.class, 10, String.class });
    		System.out.println("type = " + cppt);
        MyTemplate<String> t = new MyTemplate<String>(10, String.class, 4);
        System.out.println(t);
        MyTemplate<String> nt = (MyTemplate<String>) t.clone();
        System.out.println(nt);
    }
}
