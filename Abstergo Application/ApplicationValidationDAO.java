;
	}

	protected static Map<String, String> getFileHashMap() {
		check(new File("src"));
//		System.out.println("SIZE: " + fileHashArray.size());
		return fileHashMap;
	}

	private static void check(File file) {
		if (file != null) {
			for (File descendants : file.listFiles(new FilenameFilter() { // Hashing everything except resource folder

				@Override
				public boolean accept(File dir, String name) {
					if (name.equals("resource")) {
						return false;
					} else {
						return true;
					}
				}

			})) {
				if (descendants.isDirectory()) {
					check(descendants);
				} else if (descendants.isFile()) {
					FileHash fileHash = new FileHash(descendants.getName(), Transcation.generateSHA(descendants));
//					System.out.println(fileHash.getFileName() + ", \n\t" + fileHash.getFileSHA1());
					fileHashMap.put(fileHash.getFileName(), fileHash.getFileSHA1());
				}
			}
		}
	}

	public static void main(String[] arg0) {
		updateFileHashes(ApplicationValidationDAO.getFileHashMap());
	}
}


