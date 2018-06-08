package com.bonree.brfs.duplication.datastream.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonree.brfs.common.asynctask.AsyncTask;
import com.bonree.brfs.disknode.client.DiskNodeClient;
import com.bonree.brfs.disknode.server.handler.data.WriteData;
import com.bonree.brfs.disknode.server.handler.data.WriteResult;
import com.bonree.brfs.duplication.datastream.connection.DiskNodeConnection;

public class DataWriteTask extends AsyncTask<WriteResult[]> {
	private static final Logger LOG = LoggerFactory.getLogger(DataWriteTask.class);
	
	private DiskNodeConnection connection;
	private String filePath;
	private WriteData[] datas;
	
	public DataWriteTask(String path, DiskNodeConnection connection, WriteData[] datas) {
		this.connection = connection;
		this.filePath = path;
		this.datas = datas;
	}

	@Override
	public WriteResult[] run() throws Exception {
		long start = System.nanoTime();
		try {
			if(connection == null) {
				LOG.error("file[{}] connection is null!!!", filePath);
				return null;
			}
			
			DiskNodeClient client = connection.getClient();
			if(client == null) {
				LOG.error("file[{}] DiskNodeClient is null!!!", filePath);
				return null;
			}
			
			LOG.info("write {} data to {}:{}", filePath, connection.getRemoteAddress(), connection.getRemotePort());
			WriteResult[] result = client.writeDatas(filePath, datas);
			
			LOG.info("file[{}] write task from {}:{} get result---{}", filePath,
					connection.getRemoteAddress(), connection.getRemotePort(), result);
			
			return result;
		} finally {
			LOG.info("take##############{}", (System.nanoTime() - start) / 1000000d);
		}
	}
	
}