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

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.bridj.NativeConstants.ValueType;

class EllipsisHelper {

    static ThreadLocal<IntBuffer[]> holders = new ThreadLocal<IntBuffer[]>() {
        protected IntBuffer[] initialValue() {
            return new IntBuffer[1];
        }
    };

    public static IntBuffer unrollEllipsis(Object[] args) {
        IntBuffer[] holder = holders.get();
        int n = args.length;
        IntBuffer buf = holder[0];
        if (buf == null || buf.capacity() < n) {
            buf = holder[0] = ByteBuffer.allocateDirect(n * 4).asIntBuffer();
        }
        for (int i = 0; i < n; i++) {
            Object arg = args[i];
            ValueType type;
            if (arg == null || arg instanceof Pointer<?>) {
                type = ValueType.ePointerValue;
            } else if (arg instanceof Integer) {
                type = ValueType.eIntValue;
            } else if (arg instanceof Long) {
                type = ValueType.eLongValue;
            } else if (arg instanceof Short) {
                type = ValueType.eShortValue;
            } else if (arg instanceof Double) {
                type = ValueType.eDoubleValue;
            } else if (arg instanceof Float) {
                type = ValueType.eFloatValue;
            } else if (arg instanceof Byte) {
                type = ValueType.eByteValue;
            } else if (arg instanceof Boolean) {
                type = ValueType.eBooleanValue;
            } else if (arg instanceof Character) {
                type = ValueType.eWCharValue;
            } else if (arg instanceof SizeT) {
                type = ValueType.eSizeTValue;
                args[i] = arg = ((SizeT) arg).longValue();
            } else if (arg instanceof CLong) {
                type = ValueType.eCLongValue;
                args[i] = arg = ((CLong) arg).longValue();
            } else if (arg instanceof NativeObject) {
                type = ValueType.eNativeObjectValue;
            } else {
                throw new IllegalArgumentException("Argument type not handled in variable argument calls  : " + arg + " (" + arg.getClass().getName() + ")");
            }

            buf.put(i, type.ordinal());
        }
        return buf;
    }
}
