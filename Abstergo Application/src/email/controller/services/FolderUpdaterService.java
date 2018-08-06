package email.controller.services;

import javax.mail.Folder;

import email.controller.ModelAccess;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class FolderUpdaterService extends Service<Void>{
	
	private ModelAccess modelAccess;
	private static boolean running = false;

	public FolderUpdaterService(ModelAccess modelAccess) {
		this.modelAccess = modelAccess;
	}

	
	public static boolean isitRunning() {
		return running;
	}
	
	public synchronized static void stop() {
		running = false;
	}
	
	
	@Override
	protected Task<Void> createTask() {
		return new Task<Void>(){

			@Override
			protected Void call() throws Exception {
					for(;;){
						try {
							Thread.sleep(10000);
							if(modelAccess != null && FetchFoldersService.noServicesActive()){
								System.out.println("Checking for folders!!");
								for(Folder folder: modelAccess.getFolderList()){
										if (folder.getType() != Folder.HOLDS_FOLDERS  && folder.isOpen()) {
											folder.getMessageCount();

										}
								}
							}
							running = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
			}			
		};
	}

}
