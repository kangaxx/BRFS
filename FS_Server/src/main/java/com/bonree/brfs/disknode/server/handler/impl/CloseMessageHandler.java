package com.bonree.brfs.disknode.server.handler.impl;

import java.io.IOException;

import com.bonree.brfs.disknode.DiskWriterManager;
import com.bonree.brfs.disknode.server.handler.DiskMessage;
import com.bonree.brfs.disknode.server.handler.HandleResult;
import com.bonree.brfs.disknode.server.handler.HandleResultCallback;
import com.bonree.brfs.disknode.server.netty.MessageHandler;

public class CloseMessageHandler implements MessageHandler<DiskMessage> {
	private DiskWriterManager nodeManager;
	
	public CloseMessageHandler(DiskWriterManager nodeManager) {
		this.nodeManager = nodeManager;
	}

	@Override
	public void handle(DiskMessage msg, HandleResultCallback callback) {
		HandleResult result = new HandleResult();
		try {
			nodeManager.close(msg.getFilePath());
			result.setSuccess(true);
		} catch (IOException e) {
			result.setSuccess(false);
			result.setCause(e);
		} finally {
			callback.completed(result);
		}
	}

}