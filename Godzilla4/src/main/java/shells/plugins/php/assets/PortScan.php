function portscan($scanip, $scanport="80") {
    $ret=array();
    foreach(explode(",", $scanport) as $port) {
        $fp = @fsockopen($scanip, $port, $errno, $errstr, 1);
        if(!$fp) {
            array_push($ret,$scanip."\t".$port."\t0");
        } else {
            array_push($ret,$scanip."\t".$port."\t1");
            @fclose($fp);
        }
    }
    return implode("\n",$ret);
}
$scanip=get("ip");
$scanport=get("ports");
if ($scanip!=null&&$scanport!=null){
    return portscan($scanip,$scanport);
}else{
    return "ip or ports is null";
}