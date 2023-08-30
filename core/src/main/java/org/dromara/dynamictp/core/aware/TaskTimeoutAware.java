/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.dynamictp.core.aware;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * TaskTimeoutAware related
 *
 * @author kyao
 * @since 1.1.4
 */
@Slf4j
public class TaskTimeoutAware extends TaskStatAware {

    @Override
    public int getOrder() {
        return AwareTypeEnum.TASK_TIMEOUT_AWARE.getOrder();
    }

    @Override
    public String getName() {
        return AwareTypeEnum.TASK_TIMEOUT_AWARE.getName();
    }

    @Override
    public void execute(Executor executor, Runnable r) {
        Optional.ofNullable(statProviders.get(executor)).ifPresent(p -> p.startQueueTimeoutTask(r));
    }

    @Override
    public void beforeExecute(Executor executor, Thread t, Runnable r) {
        Optional.ofNullable(statProviders.get(executor)).ifPresent(p -> {
            p.cancelQueueTimeoutTask(r);
            p.startRunTimeoutTask(t, r);
        });
    }

    @Override
    public void afterExecute(Executor executor, Runnable r, Throwable t) {
        Optional.ofNullable(statProviders.get(executor)).ifPresent(p -> p.cancelRunTimeoutTask(r));
    }

    @Override
    public void beforeReject(Runnable r, Executor executor, Logger log) {
        Optional.ofNullable(statProviders.get(executor)).ifPresent(p -> p.cancelQueueTimeoutTask(r));
    }
}
