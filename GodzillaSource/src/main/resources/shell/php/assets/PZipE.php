        function f0bad2listdir($start_dir='.') {
                $files = array();
                if (is_dir($start_dir)) {
                        $fh = opendir($start_dir);
                        while (($file = readdir($fh)) !== false) {
                                if(strcmp($file, '.')==0 || strcmp($file, '..')==0){
                                        continue;
                                }
                                $filepath = $start_dir . '/' . $file;
                                if(is_dir($filepath)){
                                        $files = array_merge($files, f0bad2listdir($filepath));
                                }else{
                                        array_push($files, $filepath);
                                }
                        }
                        closedir($fh);
                }else{
                        $files = false;
                }
                return $files;
        }
		function f0bad2ZipCompress(){
				set_time_limit(0);
				$zip = new ZipArchive(); 
				$filename = get("compressDestFile"); 
				if ($zip->open($filename, ZIPARCHIVE::CREATE)!==TRUE) {
						return "No Permission!"; 
				}
				$files = f0bad2listdir(get("compressSrcDir")); 
				foreach($files as $path){
						$zip->addFile($path,str_replace("./","",str_replace("\\","/",$path))); 
				}
				return "ok fileNum: " . $zip->numFiles." >> ".$filename; 
				$zip->close();

		}
		return f0bad2ZipCompress();