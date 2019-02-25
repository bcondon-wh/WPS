/*
 * Copyright (C) 2007-2018 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.wps.io.data.binding.literal;

import java.io.IOException;

public class LiteralLongBinding extends AbstractLiteralDataBinding {
    /**
     *
     */
    private static final long serialVersionUID = 8751599940746613501L;

    private transient Long payload;

    public LiteralLongBinding(Long payload) {
        this.payload = payload;
    }

    public Long getPayload() {
        return payload;
    }

    public Class<Long> getSupportedClass() {
        return Long.class;
    }

    private synchronized void writeObject(java.io.ObjectOutputStream oos) throws IOException {
        oos.writeObject(payload.toString());
    }

    private synchronized void readObject(java.io.ObjectInputStream oos) throws IOException, ClassNotFoundException {
        payload = new Long((String) oos.readObject());
    }
}
