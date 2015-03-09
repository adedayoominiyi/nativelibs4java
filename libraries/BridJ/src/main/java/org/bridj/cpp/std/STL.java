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
package org.bridj.cpp.std;

import org.bridj.*;

///http://www.codesourcery.com/public/cxx-abi/cxx-vtable-ex.html
/**
 * Util methods for STL bindings in BridJ, <i>intended for internal use
 * only</i>.
 *
 * @author ochafik
 */
public final class STL extends StructCustomizer {

    /**
     * Perform platform-dependent structure bindings adjustments
     */
    @Override
    public void afterBuild(StructDescription desc) {
        if (!Platform.isWindows()) {
            return;
        }

        Class c = desc.getStructClass();
        if (c == vector.class) {
            // On Windows, vector begins by 3 pointers, before the start+finish+end pointers :
            desc.prependBytes(3 * Pointer.SIZE);
        } else if (c == list.class || c == list.list_node.class) {
            desc.setFieldOffset("prev", 5 * Pointer.SIZE, false);
            if (c == list.list_node.class) {
                desc.setFieldOffset("data", 6 * Pointer.SIZE, false);
            }
        }
    }
}
