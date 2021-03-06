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
 */

package org.apache.flink.streaming.connectors.cassandra;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.flink.configuration.Configuration;

/**
 * Flink Sink to save data into a Cassandra cluster using 
 * <a href="http://docs.datastax.com/en/drivers/java/2.1/com/datastax/driver/mapping/Mapper.html">Mapper</a>,
 * which it uses annotations from
 * <a href="http://docs.datastax.com/en/drivers/java/2.1/com/datastax/driver/mapping/annotations/package-summary.html">
 * com.datastax.driver.mapping.annotations</a>.
 *
 * @param <IN> Type of the elements emitted by this sink
 */
public class CassandraPojoSink<IN> extends CassandraSinkBase<IN, Void> {

	private static final long serialVersionUID = 1L;

	protected final Class<IN> clazz;
	protected transient Mapper<IN> mapper;
	protected transient MappingManager mappingManager;

	/**
	 * The main constructor for creating CassandraPojoSink
	 *
	 * @param clazz Class<IN> instance
	 */
	public CassandraPojoSink(Class<IN> clazz, ClusterBuilder builder) {
		super(builder);
		this.clazz = clazz;
	}

	@Override
	public void open(Configuration configuration) {
		super.open(configuration);
		try {
			this.mappingManager = new MappingManager(session);
			this.mapper = mappingManager.mapper(clazz);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create CassandraPojoSink with input: " + clazz.getSimpleName(), e);
		}
	}

	@Override
	public ListenableFuture<Void> send(IN value) {
		return mapper.saveAsync(value);
	}
}
