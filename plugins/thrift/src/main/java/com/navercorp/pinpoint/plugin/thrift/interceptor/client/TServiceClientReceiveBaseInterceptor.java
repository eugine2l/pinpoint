/*
 * Copyright 2015 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.plugin.thrift.interceptor.client;

import org.apache.thrift.TBase;

import com.navercorp.pinpoint.bootstrap.context.RecordableTrace;
import com.navercorp.pinpoint.bootstrap.interceptor.SpanEventSimpleAroundInterceptor;
import com.navercorp.pinpoint.bootstrap.util.StringUtils;
import com.navercorp.pinpoint.plugin.thrift.ThriftConstants;

/**
 * This interceptor records the response received from the server for synchronous client calls.
 * <p>
 * Based on Thrift 0.8.0+
 * 
 * @author HyunGil Jeong
 */
public class TServiceClientReceiveBaseInterceptor extends SpanEventSimpleAroundInterceptor implements ThriftConstants {

    private final boolean traceServiceResult;
    
    public TServiceClientReceiveBaseInterceptor(
            boolean traceServiceResult) {
        super(TServiceClientReceiveBaseInterceptor.class);
        this.traceServiceResult = traceServiceResult;
    }
    
    @Override
    protected void doInBeforeTrace(RecordableTrace trace, Object target, Object[] args) {
        trace.markBeforeTime();
        trace.recordServiceType(THRIFT_CLIENT_INTERNAL);
    }
    
    @Override
    protected void doInAfterTrace(RecordableTrace trace, Object target, Object[] args, Object result, Throwable throwable) {
        trace.recordApi(getMethodDescriptor());
        if (throwable == null && this.traceServiceResult) {
            if (args.length == 2 && (args[0] instanceof TBase)) {
                String resultString = getResult((TBase<?, ?>)args[0]);
                trace.recordAttribute(THRIFT_RESULT, resultString);
            }
        } else {
            trace.recordException(throwable);
        }
        trace.markAfterTime();
    }

    private String getResult(TBase<?, ?> args) {
        return StringUtils.drop(args.toString(), 256);
    }

}
