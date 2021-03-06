/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Stefan Irimescu
 *
 */
 package sparksoniq.jsoniq.runtime.iterator;

import sparksoniq.jsoniq.item.Item;
import sparksoniq.jsoniq.runtime.metadata.IteratorMetadata;
import sparksoniq.semantics.DynamicContext;
import sparksoniq.exceptions.IteratorFlowException;

import java.util.List;

public class CommaExpressionIterator extends LocalRuntimeIterator {

    public CommaExpressionIterator(List<RuntimeIterator> childIterators, IteratorMetadata iteratorMetadata) {
        super(childIterators, iteratorMetadata);
    }

    @Override
    public Item next() {
        if(currentChild == null) {
            currentChild = this._children.get(0);
            this._children.forEach(c -> c.reset(this._currentDynamicContext));
            currentChild.open(_currentDynamicContext);
        }
        if(currentChild.hasNext())
            return currentChild.next();
        else
        {
            currentChild.close();
            if(this._children.indexOf(currentChild) == this._children.size() - 1)
                throw new IteratorFlowException("Invalid next() call in Comma expression", getMetadata());
            currentChild = this._children.get(_children.indexOf(currentChild) + 1);
            currentChild.open(_currentDynamicContext);
            return currentChild.next();
        }
    }

    @Override
    public void open(DynamicContext context){
        super.open(context);
        currentChild = null;
    }

    @Override
    public void reset(DynamicContext context){
        super.reset(context);
        currentChild = null;
    }

    @Override
    public boolean hasNext(){
        return currentChild == null ||
                this._children.indexOf(currentChild) < this._children.size() - 1 ||
                currentChild.hasNext();
    }

    private RuntimeIterator currentChild = null;
}
