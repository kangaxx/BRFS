package com.br.disknode.server.handler.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.br.disknode.DiskWriterManager;
import com.br.disknode.InputEventCallback;
import com.br.disknode.InputResult;
import com.br.disknode.client.WriteResult;
import com.br.disknode.server.handler.DiskMessage;
import com.br.disknode.server.handler.HandleResult;
import com.br.disknode.server.handler.HandleResultCallback;
import com.br.disknode.server.netty.MessageHandler;
import com.br.disknode.utils.ProtoStuffUtils;
import com.br.disknode.utils.StringUtils;

public class WriteMessageHandler implements MessageHandler<DiskMessage> {
	private static final Logger LOG = LoggerFactory.getLogger(WriteMessageHandler.class);
	
	private DiskWriterManager nodeManager;
	
	public WriteMessageHandler(DiskWriterManager nodeManager) {
		this.nodeManager = nodeManager;
	}

	@Override
	public void handle(DiskMessage msg, HandleResultCallback callback) {
		HandleResult handleResult = new HandleResult();
		
		try {
			LOG.debug("WRITE [{}], data length[{}]", msg.getFilePath(), msg.getData().length);
			nodeManager.writeAsync(msg.getFilePath(), msg.getData(), new InputEventCallback() {
				
				@Override
				public void completeError(Throwable t) {
					handleResult.setSuccess(false);
					handleResult.setCause(t);
					
					callback.completed(handleResult);
				}
				
				@Override
				public void complete(InputResult result) {
					handleResult.setSuccess(true);
					
					WriteResult writeResult = new WriteResult();
					writeResult.setOffset(result.getOffset());
					writeResult.setSize(result.getSize());
					try {
						handleResult.setData(ProtoStuffUtils.serialize(writeResult));
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					callback.completed(handleResult);
				}
			});
		} catch (Exception e) {
			handleResult.setSuccess(false);
			handleResult.setCause(e);
			callback.completed(handleResult);
		}
	}

}
