/*******************************************************************************
 * Copyright 2012 Apigee Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.usergrid.mongo.commands;

import static org.usergrid.utils.MapUtils.entry;
import static org.usergrid.utils.MapUtils.map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.usergrid.management.ApplicationInfo;
import org.usergrid.mongo.MongoChannelHandler;
import org.usergrid.mongo.protocol.OpQuery;
import org.usergrid.mongo.protocol.OpReply;
import org.usergrid.persistence.EntityManager;
import org.usergrid.persistence.Identifier;
import org.usergrid.security.shiro.utils.SubjectUtils;

public class Collstats extends MongoCommand {

	@Override
	public OpReply execute(MongoChannelHandler handler,
			ChannelHandlerContext ctx, MessageEvent e, OpQuery opQuery) {
		ApplicationInfo application = SubjectUtils.getApplication(Identifier
				.fromName(opQuery.getDatabaseName()));
		OpReply reply = new OpReply(opQuery);
		if (application == null) {
			return reply;
		}
		EntityManager em = handler.getEmf().getEntityManager(
				application.getId());
		String collectionName = (String) opQuery.getQuery().get("collstats");
		long count = 0;
		try {
			count = em.getApplicationCollectionSize(collectionName);
		} catch (Exception e1) {
		}
		reply.addDocument(map(
				entry("ns", opQuery.getDatabaseName() + "." + collectionName),
				entry("count", count), entry("size", count * 40),
				entry("avgObjSize", 40.0), entry("storageSize", 8192),
				entry("numExtents", 1), entry("nindexes", 1),
				entry("lastExtentSize", 8192), entry("paddingFactor", 1.0),
				entry("flags", 1), entry("totalIndexSize", 8192),
				entry("indexSizes", map("_id_", 8192)), entry("ok", 1.0)));
		return reply;
	}

}
