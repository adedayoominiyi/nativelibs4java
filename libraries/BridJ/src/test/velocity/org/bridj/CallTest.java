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
package org.bridj;
import static org.bridj.Pointer.*;
import org.bridj.ann.*;
//import static org.bridj.Functional.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

@Library("test")
@org.bridj.ann.Runtime(CRuntime.class)
public class CallTest {

	public CallTest() {
		BridJ.register(CallTest.class);
	}
	
	/// Returns value + 1
	public static native TimeT test_incr_timet(TimeT value);
	public static native SizeT test_incr_sizet(SizeT value);
	public static native CLong test_incr_clong(CLong value);

	@Test
	public void testTimeTSize() {
		assertTrue(TimeT.SIZE == 4 || TimeT.SIZE == 8);
	}
	@Test
	public void testIncrementTimeT() {
		assertEquals(11, test_incr_timet(new TimeT(10)).longValue());	
	}
	@Test
	public void testIncrementSizeT() {
		assertEquals(11, test_incr_sizet(new SizeT(10)).longValue());	
	}
	@Test
	public void testIncrementCLong() {
		assertEquals(11, test_incr_clong(new CLong(10)).longValue());	
	}
	
#foreach ($prim in $primitivesNoBool)

	/// Returns value + 1
	public static native ${prim.Name} test_incr_${prim.Name}(${prim.Name} value);

	/// Returns value + 1 in pointed value
	public static native void test_incr_${prim.Name}_out(${prim.Name} value, Pointer<${prim.WrapperName}> out);

	@Test
	public void testIncrement${prim.CapName}() {
		Pointer<${prim.WrapperName}> out = allocate${prim.CapName}();
		for (${prim.Name} value : new ${prim.Name}[] { (${prim.Name})0, (${prim.Name})1, (${prim.Name})-1 }) {
			${prim.Name} ret = test_incr_${prim.Name}(value);
			${prim.Name} incr = (${prim.Name})(value + 1);
			assertEquals(incr, ret#if($prim.Name == "float" || $prim.Name == "double"), 0#end);
			
			test_incr_${prim.Name}_out(value, out);
			ret = out.get();
			assertEquals(incr, ret#if($prim.Name == "float" || $prim.Name == "double"), 0#end);
		}
	}
	
	public static abstract class MyCallback_${prim.Name} extends Callback {
		public abstract ${prim.Name} apply(${prim.Name} value);
	}
	///*
	/// Returns cb.apply(value)
	//public static native ${prim.Name} test_callback_${prim.Name}_${prim.Name}(Func1<${prim.WrapperName}, ${prim.WrapperName}> cb, ${prim.Name} value);
	public static native ${prim.Name} test_callback_${prim.Name}_${prim.Name}(Pointer<MyCallback_${prim.Name}> cb, ${prim.Name} value);
	
	@Test
	public void testCallback_${prim.Name}() {
		MyCallback_${prim.Name} cb = new MyCallback_${prim.Name}() {
			public ${prim.Name} apply(${prim.Name} value) {
				return (${prim.Name})(value + 1);
			}
		};
		/*
		Func1<${prim.WrapperName}, ${prim.WrapperName}> cb = new Func1<${prim.WrapperName}, ${prim.WrapperName}>() {
			public ${prim.Name} apply(${prim.Name} value) {
				return (${prim.Name})(value + 1);
			}
		};*/
		for (${prim.Name} value : new ${prim.Name}[] { (${prim.Name})0, (${prim.Name})1, (${prim.Name})-1 }) {
			${prim.Name} ret = test_callback_${prim.Name}_${prim.Name}(cb.toPointer(), value);
			${prim.Name} incr = (${prim.Name})(value + 1);
			assertEquals(incr, ret#if($prim.Name == "float" || $prim.Name == "double"), 0#end);
		}
	}
	//*/

	
#foreach ($n in [2, 9, 20])
	public static native ${prim.Name} test_add${n}_${prim.Name}(#foreach ($i in [1..$n])#if($i > 1), #end${prim.Name} arg$i#end);
	public static native void test_add${n}_${prim.Name}_out(#foreach ($i in [1..$n])#if($i > 1), #end${prim.Name} arg$i#end, Pointer<${prim.WrapperName}> out);
	
	@Test
	public void testAdd${n}${prim.CapName}() {
		Pointer<${prim.WrapperName}> out = allocate${prim.CapName}();
		
		${prim.Name} expectedTot = (${prim.Name})0;
		${prim.Name} fact = (${prim.Name})2;
#foreach ($i in [1..$n])
		${prim.Name} arg$i = (${prim.Name})(fact * ($i + 1));
		fact *= (${prim.Name})2;
		expectedTot += arg$i;
#end
		${prim.Name} tot = test_add${n}_${prim.Name}(#foreach ($i in [1..$n])#if($i > 1),#end arg$i#end);
		assertEquals(expectedTot, tot#if($prim.Name == "float" || $prim.Name == "double"), 0#end);
		
		test_add${n}_${prim.Name}_out(#foreach ($i in [1..$n])#if($i > 1),#end arg$i#end, out);
		tot = out.get();
		assertEquals(expectedTot, tot#if($prim.Name == "float" || $prim.Name == "double"), 0#end);
	}
#end


#end
}
