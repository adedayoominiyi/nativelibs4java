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
import java.nio.CharBuffer;
import org.bridj.ann.*;
import org.junit.Test;

import static org.bridj.Pointer.*;
import static org.junit.Assert.*;

public class MiscBugsTest {
  
	static {
		BridJ.register();
	}
	
	@Library("test")
	@Optional
	public static native void whatever(SizeT v);
	
	@Library("test")
	public static native Pointer<Character> getSomeWString();
		
    /** 
	 * Issue 295 : wchar_t broken on MacOS X
	 * https://github.com/ochafik/nativelibs4java/issues/295
	 */
	@Test 
	public void testWideCharReadout() {
		assertEquals("1234567890", getSomeWString().getWideCString());	
	}
	
	/** 
	 * Issue 68 : simple SizeT calls are broken
	 * http://code.google.com/p/nativelibs4java/issues/detail?id=68
	 */
	@Test(expected = UnsatisfiedLinkError.class) 
	public void testSizeTArgs() {
		whatever(new SizeT(1));	
	}
	
	/**
	 * Issue 37 : BridJ: org.bridj.Pointer#iterator for native-allocated pointers is empty
	 * http://code.google.com/p/nativelibs4java/issues/detail?id=37
	 */
	@Test
	public void emptyIteratorFromUnmanagedPointer() {
		Pointer<Byte> ptr = allocateBytes(10);
		assertTrue(!ptr.asList().isEmpty());
		assertTrue(ptr.iterator().next() != null);
		
		Pointer<Byte> unmanaged = pointerToAddress(ptr.getPeer()).as(Byte.class);
		assertTrue(!unmanaged.asList().isEmpty());
		assertTrue(unmanaged.iterator().next() != null);
	}
	
	/**
	 * Issue 47: Pointer#pointerToAddress(long, Class, Releaser) does not use releaser argument
	 * http://code.google.com/p/nativelibs4java/issues/detail?id=37
	 */
	@Test
	public void usePointerReleaser() {
		final boolean[] released = new boolean[1];
		Pointer<Integer> p = allocateInt();
		long address = p.getPeer();
		
		{
			Pointer pp = pointerToAddress(address);
			assertEquals(address, pp.getPeer());
		}
		
		{
			Pointer pp = pointerToAddress(address, 123);
			assertEquals(address, pp.getPeer());
			assertEquals(123, pp.getValidBytes());
		}
		
		Releaser releaser = new Releaser() {
			//@Override
			public void release(Pointer<?> p) {
				released[0] = true;
			}
		};
		
		{
			released[0] = false;
			Pointer pp = pointerToAddress(address, Integer.class, releaser);
			assertEquals(address, pp.getPeer());
			assertEquals(Integer.class, pp.getTargetType());
			pp.release();
			assertEquals(true, released[0]);
		}
		
		{
			Pointer pp = pointerToAddress(address, PointerIO.getIntInstance());
			assertEquals(address, pp.getPeer());
			assertEquals(Integer.class, pp.getTargetType());
		}
		
		{
			released[0] = false;
			Pointer pp = pointerToAddress(address, PointerIO.getIntInstance(), releaser);
			assertEquals(address, pp.getPeer());
			assertEquals(Integer.class, pp.getTargetType());
			pp.release();
			assertEquals(true, released[0]);
		}
		
		{
			released[0] = false;
			Pointer pp = pointerToAddress(address, releaser);
			assertEquals(address, pp.getPeer());
			pp.release();
			assertEquals(true, released[0]);
		}
		
		{
			released[0] = false;
			Pointer pp = pointerToAddress(address, 123, releaser);
			assertEquals(address, pp.getPeer());
			assertEquals(123, pp.getValidBytes());
			pp.release();
			assertEquals(true, released[0]);
		}
		
		{
			Pointer pp = pointerToAddress(address, Integer.class);
			assertEquals(address, pp.getPeer());
			assertEquals(Integer.class, pp.getTargetType());
		}
		
		{
			Pointer pp = pointerToAddress(address, 123, PointerIO.getIntInstance());
			assertEquals(address, pp.getPeer());
			assertEquals(123, pp.getValidBytes());
			assertEquals(Integer.class, pp.getTargetType());
		}
		
	}
	
	public class VIDEOHDR extends StructObject
	{
	   // fields
	   /**
		* Pointer to locked data buffer.
		*/
	   @Field(0)
	   public Pointer lpData;
	   /**
		* Length of data buffer.
		*/
	   @Field(1)
	   public int dwBufferLength;
	   /**
		* Bytes actually used.
		*/
	   @Field(2)
	   public int dwBytesUsed;
	   /**
		* Milliseconds from start of stream.
		*/
	   @Field(3)
	   public int dwTimeCaptured;
	   /**
		* User-defined data.
		*/
	   @Field(4)
	   public int dwUser;
	   /**
		* The flags are defined as follows:
		* <br> VHDR_DONE - Done bit
		* <br> VHDR_PREPARED - Set if this header has been prepared
		* <br> VHDR_INQUEUE - Reserved for driver
		* <br> VHDR_KEYFRAME - Key Frame
		*/
	   @Field(5)
	   public int dwFlags;
	   /**
		* Reserved for driver.
		*/
	   @Field(6)
	   @Array(4)
	   public Pointer<Pointer<Integer>> dwReserved;
	
	   /**
		* Constructor.
		*/
	   public VIDEOHDR()
	   {
	   }
	
	   /**
		* Constructor.
		*
		* @param ptr
		*/
	   public VIDEOHDR(Pointer<? extends StructObject> ptr)
	   {
		   super(ptr);
	   }
	}
	/**
	 * https://github.com/ochafik/nativelibs4java/issues/56#issuecomment-2075496
	 */
	@Test
	public void javaFieldsStructArrayFieldsVsSignedIntegrals() {
		new VIDEOHDR();
	}
    
    static class v4l2_fmtdesc extends StructObject {
        @Field(0)
        public int index;
        @Field(1)
        public int type;
        @Field(2)
        public int flags;
        @Field(3)
        @Array(32)
        public Pointer<Byte> description;
        @Field(4)
        public int pixelformat;
        @Field(5)
        @Array(4)
        public Pointer<Integer> reserved;
    }
    @Union
    static class fmt extends StructObject {
        @Field(0)
        public v4l2_fmtdesc pix;

        @Field(1)
        @Array(200)
        public Pointer<Byte> raw_data;
    }
    static class v4l2_format extends StructObject {
        @Field(0)
        public int type;
        @Field(1)
        public fmt fmt;
    }
    /**
	 * https://github.com/ochafik/nativelibs4java/issues/200
	 */
	@Test
    public void testUnionRegression() {
        new v4l2_fmtdesc();
        new fmt();
        new v4l2_format();
    }
}
